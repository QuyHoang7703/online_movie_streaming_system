package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Movie;
import com.example.OnlineMovieStreamingSystem.domain.UserInteraction;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.dto.request.userInteraction.UserInteractionRequestDTO;
import com.example.OnlineMovieStreamingSystem.dto.response.userInteraction.UserInteractionResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.MovieRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserInteractionRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import com.example.OnlineMovieStreamingSystem.service.UserInteractionService;
import com.example.OnlineMovieStreamingSystem.util.SecurityUtil;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInteractionServiceImpl implements UserInteractionService {
    private final UserInteractionRepository userInteractionRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    @Override
    public UserInteractionResponseDTO addUserInteraction(UserInteractionRequestDTO userInteractionRequestDTO) {
        String email = SecurityUtil.getLoggedEmail();
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy user với email: " + email));
        Movie movie = this.movieRepository.findById(userInteractionRequestDTO.getMovieId())
                .orElseThrow(() -> new ApplicationException("Không tìm thấy phim với id: " + userInteractionRequestDTO.getMovieId()));

        UserInteraction userInteraction = new UserInteraction();
        userInteraction.setRating(userInteractionRequestDTO.getRatingValue());
        userInteraction.setUser(user);
        userInteraction.setMovie(movie);

        UserInteraction savedUserInteraction = this.userInteractionRepository.save(userInteraction);
        return this.convertToUserInteractionResponseDTO(savedUserInteraction);
    }

    @Override
    public UserInteractionResponseDTO updateUserInteraction(UserInteractionRequestDTO userInteractionRequestDTO) {
        String email = SecurityUtil.getLoggedEmail();
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy user với email: " + email));

        UserInteraction userInteractionDB = this.userInteractionRepository.findByUserIdAndMovieId(user.getId(), userInteractionRequestDTO.getMovieId())
                .orElseThrow(() -> new ApplicationException("Không tìm thấy rating của user đối vói phim này"));

        userInteractionDB.setRating(userInteractionRequestDTO.getRatingValue());
        UserInteraction updatedUserInteraction = this.userInteractionRepository.save(userInteractionDB);
        return this.convertToUserInteractionResponseDTO(updatedUserInteraction);
    }

    @Override
    public UserInteractionResponseDTO getUserInteraction(long movieId) {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getLoggedEmail() : null;
        if(email != null && !email.equals("anonymous") ) {
            User user = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new ApplicationException("Không tìm thấy user với email: " + email));
            UserInteraction userInteraction = this.userInteractionRepository.findByUserIdAndMovieId(user.getId(), movieId)
                    .orElseThrow(() -> new ApplicationException("Không tìm thấy rating của user đối vói phim này"));

            return this.convertToUserInteractionResponseDTO(userInteraction);

        }
        return null;
    }

    private UserInteractionResponseDTO convertToUserInteractionResponseDTO(UserInteraction userInteraction) {
        return UserInteractionResponseDTO.builder()
                .userId(userInteraction.getUser().getId())
                .email(userInteraction.getUser().getEmail())
                .ratingValue(userInteraction.getRating())
                .movieId(userInteraction.getMovie().getId())
                .build();
    }
}
