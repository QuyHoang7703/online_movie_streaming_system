package com.example.OnlineMovieStreamingSystem.service.impl;


import com.example.OnlineMovieStreamingSystem.domain.FavoriteMovie;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.FavoriteMovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.FavoriteMovieService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteMovieServiceImpl implements FavoriteMovieService {
    private final FavoriteMovieRepository favoriteMovieRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    @Override
    public void addFavoriteMovie(long movieId) {
        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");

        if ("anonymousUser".equals(email)) {
            throw new ApplicationException("Bạn phải đăng nhập để sử dụng tính năng này");
        }

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy user vơi email là " + email));

        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim với id là " + movieId));

        boolean existingFavoriteMovie = this.favoriteMovieRepository.existsByUserIdAndMovieId(user.getId(), movieId);
        if(existingFavoriteMovie) {
            throw new ApplicationException("Bạn đã thêm phim này vào danh sách yêu thích rồi");
        }

        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.setUser(user);
        favoriteMovie.setMovie(movie);

        this.favoriteMovieRepository.save(favoriteMovie);

    }

    @Transactional
    @Override
    public void removeFavoriteMovie(long movieId) {
        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");

        if ("anonymousUser".equals(email)) {
            throw new ApplicationException("Bạn phải đăng nhập để sử dụng tính năng này");
        }

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy user với email là " + email));

        boolean existingFavoriteMovie = this.favoriteMovieRepository.existsByUserIdAndMovieId(user.getId(), movieId);
        if(!existingFavoriteMovie) {
            throw new ApplicationException("Bạn đã xóa phim này ra khỏi danh sách yêu thích rồi");
        }

        this.favoriteMovieRepository.deleteByUser_IdAndMovie_Id(user.getId(), movieId);

    }

    @Override
    public ResultPaginationDTO getFavoriteMovies(int page, int size) {
        String email = SecurityUtil.getCurrentLogin().orElse("anonymousUser");

        if ("anonymousUser".equals(email)) {
            throw new ApplicationException("Bạn phải đăng nhập để sử dụng tính năng này");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FavoriteMovie> favoriteMoviePage = this.favoriteMovieRepository.getFavoriteMovieByEmail(email, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(favoriteMoviePage.getTotalPages());
        meta.setTotalElements(favoriteMoviePage.getTotalElements());

        List<MovieUserResponseDTO> movieUserResponseDTOS = favoriteMoviePage.getContent().stream()
                .map(favoriteMovie -> this.movieService.convertToMovieUserResponseDTO(favoriteMovie.getMovie()))
                .toList();

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(movieUserResponseDTOS);

        return resultPaginationDTO;
    }
}
