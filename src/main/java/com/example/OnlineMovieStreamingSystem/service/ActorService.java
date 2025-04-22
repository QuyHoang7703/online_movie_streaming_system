package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorDetailResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActorService {
    ActorDetailResponseDTO createActor(ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException;
    ActorDetailResponseDTO updateActor(long actorId, ActorRequestDTO actorRequestDTO, MultipartFile avatar) throws IOException;
    void deleteActor(long actorId) throws IOException;
    ActorDetailResponseDTO getDetailActor(long actorId);
    ResultPaginationDTO getAllActor(String actorName, int page, int size);
}
