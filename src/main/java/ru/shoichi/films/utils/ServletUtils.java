package ru.shoichi.films.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public final class ServletUtils {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T mapRequest(BufferedReader reader, Class<T> tClass) throws IOException {
        return mapper.readValue(readJson(reader), tClass);
    }

    public static String readJson(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    public static void writeNotAuthorizeRequest(HttpServletResponse resp, String message) throws IOException {
        writeResponse(resp, SC_UNAUTHORIZED, message);
    }

    public static void writeBadRequest(HttpServletResponse resp) throws IOException {
        writeBadRequest(resp, "Неправильный путь запроса");
    }

    public static void writeBadRequest(HttpServletResponse resp, String message) throws IOException {
        writeResponse(resp, SC_BAD_REQUEST, message);
    }

    public static void writeResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(mapper.writeValueAsString(data));
    }

    public static boolean isPathNotNull(String path) {
        return path != null;
    }

    public static boolean hasPath(String pathInfo) {
        return pathInfo != null && !pathInfo.equals("/");
    }
}
