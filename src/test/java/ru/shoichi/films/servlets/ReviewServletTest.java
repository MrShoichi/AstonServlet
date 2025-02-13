package ru.shoichi.films.servlets;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.dto.ReviewCreatedDto;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.services.BaseService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.shoichi.films.utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServletTest {


    private ReviewServlet servlet;
    @Mock
    private BaseService<ReviewCreatedDto> serviceMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;

    @BeforeEach
    void setUp() {
        servlet = new ReviewServlet(serviceMock);
    }

    @Test
    void testDoGetAllReviews() throws IOException {
        ReviewCreatedDto review1 = new ReviewCreatedDto();
        review1.setId(1);
        review1.setComment("Great movie!");
        ReviewCreatedDto review2 = new ReviewCreatedDto();
        review2.setId(2);
        review2.setComment("Not bad.");

        List<ReviewCreatedDto> reviewList = List.of(review1, review2);

        when(serviceMock.getAll()).thenReturn(reviewList);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(serviceMock, times(1)).getAll();
    }

    @Test
    void testDoGetReviewById() throws IOException {
        int movieId = 1;
        ReviewCreatedDto review = new ReviewCreatedDto();
        review.setId(movieId);
        review.setComment("Great movie!");

        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.getById(movieId)).thenReturn(review);
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
        ReviewCreatedDto reviewDto = new ReviewCreatedDto();
        reviewDto.setId(1);
        reviewDto.setComment("Excellent movie!");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"comment\":\"Excellent movie!\"}")));
        when(serviceMock.save(any(ReviewCreatedDto.class))).thenReturn(reviewDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(ReviewCreatedDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoNotValidReview() throws IOException, NotFoundRelationsException, NotValideEntityException {

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"comment\":\"Excellent movie!\"}")));
        when(serviceMock.save(any(ReviewCreatedDto.class))).thenThrow(new NotValideEntityException("Test"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(ReviewCreatedDto.class));
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
        ReviewCreatedDto reviewDto = new ReviewCreatedDto();
        reviewDto.setId(1);
        reviewDto.setComment("Updated review content");

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"comment\":\"Updated review content\"}")));
        when(serviceMock.update(any(ReviewCreatedDto.class))).thenReturn(reviewDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(serviceMock, times(1)).update(any(ReviewCreatedDto.class));
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
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"comment\":\"Updated review content\"}")));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        when(serviceMock.update(any(ReviewCreatedDto.class))).thenThrow(new NoEntityException("Test"));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
