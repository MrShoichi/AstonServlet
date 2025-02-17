package ru.shoichi.films.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.dto.UserGetDto;
import ru.shoichi.films.exceptions.*;
import ru.shoichi.films.services.UserService;
import ru.shoichi.films.services.impl.UserServiceImpl;
import ru.shoichi.films.jwt.JwtUtil;
import ru.shoichi.films.utils.Response;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.shoichi.films.jwt.JwtCookieUtil.createCookieAuthorization;
import static ru.shoichi.films.utils.ServletUtils.*;

@AllArgsConstructor
@WebServlet(name = "UserServlet", value = "/api/v1/users/*")
public class UserServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "application/json";
    private static final int USER_ROLE_ID = 2;
    private final UserService service;

    public UserServlet() {
        service = new UserServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType(CONTENT_TYPE);
        try {

            if (!isPathNotNull(req.getPathInfo())) {
                List<UserBaseDto> allUsers = service.getAll();
                writeResponse(resp, SC_OK, allUsers);
                return;
            }
            String idString = pathInfo.substring(1);

            int userId = Integer.parseInt(idString);
            UserBaseDto userDto = service.getById(userId);

            if (userDto == null) {
                writeResponse(resp, SC_NOT_FOUND, Response.notFound("Пользователя с таким  id нет: " + userId));
                return;
            }
            writeResponse(resp, SC_OK, userDto);
        } catch (NumberFormatException e) {
            writeResponse(resp, SC_BAD_REQUEST, Response.error("ID должно быть числом."));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        resp.setContentType(CONTENT_TYPE);
        try {
            UserBaseDto requestUser = mapRequest(req.getReader(), UserBaseDto.class);
            if (pathInfo != null && pathInfo.substring(1).equals("login")) {
                login(resp, requestUser);
                return;
            } else if (pathInfo != null && pathInfo.substring(1).equals("register")) {
                requestUser.setRoleId(USER_ROLE_ID);
            }
            registration(resp, requestUser);
        } catch (NotFoundRelationsException | NotValideEntityException | UserExistException e) {
            writeBadRequest(resp, e.getMessage());
        } catch (NoAuthorize e) {
            writeNotAuthorizeRequest(resp, e.getMessage());
        }


    }

    private void registration(HttpServletResponse resp, UserBaseDto requestUser) throws NotFoundRelationsException,
            IOException,
            NotValideEntityException, UserExistException {
        UserBaseDto currentUser = service.save(requestUser);
        writeResponse(resp, SC_CREATED, currentUser);
    }

    private void login(HttpServletResponse resp, UserBaseDto requestUser) throws NoAuthorize, IOException, UserExistException {
        UserGetDto logined = (UserGetDto) service.login(requestUser.getUsername(), requestUser.getPassword());
        String token = JwtUtil.generateToken(logined.getUsername(), logined.getRole().getName());
        resp.addCookie(createCookieAuthorization(token));
        writeResponse(resp, SC_ACCEPTED, logined);
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
            Integer userId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = service.delete(userId);

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

        UserBaseDto requestUser = mapRequest(req.getReader(), UserBaseDto.class);
        try {
            UserBaseDto updated = service.update(requestUser);
            writeResponse(resp, SC_OK, updated);
        } catch (NoEntityException e) {
            writeBadRequest(resp, e.getMessage());
        }

    }
}
