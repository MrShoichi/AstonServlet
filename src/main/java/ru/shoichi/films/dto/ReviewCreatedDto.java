package ru.shoichi.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreatedDto {
    private Integer id;
    private Integer movieId;
    private Integer userId;
    private double rating;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewCreatedDto reviewDto)) return false;
        return Objects.equals(id, reviewDto.id) && Objects.equals(userId, reviewDto.userId)
                && Objects.equals(movieId, reviewDto.movieId)
                && Objects.equals(rating, reviewDto.rating)
                && Objects.equals(createdAt, reviewDto.createdAt)
                && Objects.equals(comment, reviewDto.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, movieId, rating, comment, createdAt);
    }
}
