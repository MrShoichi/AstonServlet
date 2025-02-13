package ru.shoichi.films.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.shoichi.films.dto.ActorDto;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.services.BaseService;
import ru.shoichi.films.services.impl.ActorServiceImpl;
import ru.shoichi.films.utils.Response;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.shoichi.films.utils.ServletUtils.*;

@AllArgsConstructor
@WebServlet(name = "ActorServlet", value = "/api/v1/actors/*")
public class ActorServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "application/json";
    private final BaseService<ActorDto> service;

    public ActorServlet() {
        service = new ActorServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE);
        try {
            if (!isPathNotNull(req.getPathInfo())) {
                List<ActorDto> allActors = service.getAll();
                writeResponse(resp, SC_OK, allActors);
                return;
            }
            String idString = pathInfo.substring(1);


            int actorId = Integer.parseInt(idString);
            ActorDto actorDto = service.getById(actorId);

            if (actorDto == null) {
                writeResponse(resp, SC_NOT_FOUND, Response.notFound("Актера с таким Id не найден: " + actorId));
                return;
            }
            writeResponse(resp, SC_OK, actorDto);
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

        ActorDto requestActor = mapRequest(req.getReader(), ActorDto.class);
        ActorDto addedActor;
        try {
            addedActor = service.save(requestActor);
        } catch (NotFoundRelationsException | NotValideEntityException e) {
            writeBadRequest(resp, e.getMessage());
            return;
        }

        writeResponse(resp, SC_CREATED, addedActor);
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
            Integer actorId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = service.delete(actorId);

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

        ActorDto requestActor = mapRequest(req.getReader(), ActorDto.class);
        try {
            ActorDto updated = service.update(requestActor);
            writeResponse(resp, SC_OK, updated);
        } catch (NoEntityException e) {
            writeBadRequest(resp, e.getMessage());
        }
    }


}
