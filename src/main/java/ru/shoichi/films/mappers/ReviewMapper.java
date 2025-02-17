package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.ReviewCreatedDto;
import ru.shoichi.films.dto.ReviewGetDto;
import ru.shoichi.films.dto.UserGetDto;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.entities.Review;
import ru.shoichi.films.entities.User;

public class ReviewMapper implements BaseMapper<Review, ReviewCreatedDto> {
    private final UserMapper userMapper = new UserMapper();
    private final MovieMapper movieMapper = new MovieMapper();


    @Override
    public Review fromDto(ReviewCreatedDto dto) {
        if (dto == null) {
            return null;
        }
        Review review = new Review();
        review.setId(dto.getId());
        User user = new User();
        user.setId(dto.getUserId());
        review.setUser(user);
        Movie movie = new Movie();
        movie.setId(dto.getMovieId());
        review.setMovie(movie);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(dto.getCreatedAt());
        return review;
    }

    @Override
    public ReviewCreatedDto toDto(Review entity) {
        if (entity == null) {
            return null;
        }
        ReviewGetDto dto = ReviewGetDto.copyFrom(ReviewGetDto.builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .movieId(entity.getMovie().getId())
                .userId(entity.getUser().getId())
                .createdAt(entity.getCreatedAt())
                .rating(entity.getRating())
                .build()
        );
        dto.setUser((UserGetDto) userMapper.toDto(entity.getUser()));
        dto.setMovie(movieMapper.toDto(entity.getMovie()));

        return dto;
    }
}
