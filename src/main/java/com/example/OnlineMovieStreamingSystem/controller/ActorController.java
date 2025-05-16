package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.dto.ResultPaginationDTO;
import com.example.OnlineMovieStreamingSystem.dto.request.actor.ActorRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.actor.ActorDetailResponseDTO;
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

public class ActorController {
    private final ActorService actorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Created the actor")
    public ResponseEntity<ActorDetailResponseDTO> createActor(@Valid @RequestPart("actorInfo") ActorRequestDTO actorRequestDTO,
                                                              @RequestParam("avatar") MultipartFile avatar) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actorRequestDTO, avatar));
    }

    @PatchMapping("{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Updated the actor")
    public ResponseEntity<ActorDetailResponseDTO> updateActor(@PathVariable("actorId") long actorId,
                                                              @Valid @RequestPart("actorInfo") ActorRequestDTO actorRequestDTO,
                                                              @RequestParam(name = "avatar", required = false) MultipartFile avatar) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(this.actorService.updateActor(actorId, actorRequestDTO, avatar));
    }

    @DeleteMapping("{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Deleted the actor")
    public ResponseEntity<Void> deleteActor(@PathVariable("actorId") long actorId) throws IOException {
        this.actorService.deleteActor(actorId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{actorId}")
    public ResponseEntity<ActorDetailResponseDTO> getDetailActor(@PathVariable("actorId") long actorId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actorService.getDetailActor(actorId));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getActors(@RequestParam(name="actorName", required = false) String actorName,
                                                            @RequestParam(name="page", defaultValue = "1") int page,
                                                            @RequestParam(name="size", defaultValue = "3") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actorService.getAllActor(actorName, page, size));
    }




}
