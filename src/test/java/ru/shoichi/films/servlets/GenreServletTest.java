package ru.shoichi.films.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.services.BaseService;
import ru.shoichi.films.utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServletTest {
    private GenreServlet servlet;
    @Mock
    private BaseService<GenreDto> serviceMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;

    @BeforeEach
    void setUp() {
        servlet = new GenreServlet(serviceMock);
    }

    @Test
    void testDoGetAllGenres() throws IOException {
        GenreDto genre1 = new GenreDto(1, "Action");
        GenreDto genre2 = new GenreDto(2, "Comedy");

        List<GenreDto> genreList = List.of(genre1, genre2);

        when(serviceMock.getAll()).thenReturn(genreList);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(serviceMock, times(1)).getAll();
    }

    @Test
    void testDoGetGenreById() throws IOException {
        int genreId = 1;
        GenreDto genreDto = new GenreDto(genreId, "Action");

        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.getById(genreId)).thenReturn(genreDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(serviceMock, times(1)).getById(genreId);
        verify(responseMock, times(1)).setContentType("application/json");
    }

    @Test
    void testDoGetByIdInvalidId() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/afdsfads");
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoGetNotFound() throws IOException {
        int movieId = 1;
        try (MockedStatic<Response> mockedResponse = mockStatic(Response.class)) {
            when(requestMock.getPathInfo()).thenReturn("/1");
            when(serviceMock.getById(movieId)).thenReturn(null);
            when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

            servlet.doGet(requestMock, responseMock);

            verify(serviceMock, times(1)).getById(movieId);
            verify(responseMock, times(1)).setContentType("application/json");
            mockedResponse.verify(() -> Response.notFound(any(String.class)), times(1));
            verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
    }

    @Test
    void testDoPostCreateGenre() throws IOException, NotFoundRelationsException, NotValideEntityException {
        GenreDto genreDto = new GenreDto(1, "Action");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Action\"}")));
        when(serviceMock.save(any(GenreDto.class))).thenReturn(genreDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(GenreDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoDeleteInvalidIdReview() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/afds");
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoDeleteInvalidPath() throws IOException {
        when(requestMock.getPathInfo()).thenReturn(null);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoNotValidReview() throws IOException, NotFoundRelationsException, NotValideEntityException {

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Test!\"}")));
        when(serviceMock.save(any(GenreDto.class))).thenThrow(new NotValideEntityException("Test"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(GenreDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPostPathNotEmptyReview() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @SneakyThrows
    @Test
    void testDoDeleteGenre() {
        int genreId = 1;
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.delete(genreId)).thenReturn(true);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(serviceMock, times(1)).delete(genreId);
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPutUpdateGenre() throws IOException, NoEntityException {
        GenreDto genreDto = new GenreDto(1, "Drama");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Drama\"}")));
        when(serviceMock.update(any(GenreDto.class))).thenReturn(genreDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(serviceMock, times(1)).update(any(GenreDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPutHasPath() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/afds");

        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPutNoEntity() throws IOException, NoEntityException {
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Drama\"}")));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        when(serviceMock.update(any(GenreDto.class))).thenThrow(new NoEntityException("Test"));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
