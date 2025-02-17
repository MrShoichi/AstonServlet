package ru.shoichi.films.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewGetDto extends ReviewCreatedDto {
    private MovieDto movie;
    private UserGetDto user;

    public static ReviewGetDto copyFrom(ReviewCreatedDto review) {
        ReviewGetDto reviewGetDto = new ReviewGetDto();
        reviewGetDto.setId(review.getId());
        reviewGetDto.setComment(review.getComment());
        reviewGetDto.setRating(review.getRating());
        reviewGetDto.setCreatedAt(review.getCreatedAt());
        reviewGetDto.setMovieId(review.getMovieId());
        reviewGetDto.setUserId(review.getUserId());
        return reviewGetDto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ReviewGetDto reviewGetDto)) return false;
        return super.equals(reviewGetDto) && movie.equals(reviewGetDto.movie) && user.equals(reviewGetDto.user);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + movie.hashCode() + user.hashCode();
    }
}
