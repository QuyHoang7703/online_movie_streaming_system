package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActorService {
    ActorResponseDTO createActor(ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException;
    ActorResponseDTO updateActor(long actorId, ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException;
    void deleteActor(long actorId) throws IOException;
    ActorResponseDTO getDetailActor(long actorId);
}
