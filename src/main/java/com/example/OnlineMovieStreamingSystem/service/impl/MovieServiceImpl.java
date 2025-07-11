package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.*;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.movie.MovieRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.country.CountryResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieSummaryResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.subscriptionPlan.SubscriptionPlanSummaryDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.videoVersion.VideoVersionDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.*;
import com.example.OnlineMovieStreamingSystem.service.*;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.constant.MovieType;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
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
    private final CountryRepository countryRepository;
    private final CountryService countryService;
    private final VideoVersionService videoVersionService;
    private final UserRepository userRepository;
    private final FavoriteMovieRepository favoriteMovieRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
//    private final String MOVIE_PREFIX = "movie:";
    private final MovieRedisService movieRedisService;

    @Override
    public Movie createMovieFromDTO(MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movie = new Movie();
        // Set attributes for movie
        movie.setTitle(movieRequestDTO.getTitle());
        movie.setOriginalTitle(movieRequestDTO.getOriginalTitle());
        movie.setDescription(movieRequestDTO.getDescription());
        movie.setDirector(movieRequestDTO.getDirector());
        movie.setReleaseDate(movieRequestDTO.getReleaseDate());
        movie.setFree(movieRequestDTO.getFree());
        movie.setTrailerUrl(movieRequestDTO.getTrailerUrl());
        movie.setMovieType(movieRequestDTO.getMovieType());
        movie.setStatus(movieRequestDTO.getStatus());
        movie.setVoteAverage(movieRequestDTO.getVoteAverage());
        movie.setVoteCount(movieRequestDTO.getVoteCount());
        movie.setQuality(movieRequestDTO.getQuality());

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
        if(!movieRequestDTO.getFree() && subscriptionPlanIds != null && !subscriptionPlanIds.isEmpty()) {
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

        // Set countries for movie
        List<String> countryIds = movieRequestDTO.getCountryIds();
        if(countryIds != null && !countryIds.isEmpty()) {
            List<Country> countries = this.countryRepository.findByIdIn(countryIds);
            movie.setCountries(countries);
        }

        return movie;
    }


    @Override
    public <T extends MovieResponseDTO> T convertToMovieInfoDTO(Movie movie, Class<T> clazz) {
        try {
            T dto = clazz.getDeclaredConstructor().newInstance();
            dto.setId(movie.getId());
            dto.setTitle(movie.getTitle());
            dto.setOriginalTitle(movie.getOriginalTitle());
            dto.setDescription(movie.getDescription());
            dto.setDirector(movie.getDirector());
            dto.setPosterUrl(movie.getPosterUrl());
            dto.setBackdropUrl(movie.getBackdropUrl());
            dto.setReleaseDate(movie.getReleaseDate());
            dto.setFree(movie.isFree());
            dto.setTrailerUrl(movie.getTrailerUrl());
            dto.setMovieType(movie.getMovieType());
            dto.setStatus(movie.getStatus());
            dto.setVoteAverage(movie.getVoteAverage());
            dto.setVoteCount(movie.getVoteCount());
            dto.setTmdbId(movie.getTmdbId());
            dto.setQuality(movie.getQuality());
            dto.setCreateAt(movie.getCreateAt());
            dto.setUpdateAt(movie.getUpdateAt());

            if(movie.getVideoVersions() != null && !movie.getVideoVersions().isEmpty()) {
                VideoVersion videoVersion = movie.getVideoVersions().get(0);
                if(videoVersion.getEpisodes() != null && !videoVersion.getEpisodes().isEmpty()) {
                    dto.setDuration(movie.getVideoVersions().get(0).getEpisodes().get(0).getDuration());
                }
            }

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

            if(movie.getCountries() != null && !movie.getCountries().isEmpty()) {
                List<CountryResponseDTO> countryResponseDTOS = movie.getCountries().stream()
                        .map(this.countryService::convertToCountryResponseDTO)
                        .toList();
                dto.setCountries(countryResponseDTOS);
            }

            // Set favorite movie
            String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");
            if(!"anonymousUser".equals(email)) {
                User user = this.userRepository.findByEmail(email)
                        .orElseThrow(() -> new ApplicationException("Không tồn tại user với email là " + email));
                boolean isFavoriteMovie = this.favoriteMovieRepository.existsByUserIdAndMovieId(user.getId(), movie.getId());
                dto.setFavorite(isFavoriteMovie);
            }

            return dto;
        } catch (Exception e) {
            System.out.println("Error convert to dto: " +e);
            throw new RuntimeException("Failed to convert DTO", e);
        }
    }

    public ResultPaginationDTO getMoviesForUser(String title,
                                               List<String> genreNames,
                                               String movieType,
                                               List<String> countries,
                                               int page,
                                               int size) throws BadRequestException {
        Function<Movie, MovieUserResponseDTO> userMapper = (movie) -> this.convertToMovieUserResponseDTO(movie);
        return getMovies(title, genreNames, movieType, countries, null, page, size, userMapper, false);
    }


    @Override
    public ResultPaginationDTO getMoviesForAdmin(String title,
                                               List<String> genreNames,
                                               String movieType,
                                               List<String> countries,
                                                 Long subscriptionPlanId,
                                               int page,
                                               int size) throws BadRequestException {
        Function<Movie, MovieSummaryResponseDTO> adminMapper = (movie) -> this.convertToMovieSummaryResponseDTO(movie);
        return getMovies(title, genreNames, movieType, countries, subscriptionPlanId, page, size, adminMapper, true);
    }


    @Override
    public List<String> getAllCountriesOfMovie() {
        return null;
//        return this.movieRepository.getAllCountriesOfMovies();
    }

    @Transactional
    @Override
    public Movie updateMovieFromDTO(long movieId, MovieRequestDTO movieRequestDTO, MultipartFile poster, MultipartFile backdrop) throws IOException {
        Movie movieDB = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim"));
        String newTitle = movieRequestDTO.getTitle();
        if (newTitle != null && !Objects.equals(newTitle, movieDB.getTitle())) {
            movieDB.setTitle(newTitle);
        }

        String newDescription = movieRequestDTO.getDescription();
        if (newDescription != null && !Objects.equals(newDescription, movieDB.getDescription())) {
            movieDB.setDescription(newDescription);
        }

        String newDirector = movieRequestDTO.getDirector();
        if (newDirector != null && !Objects.equals(newDirector, movieDB.getDirector())) {
            movieDB.setDirector(newDirector);
        }

        LocalDate newReleaseDate = movieRequestDTO.getReleaseDate();
        if (newReleaseDate != null && !Objects.equals(newReleaseDate, movieDB.getReleaseDate())) {
            movieDB.setReleaseDate(newReleaseDate);
        }

        Boolean newIsFree = movieRequestDTO.getFree();
        if (newIsFree != null && !Objects.equals(newIsFree, movieDB.isFree())) {
            movieDB.setFree(newIsFree);
        }

        String newTrailerUrl = movieRequestDTO.getTrailerUrl();
        if (newTrailerUrl != null && !Objects.equals(newTrailerUrl, movieDB.getTrailerUrl())) {
            movieDB.setTrailerUrl(newTrailerUrl);
        }

        String newQuality = movieRequestDTO.getQuality();
        if (newQuality != null && !Objects.equals(newQuality, movieDB.getQuality())) {
            movieDB.setQuality(newQuality);
        }

        String newStatus = movieRequestDTO.getStatus();
        if (newStatus != null && !Objects.equals(newStatus, movieDB.getStatus())) {
            movieDB.setStatus(newStatus);
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

        // Update countries for movie
        if(movieRequestDTO.getCountryIds() != null) {
            List<String> currentCountryIds = movieDB.getCountries().stream().map(Country::getId).toList();
            List<String> updateCountryIds = movieRequestDTO.getCountryIds();
            if(!currentCountryIds.equals(updateCountryIds)) {
                List<Country> countries = this.countryRepository.findByIdIn(updateCountryIds);
                movieDB.setCountries(countries);
            }
        }

        // Update subscription plan for movie
        if(movieRequestDTO.getSubscriptionPlanIds() != null && !movieRequestDTO.getSubscriptionPlanIds().isEmpty()) {
            List<Long> currentSubscriptionPlanIds = movieDB.getSubscriptionPlans().stream().map(SubscriptionPlan::getId).toList();
            List<Long> updateSubscriptionPlanIds = movieRequestDTO.getSubscriptionPlanIds();
            if(!currentSubscriptionPlanIds.equals(updateSubscriptionPlanIds)) {
                List<SubscriptionPlan> subscriptionPlans = this.subscriptionPlanRepository.findByIdIn(updateSubscriptionPlanIds);
                movieDB.setSubscriptionPlans(subscriptionPlans);
            }
            movieDB.setFree(false);
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
                .orElseThrow(() -> new ApplicationException("Không tồn tại phim với id " + movieId));
        this.movieRepository.delete(movie);
        this.movieRedisService.clearMovieInRedis();
    }

    @Override
    public MovieUserResponseDTO convertToMovieUserResponseDTO(Movie movie) {

        MovieUserResponseDTO movieUserResponseDTO = MovieUserResponseDTO.builder()
                .movieId(movie.getId())
                .movieType(movie.getMovieType())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .voteAverage(movie.getVoteAverage())
                .year(movie.getReleaseDate().getYear())
                .build();

        if(movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            List<GenreSummaryDTO> genreSummaryDTOS = movie.getGenres().stream()
                    .map(this.genreService::convertToGenreSummaryDTO)
                    .toList();
            movieUserResponseDTO.setGenres(genreSummaryDTOS);
        }

        if(movie.getVideoVersions() != null && !movie.getVideoVersions().isEmpty()) {
            List<VideoVersion> videoVersions = movie.getVideoVersions();
            List<VideoVersionDetailResponseDTO> videoVersionDetailResponseDTOS = videoVersions.stream()
                    .map(this.videoVersionService::convertToVideoVersionDetailResponseDTO)
                    .toList();
            movieUserResponseDTO.setVideoVersions(videoVersionDetailResponseDTOS);
            movieUserResponseDTO.setDuration(videoVersions.get(0).getEpisodes().get(0).getDuration());
        }

        // Set attribute for standaloneMovie
        if(movie.getMovieType() == MovieType.STANDALONE) {
            if(movie.getVideoVersions() != null && !movie.getVideoVersions().isEmpty()) {
                movieUserResponseDTO.setDuration(movie.getVideoVersions().get(0).getEpisodes().get(0).getDuration());
            }
        }

        // Set attribute for seriesMovie
        if(movie.getMovieType() == MovieType.SERIES) {
            movieUserResponseDTO.setSeason(movie.getSeriesMovie().getSeason());
            movieUserResponseDTO.setTotalEpisodes(movie.getSeriesMovie().getTotalEpisodes());
        }

        if(movie.getSubscriptionPlans() != null && !movie.getSubscriptionPlans().isEmpty()) {
            List<SubscriptionPlanSummaryDTO> subscriptionPlanSummaryDTOS = movie.getSubscriptionPlans().stream()
                    .map(this.subscriptionPlanService::convertToSubscriptionPlanSummaryDTO)
                    .toList();
            movieUserResponseDTO.setSubscriptionPlans(subscriptionPlanSummaryDTOS);
        }

        // Set favorite movie
        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");
        if(!"anonymousUser".equals(email)) {
            User user = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new ApplicationException("Không tồn tại user với email là " + email));
            boolean isFavoriteMovie = this.favoriteMovieRepository.existsByUserIdAndMovieId(user.getId(), movie.getId());
            movieUserResponseDTO.setFavorite(isFavoriteMovie);
        }

        return movieUserResponseDTO;
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

    private ResultPaginationDTO getMovies(String title,
                                          List<String> genreNames,
                                          String movieType,
                                          List<String> countries,
                                          Long subscriptionPlanId,
                                          int page,
                                          int size, Function<Movie, ?> movieMapper,
                                          boolean isAdmin) throws BadRequestException {
        MovieType type = null;

        if (movieType != null && !movieType.isEmpty()) {
            try {
                type = MovieType.valueOf(movieType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bắt lỗi nếu truyền sai enum → trả lỗi cho FE hoặc set null tùy logic
                throw new BadRequestException("Movie type không hợp lệ: " + movieType);
            }
        }

        // Key được tạo từ filters
        String key = this.movieRedisService.getKey(genreNames, movieType, countries, subscriptionPlanId, page, size, isAdmin);

        // Nếu có data movie trong redis thì lấy trong redis theo key
        Object cached = this.redisTemplate.opsForValue().get(key);
        if(cached != null) {
            return this.objectMapper.convertValue(cached, ResultPaginationDTO.class);
        }


        if(movieType != null && !movieType.isEmpty()) {
            type = MovieType.valueOf(movieType.toUpperCase());
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "releaseDate");
        Page<Movie> moviePage = null;
        if (subscriptionPlanId != null) {
            // Lấy tất cả các gói liên quan (bao gồm gói cha và tất cả các gói con)
            Set<SubscriptionPlan> allRelevantPlans = new HashSet<>();
            SubscriptionPlan mainPlan = subscriptionPlanRepository.findById(subscriptionPlanId)
                    .orElseThrow(() -> new ApplicationException("Subscription Plan không tồn tại."));
            allRelevantPlans.add(mainPlan);
            findAllChildPlans(mainPlan, allRelevantPlans);

            List<Long> planIds = allRelevantPlans.stream()
                    .map(SubscriptionPlan::getId)
                    .toList();

            // Truy vấn phim dựa trên các tiêu chí lọc VÀ các gói đăng ký liên quan
            // Bạn cần một phương thức mới trong movieRepository để xử lý điều này
            moviePage = this.movieRepository.findMoviesByFilterAndSubscriptionPlans(
                    title, genreNames, type, countries, planIds, pageable);

        } else {
            // Nếu không có subscriptionPlanId, lọc như bình thường (không theo gói)
            moviePage = this.movieRepository.findMoviesByFilter(title, genreNames, type, countries, pageable);
        }


        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(moviePage.getTotalPages());
        meta.setTotalElements(moviePage.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);


        List<?> mappedResults  = moviePage.getContent().stream()
                .map(movieMapper)
                .toList();

        resultPaginationDTO.setResult(mappedResults);

        redisTemplate.opsForValue().set(key, resultPaginationDTO, Duration.ofMinutes(5));

        return resultPaginationDTO;
    }


    private void findAllChildPlans(SubscriptionPlan parentSubscriptionPlan, Set<SubscriptionPlan> collectedPlans ) {
        if (parentSubscriptionPlan.getChildPlans() != null && !parentSubscriptionPlan.getChildPlans().isEmpty()) {
            for (SubscriptionPlan child : parentSubscriptionPlan.getChildPlans()) {
                if (collectedPlans.add(child)) { // Thêm nếu chưa có và tránh vòng lặp vô hạn
                    findAllChildPlans(child, collectedPlans);
                }
            }
        }

    }









}
