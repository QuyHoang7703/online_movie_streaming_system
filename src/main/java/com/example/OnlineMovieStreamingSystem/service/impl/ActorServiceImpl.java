package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Actor;
import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.MovieActor;
import com.example.OnlineMovieStreamingSystem.dto.Meta;
import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorDetailResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.movie.MovieUserResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.ActorRepository;
import com.example.OnlineMovieStreamingSystem.service.ActorService;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.service.MovieService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ImageStorageService imageStorageService;
    private final MovieService movieService;
    private final String CONTAINER_NAME = "actor-image-container";

    @Override
    public ActorDetailResponseDTO createActor(ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException {
        Actor actor = new Actor();
        actor.setName(actorRequestDTO.getName());
        actor.setBirthDate(actorRequestDTO.getBirthDate());
        actor.setBiography(actorRequestDTO.getBiography());
        actor.setOtherName(actorRequestDTO.getOtherName());
        actor.setGender(actorRequestDTO.getGender());
        actor.setPlaceOfBirth(actorRequestDTO.getPlaceOfBirth());

        String avatarUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, avatar.getOriginalFilename(), avatar.getInputStream());
        actor.setAvatarUrl(avatarUrl);

        Actor savedActor = actorRepository.save(actor);

        return this.convertToActorDetailResponseDTO(savedActor);
    }

    @Override
    public ActorDetailResponseDTO updateActor(long actorId, ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException {
        Actor actorDB = this.actorRepository.findById(actorId)
                .orElseThrow(() ->  new ApplicationException("Actor not found"));

        // Objects.equals(a, b) <=> (a==b) || (a != null && a.equals(b))
        if(!Objects.equals(actorRequestDTO.getName(), actorDB.getName())) {
            actorDB.setName(actorRequestDTO.getName());
        }
        if (!Objects.equals(actorRequestDTO.getBiography(), actorDB.getBiography())) {
            actorDB.setBirthDate(actorRequestDTO.getBirthDate());
        }
        if(!Objects.equals(actorRequestDTO.getBiography(), actorDB.getBiography())) {
            actorDB.setBiography(actorRequestDTO.getBiography());
        }
        if(!Objects.equals(actorRequestDTO.getOtherName(), actorDB.getOtherName())) {
            actorDB.setOtherName(actorRequestDTO.getOtherName());
        }
        if(!Objects.equals(actorRequestDTO.getGender(), actorDB.getGender())) {
            actorDB.setGender(actorRequestDTO.getGender());
        }

        if(!Objects.equals(actorRequestDTO.getPlaceOfBirth(), actorDB.getPlaceOfBirth())) {
            actorDB.setPlaceOfBirth(actorRequestDTO.getPlaceOfBirth());
        }

        String avatarUrl = actorDB.getAvatarUrl();
        if(avatar != null && avatarUrl != null){
            String newAvatarUrl = this.imageStorageService.uploadFile(CONTAINER_NAME, avatar.getOriginalFilename(), avatar.getInputStream());
            // After uploaded successfully then delete old avatar
            String originalAvatarName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            this.imageStorageService.deleteFile(CONTAINER_NAME, originalAvatarName);
            actorDB.setAvatarUrl(newAvatarUrl);
        }else{
            log.info("Not avatar to update");
        }
        Actor updatedActor = this.actorRepository.save(actorDB);

        return this.convertToActorDetailResponseDTO(updatedActor);
    }

    @Override
    public void deleteActor(long actorId) throws IOException {
        Actor actor = this.actorRepository.findById(actorId)
                .orElseThrow(() ->  new ApplicationException("Actor not found"));

        String avatarUrl = actor.getAvatarUrl();
        String originalAvatar = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
        this.imageStorageService.deleteFile(CONTAINER_NAME, originalAvatar);

        this.actorRepository.delete(actor);
    }

    @Override
    public ActorDetailResponseDTO getDetailActor(long actorId) {
        Actor actor = this.actorRepository.findById(actorId)
                .orElseThrow(() ->  new ApplicationException("Actor not found"));

        return this.convertToActorDetailResponseDTO(actor);
    }

    @Override
    public ResultPaginationDTO getAllActor(String actorName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Actor> actorPage = this.actorRepository.findAll(actorName, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(actorPage.getTotalPages());
        meta.setTotalElements(actorPage.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);

        List<ActorResponseDTO> actorResponseDTOS = actorPage.getContent().stream()
                .map(this::convertToActorResponseDTO)
                .toList();

        resultPaginationDTO.setResult(actorResponseDTOS);

        return resultPaginationDTO;
    }

    private ActorDetailResponseDTO convertToActorDetailResponseDTO(Actor actor) {
        ActorDetailResponseDTO actorDetailResponseDTO = new ActorDetailResponseDTO();
        actorDetailResponseDTO.setId(actor.getId());
        actorDetailResponseDTO.setName(actor.getName());
        actorDetailResponseDTO.setBirthDate(actor.getBirthDate());
        actorDetailResponseDTO.setAvatarUrl(actor.getAvatarUrl());
        actorDetailResponseDTO.setBiography(actor.getBiography());
        actorDetailResponseDTO.setOtherName(actor.getOtherName());
        actorDetailResponseDTO.setPlaceOfBirth(actor.getPlaceOfBirth());
        actorDetailResponseDTO.setGender(actor.getGender());
        
        List<Movie> movies = actor.getMovieActors().stream().map(MovieActor::getMovie).toList();
        List<MovieUserResponseDTO> movieUserResponseDTOS = movies.stream()
                .map(this.movieService::convertToMovieUserResponseDTO)
                .toList();
        actorDetailResponseDTO.setMovies(movieUserResponseDTOS);

        return actorDetailResponseDTO;
    }

    private ActorResponseDTO convertToActorResponseDTO(Actor actor) {
        ActorResponseDTO actorResponseDTO = ActorResponseDTO.builder()
                .id(actor.getId())
                .name(actor.getName())
                .birthDate(actor.getBirthDate())
                .placeOfBirth(actor.getPlaceOfBirth())
                .avatarUrl(actor.getAvatarUrl())
                .build();

        return actorResponseDTO;
    }
}
