package ru.shoichi.films.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Review extends BaseEntity {
    private Movie movie;
    private User user;
    private double rating;
    private String comment;
    private Date createdAt;
}
