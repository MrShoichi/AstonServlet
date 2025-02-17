package ru.shoichi.films.utils;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private int status;
    private String message;
    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public static Response success(String message) {
        return new Response(200, message);
    }
    public static Response notFound(String message) {
        return new Response(404, message);
    }
    public static Response error(String message) {
        return new Response(400, message);
    }

}
