package ru.shoichi.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActorDto {
    private Integer id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String biography;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActorDto actorDto)) return false;
        return Objects.equals(id, actorDto.id) && Objects.equals(name, actorDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
