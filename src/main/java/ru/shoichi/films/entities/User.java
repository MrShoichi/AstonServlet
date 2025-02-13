package ru.shoichi.films.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private Date createdAt;
    private Role role;
}
