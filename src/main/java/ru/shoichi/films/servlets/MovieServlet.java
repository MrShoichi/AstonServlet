package ru.shoichi.films.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.shoichi.films.dto.MovieDto;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.services.BaseService;
import ru.shoichi.films.services.impl.MovieServiceImpl;
import ru.shoichi.films.utils.Response;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.shoichi.films.utils.ServletUtils.*;

@AllArgsConstructor
@WebServlet(name = "MovieServlet", value = "/api/v1/movies/*")
public class MovieServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "application/json";
    private final BaseService<MovieDto> service;

    public MovieServlet() {
        service = new MovieServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE);
        try {

            if (!isPathNotNull(req.getPathInfo())) {
                List<MovieDto> allMovies = service.getAll();
                writeResponse(resp, SC_OK, allMovies);
                return;
            }
            String idString = pathInfo.substring(1);

            int movieId = Integer.parseInt(idString);
            MovieDto movieDto = service.getById(movieId);

            if (movieDto == null) {
                writeResponse(resp, SC_NOT_FOUND, Response.notFound("Фильм с таким Id не найден: " + movieId));
                return;
            }
            writeResponse(resp, SC_OK, movieDto);
        } catch (NumberFormatException e) {
            writeResponse(resp, SC_BAD_REQUEST, Response.error("ID должно быть числом."));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (isPathNotNull(req.getPathInfo())) {
            writeBadRequest(resp);
            return;
        }
        resp.setContentType(CONTENT_TYPE);

        MovieDto requestMovie = mapRequest(req.getReader(), MovieDto.class);
        MovieDto addedMovie;
        try {
            addedMovie = service.save(requestMovie);
        } catch (NotFoundRelationsException | NotValideEntityException e) {
            writeBadRequest(resp, e.getMessage());
            return;
        }

        writeResponse(resp, SC_CREATED, addedMovie);
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(CONTENT_TYPE);

        String pathInfo = req.getPathInfo();
        if (!hasPath(pathInfo)) {
            writeBadRequest(resp);
            return;
        }
        try {
            Integer movieId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = service.delete(movieId);

            writeResponse(resp, SC_OK, Response.success("Объект удален: " + deleted));
        } catch (NumberFormatException e) {
            writeBadRequest(resp,"Путь должен содержать только число");
        } catch (NoEntityException e) {
            writeResponse(resp, SC_NOT_FOUND, Response.notFound(e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (hasPath(req.getPathInfo())) {
            writeBadRequest(resp);
            return;
        }
        resp.setContentType(CONTENT_TYPE);

        MovieDto requestMovie = mapRequest(req.getReader(), MovieDto.class);
        try {
            MovieDto updated = service.update(requestMovie);
            writeResponse(resp, SC_OK, updated);
        } catch (NoEntityException e) {
            writeBadRequest(resp, e.getMessage());
        }

    }
}
