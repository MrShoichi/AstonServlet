package ru.shoichi.films.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class Movie extends BaseEntity {
    private String title;
    private String description;
    private int duration;
    private double rating;
    private Date createdAt;
    @JsonBackReference
    private List<Genre> genres = new ArrayList<>();
    @JsonBackReference
    private List<Actor> actors = new ArrayList<>();
}
