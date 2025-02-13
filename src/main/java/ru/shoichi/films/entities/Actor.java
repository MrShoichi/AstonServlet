package ru.shoichi.films.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Actor extends BaseEntity {
    private String name;
    private Date birthDate;
    private String biography;
}
