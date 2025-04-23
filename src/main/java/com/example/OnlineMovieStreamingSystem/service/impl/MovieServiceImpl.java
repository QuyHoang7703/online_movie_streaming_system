package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.*;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.ActorRepository;
import com.example.OnlineMovieStreamingSystem.repository.GenreRepository;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.SubscriptionPlanRepository;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            List<MovieActorResponseDTO> movieActorResponseDTOS = movie.getMovieActors().stream()
                    .map(this::convertToMovieActorDTO)
                    .toList();

            dto.setMovieActors(movieActorResponseDTOS);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DTO", e);
        }
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

}
