package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.MovieDto;
import ru.shoichi.films.entities.Movie;


public class MovieMapper implements BaseMapper<Movie, MovieDto> {
    private final GenreMapper genreMapper = new GenreMapper();
    private final ActorMapper actorMapper = new ActorMapper();
    @Override
    public Movie fromDto(MovieDto dto) {
        if (dto == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setCreatedAt(dto.getCreatedAt());
        movie.setDuration(dto.getDuration());
        movie.setGenres(dto.getGenres().stream().map(genreMapper::fromDto).toList());
        movie.setRating(dto.getRating());
        movie.setActors(dto.getActors().stream().map(actorMapper::fromDto).toList());
        return movie;
    }

    @Override
    public MovieDto toDto(Movie entity) {
        if (entity == null) {
            return null;
        }
        return MovieDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .genres(entity.getGenres().stream().map(genreMapper::toDto).toList())
                .rating(entity.getRating())
                .actors(entity.getActors().stream().map(actorMapper::toDto).toList())
                .duration(entity.getDuration())
                .build();
    }
}
