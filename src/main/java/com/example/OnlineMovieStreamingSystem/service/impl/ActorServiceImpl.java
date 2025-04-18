package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Actor;
import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.ActorRepository;
import com.example.OnlineMovieStreamingSystem.service.ActorService;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ImageStorageService imageStorageService;
    private final String CONTAINER_NAME = "actor-image-container";

    @Override
    public ActorResponseDTO createActor(ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException {
        Actor actor = new Actor();
        actor.setName(actorRequestDTO.getName());
        actor.setBirthDate(actorRequestDTO.getBirthDate());
        actor.setBiography(actorRequestDTO.getBiography());
        actor.setOtherName(actorRequestDTO.getOtherName());
        actor.setGender(actorRequestDTO.getGender());

        String avatarUrl = this.imageStorageService.uploadImage(CONTAINER_NAME, avatar.getOriginalFilename(), avatar.getInputStream());
        actor.setAvatarUrl(avatarUrl);

        Actor savedActor = actorRepository.save(actor);

        return this.convertToActorResponseDTO(savedActor);
    }

    @Override
    public ActorResponseDTO updateActor(long actorId, ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException {
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

        String avatarUrl = actorDB.getAvatarUrl();
        if(avatar != null && avatarUrl != null){
            String newAvatarUrl = this.imageStorageService.uploadImage(CONTAINER_NAME, avatar.getOriginalFilename(), avatar.getInputStream());
            // After uploaded successfully then delete old avatar
            String originalAvatarName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            this.imageStorageService.deleteImage(CONTAINER_NAME, originalAvatarName);
            actorDB.setAvatarUrl(newAvatarUrl);
        }
        Actor updatedActor = this.actorRepository.save(actorDB);

        return this.convertToActorResponseDTO(updatedActor);
    }

    @Override
    public void deleteActor(long actorId) throws IOException {
        Actor actor = this.actorRepository.findById(actorId)
                .orElseThrow(() ->  new ApplicationException("Actor not found"));

        String avatarUrl = actor.getAvatarUrl();
        String originalAvatar = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
        this.imageStorageService.deleteImage(CONTAINER_NAME, originalAvatar);

        this.actorRepository.delete(actor);
    }

    @Override
    public ActorResponseDTO getDetailActor(long actorId) {
        Actor actor = this.actorRepository.findById(actorId)
                .orElseThrow(() ->  new ApplicationException("Actor not found"));

        return this.convertToActorResponseDTO(actor);
    }

    private ActorResponseDTO convertToActorResponseDTO(Actor actor) {
        ActorResponseDTO actorResponseDTO = new ActorResponseDTO();
        actorResponseDTO.setId(actor.getId());
        actorResponseDTO.setName(actor.getName());
        actorResponseDTO.setBirthDate(actor.getBirthDate());
        actorResponseDTO.setAvatarUrl(actor.getAvatarUrl());
        actorResponseDTO.setBiography(actor.getBiography());
        actorResponseDTO.setOtherName(actor.getOtherName());
        actorResponseDTO.setGender(actor.getGender());

        return actorResponseDTO;
    }
}
