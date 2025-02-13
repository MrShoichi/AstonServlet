package ru.shoichi.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer id;
    private String title;
    private String description;
    private int duration;
    private double rating;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    private List<GenreDto> genres;
    private List<ActorDto> actors;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieDto movieDto)) return false;
        return Objects.equals(id, movieDto.id) && Objects.equals(title, movieDto.title)
                && Objects.equals(description, movieDto.description)
                && Objects.equals(duration, movieDto.duration)
                && Objects.equals(rating, movieDto.rating)
                && Objects.equals(createdAt, movieDto.createdAt)
                && Objects.equals(genres, movieDto.genres)
                && Objects.equals(actors, movieDto.actors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, duration, rating, createdAt, genres, actors);
    }
}
