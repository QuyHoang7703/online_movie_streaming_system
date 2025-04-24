package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.*;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieSummaryResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.repository.ActorRepository;
import com.example.OnlineMovieStreamingSystem.repository.GenreRepository;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionPlanRepository;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final ImageStorageService imageStorageService;
    private final GenreRepository genreRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final String CONTAINER_NAME= "movie-image-container";
    private final ActorRepository actorRepository;

    @Override
    public Movie createMovieFromDTO(MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movie = new Movie();
        // Set attributes for movie
        movie.setTitle(movieRequestDTO.getTitle());
        movie.setDescription(movieRequestDTO.getDescription());
        movie.setDirector(movieRequestDTO.getDirector());
        movie.setCountry(movieRequestDTO.getCountry());
        movie.setReleaseDate(movieRequestDTO.getReleaseDate());
        movie.setFree(movieRequestDTO.isFree());
        movie.setTrailerUrl(movieRequestDTO.getTrailerUrl());
        movie.setMovieType(movieRequestDTO.getMovieType());

        String posterUrl = this.imageStorageService.uploadImage(CONTAINER_NAME, poster.getOriginalFilename(), poster.getInputStream());
        String backdropUrl = this.imageStorageService.uploadImage(CONTAINER_NAME, backdrop.getOriginalFilename(), backdrop.getInputStream());

        movie.setPosterUrl(posterUrl);
        movie.setBackdropUrl(backdropUrl);

        // Set genres for movie
        List<Long> genreIds = movieRequestDTO.getGenreIds();
        if(genreIds != null && !genreIds.isEmpty()) {
            List<Genre> genres = this.genreRepository.findByIdIn(genreIds);
            movie.setGenres(genres);
        }

        // Set subscription plan for movie
        List<Long> subscriptionPlanIds = movieRequestDTO.getSubscriptionPlanIds();
        if(!movieRequestDTO.isFree() && subscriptionPlanIds != null && !subscriptionPlanIds.isEmpty()) {
            List<SubscriptionPlan> subscriptionPlans = this.subscriptionPlanRepository.findByIdIn(subscriptionPlanIds);
            movie.setSubscriptionPlans(subscriptionPlans);
        }

        // Set actors for movie
        List<MovieActorRequestDTO> movieActorDTOS = movieRequestDTO.getMovieActors();
        if(movieActorDTOS != null && !movieActorDTOS.isEmpty()) {
            List<Long> actorIds = movieActorDTOS.stream().map(MovieActorRequestDTO::getActorId).toList();
            List<Actor> actors = this.actorRepository.findByIdIn(actorIds);
            Map<Long, Actor> actorMap = actors.stream().collect(Collectors.toMap(Actor::getId, actor -> actor));
            List<MovieActor> movieActors = movieActorDTOS.stream()
                    .map(movieActorDTO -> {
                        MovieActor movieActor = new MovieActor();
                        movieActor.setMovie(movie);
                        movieActor.setActor(actorMap.get(movieActorDTO.getActorId()));
                        movieActor.setCharacterName(movieActorDTO.getCharacterName());
                        return movieActor;
                    }).toList();
            movie.setMovieActors(movieActors);
        }
        return movie;
    }


    @Override
    public <T extends MovieResponseDTO> T convertToMovieInfoDTO(Movie movie, Class<T> clazz) {
        try {
            T dto = clazz.getDeclaredConstructor().newInstance();
            dto.setId(movie.getId());
            dto.setTitle(movie.getTitle());
            dto.setDescription(movie.getDescription());
            dto.setDirector(movie.getDirector());
            dto.setPosterUrl(movie.getPosterUrl());
            dto.setBackdropUrl(movie.getBackdropUrl());
            dto.setCountry(movie.getCountry());
            dto.setReleaseDate(movie.getReleaseDate());
            dto.setFree(movie.isFree());
            dto.setTrailerUrl(movie.getTrailerUrl());
            dto.setMovieType(movie.getMovieType());
            dto.setCreateAt(movie.getCreateAt());
            dto.setUpdateAt(movie.getUpdateAt());

            if(movie.getMovieActors() != null && !movie.getMovieActors().isEmpty()) {
                List<MovieActorResponseDTO> movieActorResponseDTOS = movie.getMovieActors().stream()
                        .map(this::convertToMovieActorDTO)
                        .toList();

                dto.setMovieActors(movieActorResponseDTOS);
            }


            return dto;
        } catch (Exception e) {
            System.out.println("Error convert to dto: " +e);
            throw new RuntimeException("Failed to convert DTO", e);
        }
    }

    @Override
    public ResultPaginationDTO getMovies(String title, List<String> genreNames, String movieType, int page, int size) throws BadRequestException {
        MovieType type = null;
        if (movieType != null && !movieType.isEmpty()) {
            try {
                type = MovieType.valueOf(movieType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bắt lỗi nếu truyền sai enum → trả lỗi cho FE hoặc set null tùy logic
                throw new BadRequestException("Movie type không hợp lệ: " + movieType);
            }
        }

        if(movieType != null && !movieType.isEmpty()) {
            type = MovieType.valueOf(movieType.toUpperCase());
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Movie> moviePage = this.movieRepository.findMoviesByFilter(title, genreNames, type, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(moviePage.getTotalPages());
        meta.setTotalElements(moviePage.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);

        List<MovieSummaryResponseDTO> movieSummaryResponseDTOS = moviePage.getContent().stream()
                .map(this::convertToMovieSummaryResponseDTO)
                .toList();

        resultPaginationDTO.setResult(movieSummaryResponseDTOS);

        return resultPaginationDTO;
    }

    private MovieActorResponseDTO convertToMovieActorDTO(MovieActor movieActor) {
        Actor actor = movieActor.getActor();
        MovieActorResponseDTO movieActorResponseDTO = MovieActorResponseDTO.builder()
                .actorId(actor.getId())
                .actorName(actor.getName())
                .avatarUrl(actor.getAvatarUrl())
                .characterName(movieActor.getCharacterName())
                .build();

        return movieActorResponseDTO;

    }

    private MovieSummaryResponseDTO convertToMovieSummaryResponseDTO (Movie movie) {
        MovieSummaryResponseDTO movieSummaryResponseDTO = new MovieSummaryResponseDTO();
        movieSummaryResponseDTO.setId(movie.getId());
        movieSummaryResponseDTO.setPosterUrl(movie.getPosterUrl());
        movieSummaryResponseDTO.setTitle(movie.getTitle());
        movieSummaryResponseDTO.setDirector(movie.getDirector());
        movie.setReleaseDate(movie.getReleaseDate());
        movieSummaryResponseDTO.setMovieType(movie.getMovieType());

        List<GenreSummaryDTO> genreSummaryDTOS = movie.getGenres().stream()
                .map(genre -> {
                    GenreSummaryDTO genreSummaryDTO = new GenreSummaryDTO();
                    genreSummaryDTO.setId(genre.getId());
                    genreSummaryDTO.setName(genre.getName());
                    return genreSummaryDTO;
                }).toList();

        List<SubscriptionPlanSummaryDTO> subscriptionPlanSummaryDTOS = movie.getSubscriptionPlans().stream()
                .map(subscriptionPlan -> {
                    SubscriptionPlanSummaryDTO subscriptionPlanSummaryDTO = new SubscriptionPlanSummaryDTO();
                    subscriptionPlanSummaryDTO.setId(subscriptionPlan.getId());
                    subscriptionPlanSummaryDTO.setName(subscriptionPlan.getName());
                    return subscriptionPlanSummaryDTO;
                }).toList();

        movieSummaryResponseDTO.setGenres(genreSummaryDTOS);
        movieSummaryResponseDTO.setSubscriptionPlans(subscriptionPlanSummaryDTOS);

        return movieSummaryResponseDTO;
    }



}
