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
import ru.shoichi.films.dto.MovieDto;
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
class MovieServletTest {
    private MovieServlet servlet;

    @Mock
    private BaseService<MovieDto> serviceMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;

    @BeforeEach
    void setUp() {
        servlet = new MovieServlet(serviceMock);
    }

    @Test
    void testDoGetAllMovies() throws IOException {
        MovieDto movie1 = new MovieDto();
        movie1.setId(1);
        movie1.setTitle("Movie 1");
        MovieDto movie2 = new MovieDto();
        movie2.setId(2);
        movie2.setTitle("Movie 2");

        List<MovieDto> movieList = List.of(movie1, movie2);

        when(serviceMock.getAll()).thenReturn(movieList);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(serviceMock, times(1)).getAll();
    }

    @Test
    void testDoGetMovieById() throws IOException {
        int movieId = 1;
        MovieDto movie = new MovieDto();
        movie.setId(movieId);
        movie.setTitle("Test Movie");

        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.getById(movieId)).thenReturn(movie);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(serviceMock, times(1)).getById(movieId);
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
    void testDoPostCreateMovie() throws IOException, NotFoundRelationsException, NotValideEntityException {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(1);
        movieDto.setTitle("New Movie");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"title\":\"New Movie\"}")));
        when(serviceMock.save(any(MovieDto.class))).thenReturn(movieDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(MovieDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoNotValidReview() throws IOException, NotFoundRelationsException, NotValideEntityException {

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"title\":\"New Movie\"}")));
        when(serviceMock.save(any(MovieDto.class))).thenThrow(new NotValideEntityException("Test"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(MovieDto.class));
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

    @SneakyThrows
    @Test
    void testDoDeleteMovie() {
        int movieId = 1;
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.delete(movieId)).thenReturn(true);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(serviceMock, times(1)).delete(movieId);
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    void testDoPutUpdateMovie() throws IOException, NoEntityException {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(1);
        movieDto.setTitle("Updated Movie");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"title\":\"Updated Movie\"}")));
        when(serviceMock.update(any(MovieDto.class))).thenReturn(movieDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(serviceMock, times(1)).update(any(MovieDto.class));
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
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"title\":\"Updated Movie\"}")));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        when(serviceMock.update(any(MovieDto.class))).thenThrow(new NoEntityException("Test"));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}