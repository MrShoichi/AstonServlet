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
import ru.shoichi.films.dto.ActorDto;
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
class ActorServletTest {
    private ActorServlet servlet;
    @Mock
    private BaseService<ActorDto> serviceMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;

    @BeforeEach
    void setUp() {
        servlet = new ActorServlet(serviceMock);
    }

    @Test
    void testDoGetAllReviews() throws IOException {
        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("Test 1!");
        ActorDto actorDto2 = new ActorDto();
        actorDto2.setId(2);
        actorDto2.setName("Test 2!");

        List<ActorDto> reviewList = List.of(actorDto, actorDto2);

        when(serviceMock.getAll()).thenReturn(reviewList);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(serviceMock, times(1)).getAll();
    }

    @Test
    void testDoGetReviewById() throws IOException {
        int movieId = 1;
        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("Test 1!");

        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.getById(movieId)).thenReturn(actorDto);
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
    void testDoPostCreateReview() throws IOException, NotFoundRelationsException, NotValideEntityException {
        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("Test!");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Test!\"}")));
        when(serviceMock.save(any(ActorDto.class))).thenReturn(actorDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(ActorDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }
    @Test
    void testDoNotValidReview() throws IOException, NotFoundRelationsException, NotValideEntityException {

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Test!\"}")));
        when(serviceMock.save(any(ActorDto.class))).thenThrow(new NotValideEntityException("Test"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(ActorDto.class));
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
    void testDoDeleteReview() {
        int reviewId = 1;
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.delete(reviewId)).thenReturn(true);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(serviceMock, times(1)).delete(reviewId);
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
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
    void testDoPutUpdateReview() throws IOException, NoEntityException {
        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("Test");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Test\"}")));
        when(serviceMock.update(any(ActorDto.class))).thenReturn(actorDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(serviceMock, times(1)).update(any(ActorDto.class));
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
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"name\":\"Test\"}")));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        when(serviceMock.update(any(ActorDto.class))).thenThrow(new NoEntityException("Test"));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}