package ru.shoichi.films.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.services.BaseService;
import ru.shoichi.films.services.impl.GenreServiceImpl;
import ru.shoichi.films.utils.Response;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.shoichi.films.utils.ServletUtils.*;

@AllArgsConstructor
@WebServlet(name = "GenreServlet", value = "/api/v1/genres/*")
public class GenreServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "application/json";
    private final BaseService<GenreDto> genreService;

    public GenreServlet() {
        genreService = new GenreServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE);
        try {
            if (!isPathNotNull(req.getPathInfo())) {
                List<GenreDto> allGenres = genreService.getAll();
                writeResponse(resp, SC_OK, allGenres);
                return;
            }
            String idString = pathInfo.substring(1);


            int genreId = Integer.parseInt(idString);
            GenreDto genreDto = genreService.getById(genreId);

            if (genreDto == null) {
                writeResponse(resp, SC_NOT_FOUND, Response.notFound("Жанр с таким Id не найден: " + genreId));
                return;
            }
            writeResponse(resp, SC_OK, genreDto);
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

        GenreDto requestGenre = mapRequest(req.getReader(), GenreDto.class);
        GenreDto addedGenre;
        try {
            addedGenre = genreService.save(requestGenre);
        } catch (NotFoundRelationsException | NotValideEntityException e) {
            writeBadRequest(resp, e.getMessage());
            return;
        }

        writeResponse(resp, SC_CREATED, addedGenre);
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
            Integer genreId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = genreService.delete(genreId);

            writeResponse(resp, SC_OK, Response.success("Объект удален: " + deleted));
        } catch (NumberFormatException e) {
            writeBadRequest(resp, "Путь должен содержать только число");
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

        GenreDto requestGenre = mapRequest(req.getReader(), GenreDto.class);
        try {
            GenreDto updatedGenre = genreService.update(requestGenre);
            writeResponse(resp, SC_OK, updatedGenre);
        } catch (NoEntityException e) {
            writeBadRequest(resp, e.getMessage());
        }
    }


}
