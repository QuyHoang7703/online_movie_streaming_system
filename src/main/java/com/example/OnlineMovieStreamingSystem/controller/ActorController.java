package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.ActorService;
import com.example.OnlineMovieStreamingSystem.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/actors")
@PreAuthorize("hasRole('ADMIN')")
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    @ApiMessage("Created the actor")
    public ResponseEntity<ActorResponseDTO> createActor(@Valid @RequestPart("actorInfo") ActorRequestDTO actorRequestDTO,
                                                        @RequestParam("avatar") MultipartFile avatar) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actorRequestDTO, avatar));
    }

    @PatchMapping("{actorId}")
    @ApiMessage("Updated the actor")
    public ResponseEntity<ActorResponseDTO> updateActor(@PathVariable("actorId") long actorId,
                                                        @Valid @RequestPart("actorInfo") ActorRequestDTO actorRequestDTO,
                                                        @RequestParam(name = "avatar", required = false) MultipartFile avatar) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(this.actorService.updateActor(actorId, actorRequestDTO, avatar));
    }

    @DeleteMapping("{actorId}")
    @ApiMessage("Deleted the actor")
    public ResponseEntity<Void> deleteActor(@PathVariable("actorId") long actorId) throws IOException {
        this.actorService.deleteActor(actorId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{actorId}")
    public ResponseEntity<ActorResponseDTO> getDetailActor(@PathVariable("actorId") long actorId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actorService.getDetailActor(actorId));
    }
}
