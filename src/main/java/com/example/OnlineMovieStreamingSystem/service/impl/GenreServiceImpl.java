package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Genre;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.genre.GenreRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreSummaryDTO;
import com.example.OnlineMovieStreamingSystem.repository.GenreRepository;
import com.example.OnlineMovieStreamingSystem.service.GenreService;
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
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public GenreResponseDTO createGenre(GenreRequestDTO genreRequestDTO) {
        boolean isExistedGenre = this.genreRepository.existsByName(genreRequestDTO.getName());
        if(isExistedGenre) {
            throw new ApplicationException("Genre name is existed");
        }
        Genre genre = new Genre();
        genre.setName(genreRequestDTO.getName());
        genre.setDescription(genreRequestDTO.getDescription());

        Genre savedGenre = this.genreRepository.save(genre);

        return this.convertToGenreDTO(savedGenre);

    }

    @Override
    public GenreResponseDTO updateGenre(long id, GenreRequestDTO genreRequestDTO) {
//        boolean isExistedGenre = this.genreRepository.existsByName(genreRequestDTO.getName());
//        if(isExistedGenre) {
//            throw new ApplicationException("Genre name is existed");
//        }
        Genre genre = this.findGenreById(id);
        genre.setName(genreRequestDTO.getName());
        genre.setDescription(genreRequestDTO.getDescription());
        Genre updatedGenre = this.genreRepository.save(genre);

        return this.convertToGenreDTO(updatedGenre);
    }

    @Override
    public ResultPaginationDTO getAllGenres(String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Genre> genrePage = this.genreRepository.findAll(genre, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(genrePage.getTotalPages());
        meta.setTotalElements(genrePage.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);

        List<GenreResponseDTO> genreResponseDTOS = genrePage.getContent().stream()
                .map(this::convertToGenreDTO)
                .toList();
        resultPaginationDTO.setResult(genreResponseDTOS);

        return resultPaginationDTO;
    }

    @Transactional
    @Override
    public void deleteGenre(long id) {
        Genre genre = this.findGenreById(id);
        for(Movie movie : genre.getMovies()) {
            movie.getGenres().remove(genre);
        }
        // Khi xóa genre thì tự động jpa xóa luôn record của movie_genre luôn
        this.genreRepository.delete(genre);
    }

    @Override
    public GenreResponseDTO getGenreById(long id) {
        Genre genre = this.findGenreById(id);
        return this.convertToGenreDTO(genre);
    }

    @Override
    public List<GenreSummaryDTO> getGenreSummaryDTOs() {
        List<Genre> genres = this.genreRepository.findAll();

        List<GenreSummaryDTO> genreSummaryDTOS = genres.stream()
                .map(this::convertToGenreSummaryDTO)
                .toList();

        return genreSummaryDTOS;
    }

    private GenreResponseDTO convertToGenreDTO(Genre genre) {
        GenreResponseDTO genreResponseDTO = GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();

        return genreResponseDTO;
    }

    public GenreSummaryDTO convertToGenreSummaryDTO(Genre genre) {
        GenreSummaryDTO genreSummaryDTO = GenreSummaryDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();

        return genreSummaryDTO;
    }

    private Genre findGenreById(long id) {
        return this.genreRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Genre not found"));
    }




}
