package com.example.OnlineMovieStreamingSystem.domain;

import com.example.OnlineMovieStreamingSystem.util.constant.VideoType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private VideoType videoType;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name="standalone_id")
    private StandaloneMovie standaloneMovie;

    @ManyToOne
    @JoinColumn(name="episode_id")
    private Episode episode;

    @AssertTrue(message = "VideoVersion must belong to either a StandaloneMovie or an Episode, not both.")
    public boolean isValidOwner() {
        return (standaloneMovie != null && episode == null) ||
                (standaloneMovie == null && episode != null);
    }
}
