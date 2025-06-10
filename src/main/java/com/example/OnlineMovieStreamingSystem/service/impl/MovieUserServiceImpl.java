package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.client.RecommendationClient;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionOrder;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.RecommendationMovieRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie.RecommendationMovieResponse;
import com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie.RecommendationResponseWrapper;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionOrderRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserInteractionRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.*;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.constant.SubscriptionOrderStatus;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieUserServiceImpl implements MovieUserService {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final RecommendationClient recommendationClient;
    private final MovieService movieService;
    private final UserInteractionRepository interactionRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final StandaloneMovieService standaloneMovieService;
    private final SeriesMovieService seriesMovieService;

    @Override
    public boolean canUserWatchMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id là " + movieId));

        if(movie.isFree()){
            return true;
        }

        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");
        if ("anonymousUser".equals(email)) {
           return false;
        }

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tồn tại user với email: " + email));

        List<SubscriptionPlan> subscriptionPlans = movie.getSubscriptionPlans();

        Set<SubscriptionPlan> parentOfSubscriptionPlans = subscriptionPlans.stream()
                .flatMap(subscriptionPlan -> subscriptionPlan.getParentPlans().stream())
                .collect(Collectors.toSet());

        List<SubscriptionPlan> allSubscriptionPlans = Stream.concat(subscriptionPlans.stream(), parentOfSubscriptionPlans.stream()).toList();

        List<PlanDuration> validDurations = allSubscriptionPlans.stream()
                .flatMap(subscriptionPlan -> subscriptionPlan.getPlanDurations().stream())
                .toList();

        List<Long> validDurationsIds = validDurations.stream().map(PlanDuration::getId).toList();

        if (validDurationsIds.isEmpty()) return false;

        return this.subscriptionOrderRepository
                .existsByUserIdAndPlanDuration_IdInAndStatusAndEndDateAfter(
                        user.getId(),
                        validDurationsIds,
                        SubscriptionOrderStatus.ACTIVE,
                        LocalDate.now()
                );
    }

    @Override
    public List<SubscriptionPlanResponseDTO> getSubscriptionPlansForMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id là " + movieId));
        List<SubscriptionPlan> subscriptionPlans = movie.getSubscriptionPlans();
        List<SubscriptionPlanResponseDTO> subscriptionPlanResponseDTOS = subscriptionPlans.stream()
                .map(this.subscriptionPlanService::convertToSubscriptionPlanResponseDTO)
                .toList();

        return subscriptionPlanResponseDTOS;
    }

    @Override
    public List<MovieUserResponseDTO> getRecommendationsForMovie(RecommendationMovieRequest recommendationMovieRequest) {

        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");

        long countRatingsOfUser = userInteractionRepository.countRatingsOfUser(recommendationMovieRequest.getUser_id());


        RecommendationResponseWrapper recommendationResponseWrapper = null;

        if(!email.equals("anonymousUser")) {
            double cbfWeight = 0.0;
            double neumfWeight = 0.0;

            if(countRatingsOfUser == 0) {
                cbfWeight = 1.0;
            } else if (countRatingsOfUser < 10) {
                cbfWeight = 0.7;
                neumfWeight = 0.3;
            } else if (countRatingsOfUser < 50) {
                cbfWeight = 0.6;
                neumfWeight = 0.4;
            }else{
                cbfWeight = 0.3;
                neumfWeight = 0.7;
            }

            recommendationMovieRequest.setCbf_weight(cbfWeight);
            recommendationMovieRequest.setNeumf_weight(neumfWeight);

            recommendationResponseWrapper = this.recommendationClient.getRecommendationHybridResponse(recommendationMovieRequest);
        }else {
            recommendationResponseWrapper = this.recommendationClient.getRecommendationCBFResponse(recommendationMovieRequest);
        }
        if(recommendationResponseWrapper != null) {

            List<RecommendationMovieResponse> recommendationMovieResponses = recommendationResponseWrapper.getData().getRecommendations();

            recommendationMovieResponses.forEach(r -> log.info("CBF Score: {}, NeuMF Score: {}, Hybrid Score: {}, Source: {}, TMDB ID: {} ",
                    r.getCbf_score(), r.getNeumf_score(), r.getHybrid_score(), r.getSource(), r.getTmdb_id()));

            List<Long> movieIds = recommendationMovieResponses.stream().map(RecommendationMovieResponse::getTmdb_id).toList();
            List<Movie> movies = this.movieRepository.findByIdIn(movieIds);
            log.info("Number of movies: " + movies.size());

            List<MovieUserResponseDTO> movieUserResponseDTOS = movies.stream().map(this.movieService::convertToMovieUserResponseDTO).toList();

            return movieUserResponseDTOS;

        }

        return null;
    }

    @Override
    public ResultPaginationDTO getHotMovieByMovieType(MovieType movieType,String countryId, int size) {
        Pageable pageable = PageRequest.of(0, size,
                Sort.by(Sort.Order.desc("releaseDate"),
                        Sort.Order.desc("voteCount"),
                        Sort.Order.desc("voteAverage")));

        Page<Movie> moviePage = this.movieRepository.getHotMoviesByFilter(movieType, countryId, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(moviePage.getTotalPages());
        meta.setTotalElements(moviePage.getTotalElements());

        List<MovieUserResponseDTO> movieUserResponseDTOS = moviePage.getContent().stream()
                .map(this.movieService::convertToMovieUserResponseDTO).toList();

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(movieUserResponseDTOS);

        return resultPaginationDTO;
    }

    @Override
    public ResultPaginationDTO getFeatureMovies(int size) {
        Pageable pageable = PageRequest.of(0, size,
                Sort.by(Sort.Order.desc("releaseDate"),
                        Sort.Order.desc("voteCount"),
                        Sort.Order.desc("voteAverage")));
        Page<Movie> moviePage = this.movieRepository.findAll(pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(moviePage.getTotalPages());
        meta.setTotalElements(moviePage.getTotalElements());

        List<MovieResponseDTO> movieResponseDTOS = new ArrayList<>();

        for(Movie movie : moviePage.getContent()) {
            if(movie.getMovieType() == MovieType.STANDALONE) {
                movieResponseDTOS.add(this.standaloneMovieService.convertToStandaloneMovieResponseDTO(movie));
            }else{
                movieResponseDTOS.add(this.seriesMovieService.convertToSeriesResponseDTO(movie));

            }
        }

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(movieResponseDTOS);

        return resultPaginationDTO;
    }


}
