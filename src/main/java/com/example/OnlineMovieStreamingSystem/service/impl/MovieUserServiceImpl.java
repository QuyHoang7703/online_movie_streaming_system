package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.client.RecommendationClient;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.PlanDuration;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionOrder;
import com.example.OnlineMovieStreamingSystem.domain.SubscriptionPlan;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.recommendMovie.RecommendationMovieRequest;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie.RecommendationMovieResponse;
import com.example.OnlineMovieStreamingSystem.dto.response.recommendMovie.RecommendationResponseWrapper;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionOrderRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.MovieUserService;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionPlanService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.constant.SubscriptionOrderStatus;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MovieUserServiceImpl implements MovieUserService {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final RecommendationClient recommendationClient;
    private final MovieService movieService;

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
        RecommendationResponseWrapper recommendationResponseWrapper = this.recommendationClient.getRecommendationResponse(recommendationMovieRequest);
        if(recommendationResponseWrapper != null) {
            List<RecommendationMovieResponse> recommendationMovieResponses = recommendationResponseWrapper.getData().getRecommendations();
            List<Long> movieIds = recommendationMovieResponses.stream().map(RecommendationMovieResponse::getId).toList();
            List<Movie> movies = this.movieRepository.findByIdIn(movieIds);

            List<MovieUserResponseDTO> movieUserResponseDTOS = movies.stream().map(this.movieService::convertToMovieUserResponseDTO).toList();

            return movieUserResponseDTOS;

        }
        return null;
    }


}
