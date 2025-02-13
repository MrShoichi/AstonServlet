package ru.shoichi.films.services.impl;

import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.dto.ReviewCreatedDto;
import ru.shoichi.films.entities.Review;
import ru.shoichi.films.mappers.ReviewMapper;
import ru.shoichi.films.repositories.impl.ReviewRepositoryImpl;
import ru.shoichi.films.services.ABaseService;

@Slf4j
public class ReviewServiceImpl extends ABaseService<Review, ReviewCreatedDto> {
    public ReviewServiceImpl() {
        super(new ReviewRepositoryImpl(), new ReviewMapper());
    }
}
