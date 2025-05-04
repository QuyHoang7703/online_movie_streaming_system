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
import com.example.OnlineMovieStreamingSystem.service.GenreService;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.service.SubscriptionPlanService;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
    private final GenreService genreService;
    private final SubscriptionPlanService subscriptionPlanService;

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

        String posterUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, poster.getOriginalFilename(), poster.getInputStream());
        String backdropUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, backdrop.getOriginalFilename(), backdrop.getInputStream());

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

            if(movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                List<GenreSummaryDTO> genreSummaryDTOS = movie.getGenres().stream()
                        .map(this.genreService::convertToGenreSummaryDTO)
                        .toList();
                dto.setGenres(genreSummaryDTOS);
            }

            if(movie.getSubscriptionPlans() != null && !movie.getSubscriptionPlans().isEmpty()) {
                List<SubscriptionPlanSummaryDTO> subscriptionPlanSummaryDTOS = movie.getSubscriptionPlans().stream()
                        .map(this.subscriptionPlanService::convertToSubscriptionPlanSummaryDTO)
                        .toList();
                dto.setSubscriptionPlans(subscriptionPlanSummaryDTOS);
            }


            return dto;
        } catch (Exception e) {
            System.out.println("Error convert to dto: " +e);
            throw new RuntimeException("Failed to convert DTO", e);
        }
    }

    @Override
    public ResultPaginationDTO getMovies(String title,
                                         List<String> genreNames,
                                         String movieType,
                                         List<String> countries,
                                         int page,
                                         int size) throws BadRequestException {
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
        Page<Movie> moviePage = this.movieRepository.findMoviesByFilter(title, genreNames, type, countries, pageable);

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

    @Override
    public List<String> getAllCountriesOfMovie() {
        return this.movieRepository.getAllCountriesOfMovies();
    }

    @Transactional
    @Override
    public Movie updateMovieFromDTO(long movieId, MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movieDB = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        if(!Objects.equals(movieRequestDTO.getTitle(), movieDB.getTitle())) {
            movieDB.setTitle(movieRequestDTO.getTitle());
        }
        if(!Objects.equals(movieRequestDTO.getDescription(), movieDB.getDescription())) {
            movieDB.setDescription(movieRequestDTO.getDescription());
        }
        if(!Objects.equals(movieRequestDTO.getDirector(), movieDB.getDirector())) {
            movieDB.setDirector(movieRequestDTO.getDirector());
        }
        if(!Objects.equals(movieRequestDTO.getCountry(), movieDB.getCountry())) {
            movieDB.setCountry(movieRequestDTO.getCountry());
        }
        if(!Objects.equals(movieRequestDTO.getReleaseDate(), movieDB.getReleaseDate())) {
            movieDB.setReleaseDate(movieRequestDTO.getReleaseDate());
        }
        if(!Objects.equals(movieRequestDTO.isFree(), movieDB.isFree())) {
            movieDB.setFree(movieRequestDTO.isFree());
        }
        if(!Objects.equals(movieRequestDTO.getTrailerUrl(), movieDB.getTrailerUrl())) {
            movieDB.setTrailerUrl(movieRequestDTO.getTrailerUrl());
        }

        // Update genre for movie
        if(movieRequestDTO.getGenreIds() != null) {
            List<Long> currentGenreIds = movieDB.getGenres().stream().map(Genre::getId).toList();
            List<Long> updateGenreIds = movieRequestDTO.getGenreIds();
            if(!currentGenreIds.equals(updateGenreIds)) {
                List<Genre> genres = this.genreRepository.findByIdIn(updateGenreIds);
                movieDB.setGenres(genres);
            }
        }

        // Update subscription plan for movie
        if(movieRequestDTO.getSubscriptionPlanIds() != null) {
            List<Long> currentSubscriptionPlanIds = movieDB.getSubscriptionPlans().stream().map(SubscriptionPlan::getId).toList();
            List<Long> updateSubscriptionPlanIds = movieRequestDTO.getSubscriptionPlanIds();
            if(!currentSubscriptionPlanIds.equals(updateSubscriptionPlanIds)) {
                List<SubscriptionPlan> subscriptionPlans = this.subscriptionPlanRepository.findByIdIn(updateSubscriptionPlanIds);
                movieDB.setSubscriptionPlans(subscriptionPlans);
            }
        }

        // Update actor for movie
        if(movieRequestDTO.getMovieActors() != null) {
          Map<Long, MovieActorRequestDTO> movieActorRequestDTOMap = movieRequestDTO.getMovieActors().stream()
                  .collect(Collectors.toMap(MovieActorRequestDTO::getActorId, movieActorRequestDTO -> movieActorRequestDTO));

          Set<Long> requestActorIds  = movieRequestDTO.getMovieActors().stream().map(MovieActorRequestDTO::getActorId).collect(Collectors.toSet());

          List<MovieActor> movieActors = movieDB.getMovieActors();

          List<MovieActor> updateMovieActors = new ArrayList<>();

          // Update existed movieActor or unchanged movieActor
          for(MovieActor movieActor : movieActors) {
              long actorId = movieActor.getActor().getId();
              if(requestActorIds.contains(actorId)) {
                 if (!Objects.equals(movieActor.getCharacterName(), movieActorRequestDTOMap.get(actorId).getCharacterName()))  {
                     movieActor.setCharacterName(movieActorRequestDTOMap.get(actorId).getCharacterName());
                 }
                 updateMovieActors.add(movieActor);
             }
              requestActorIds.remove(actorId);

          }
          // Add new movieActor
          for(Long newActorId : requestActorIds) {
              Actor actor = this.actorRepository.findById(newActorId).
                      orElseThrow(() -> new ApplicationException("Không tìm thấy diễn viên"));
              MovieActor movieActor = new MovieActor();
              movieActor.setActor(actor);
              movieActor.setCharacterName(movieActorRequestDTOMap.get(newActorId).getCharacterName());
              movieActor.setMovie(movieDB);
              updateMovieActors.add(movieActor);
          }

          movieDB.getMovieActors().clear();
          movieDB.getMovieActors().addAll(updateMovieActors);

        }

        if(poster!=null) {
            log.info("Có poster mới: " + poster.getOriginalFilename());
            String newPosterUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, poster.getOriginalFilename(), poster.getInputStream());
            if(newPosterUrl!=null) {
                String oldFilenamePoster = movieDB.getPosterUrl().substring(movieDB.getPosterUrl().lastIndexOf("/") +1);
                this.imageStorageService.deleteFile(CONTAINER_NAME, oldFilenamePoster);
                movieDB.setPosterUrl(newPosterUrl);
            }
        }else{
            log.info("ko có  poster mới: ");
        }
        if(backdrop!=null) {
            String newBackdropUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, backdrop.getOriginalFilename(), backdrop.getInputStream());
            if(newBackdropUrl!=null) {
                String oldFilenameBackdrop = movieDB.getBackdropUrl().substring(movieDB.getBackdropUrl().lastIndexOf("/") +1);
                this.imageStorageService.deleteFile(CONTAINER_NAME, oldFilenameBackdrop);
                movieDB.setBackdropUrl(newBackdropUrl);
            }
        }


        return movieDB;
    }

    @Transactional
    @Override
    public void deleteMovie(long movieId) {
        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        this.movieRepository.delete(movie);
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
        movieSummaryResponseDTO.setReleaseDate(movie.getReleaseDate());

        List<GenreSummaryDTO> genreSummaryDTOS = movie.getGenres().stream()
                .map(this.genreService::convertToGenreSummaryDTO).toList();

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
