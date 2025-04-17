package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Genre;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.genre.GenreRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.genre.GenreResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.GenreRepository;
import com.example.OnlineMovieStreamingSystem.service.GenreService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public GenreResponseDTO createGenre(GenreRequestDTO genreRequestDTO) {
        Genre genre = new Genre();
        genre.setName(genreRequestDTO.getName());
        genre.setDescription(genreRequestDTO.getDescription());

        Genre savedGenre = this.genreRepository.save(genre);

        return this.convertGenreToGenreDTO(savedGenre);

    }

    @Override
    public GenreResponseDTO updateGenre(long id, GenreRequestDTO genreRequestDTO) {
        Genre genre = this.findGenreById(id);
        genre.setName(genreRequestDTO.getName());
        genre.setDescription(genreRequestDTO.getDescription());
        Genre updatedGenre = this.genreRepository.save(genre);

        return this.convertGenreToGenreDTO(updatedGenre);
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
                .map(this::convertGenreToGenreDTO)
                .toList();
        resultPaginationDTO.setResult(genreResponseDTOS);

        return resultPaginationDTO;
    }

    @Override
    public void deleteGenre(long id) {
        Genre genre = this.findGenreById(id);
        this.genreRepository.delete(genre);
    }

    private GenreResponseDTO convertGenreToGenreDTO(Genre genre) {
        GenreResponseDTO genreResponseDTO = GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();

        return genreResponseDTO;
    }

    private Genre findGenreById(long id) {
        return this.genreRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Genre not found"));
    }


}
