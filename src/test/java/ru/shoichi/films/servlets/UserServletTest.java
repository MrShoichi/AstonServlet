package ru.shoichi.films.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.dto.*;
import ru.shoichi.films.exceptions.*;
import ru.shoichi.films.services.UserService;
import ru.shoichi.films.utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServletTest {
    private UserServlet servlet;

    @Mock
    private UserService serviceMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @BeforeEach
    void setUp() {
        servlet = new UserServlet(serviceMock);
    }

    @Test
    void testDoGetAllUsers() throws IOException {
        UserBaseDto user1 = UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build();
        UserBaseDto user2 = UserBaseDto.builder()
                .id(2)
                .username("User2")
                .email("User2@mail.ru")
                .password("password2")
                .build();

        List<UserBaseDto> userList = List.of(user1, user2);

        when(serviceMock.getAll()).thenReturn(userList);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(serviceMock, times(1)).getAll();
    }

    @Test
    void testDoGetUserById() throws IOException {
        int userId = 1;
        UserBaseDto userDto = UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build();

        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.getById(userId)).thenReturn(userDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(serviceMock, times(1)).getById(userId);
        verify(responseMock, times(1)).setContentType("application/json");
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
    void testDoGetByIdInvalidId() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/afdsfads");
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doGet(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testDoPostRegisterUser() throws IOException, NotFoundRelationsException, NotValideEntityException, UserExistException {
        UserBaseDto userDto = UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build();

        when(requestMock.getPathInfo()).thenReturn("/register");
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"username\":\"User1\", \"email\":\"user1@example.com\"}")));
        when(serviceMock.save(any(UserBaseDto.class))).thenReturn(userDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(UserBaseDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoNotValidReview() throws IOException, NotFoundRelationsException, NotValideEntityException {
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, \"username\":\"User1\", \"email\":\"user1@example.com\"}")));
        when(serviceMock.save(any(UserBaseDto.class))).thenThrow(new NotValideEntityException("Test"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).save(any(UserBaseDto.class));
        verify(responseMock, times(1)).setContentType("application/json");
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
    void testDoDeleteInvalidIdReview() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/afds");
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }



    @Test
    void testDoPostLoginUser() throws IOException, NoAuthorize, UserExistException {
        UserGetDto userGetDto = UserGetDto.copyFrom(UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build());
        userGetDto.setRole(RoleDto.builder()
                .id(1)
                .name("User")
                .build());

        when(requestMock.getPathInfo()).thenReturn("/login");
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"User1\", \"password\":\"password\"}")));
        when(serviceMock.login("User1", "password")).thenReturn(userGetDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(serviceMock, times(1)).login("User1", "password");
        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_ACCEPTED);
        verify(responseMock, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testDoPostNotAuthorize() throws IOException, NoAuthorize {
        UserGetDto userGetDto = UserGetDto.copyFrom(UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build());
        userGetDto.setRole(RoleDto.builder()
                .id(1)
                .name("User")
                .build());

        when(requestMock.getPathInfo()).thenReturn("/login");
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"User1\", \"password\":\"password\"}")));
        when(serviceMock.login("User1", "password")).thenThrow(new NoAuthorize("Вход не произошел"));
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPost(requestMock, responseMock);

        verify(responseMock, times(1)).setContentType("application/json");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @SneakyThrows
    @Test
    void testDoDeleteUser() {
        int userId = 1;
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(serviceMock.delete(userId)).thenReturn(true);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doDelete(requestMock, responseMock);

        verify(serviceMock, times(1)).delete(userId);
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPutUpdateUser() throws IOException, NoEntityException {
        UserBaseDto userDto = UserBaseDto.builder()
                .id(1)
                .username("User1")
                .email("User1@mail.ru")
                .password("password")
                .build();

        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, " +
                "\"username\":\"UpdatedUser\", " +
                "\"password\":\"password\", " +
                "\"email\":\"updated@example.com\"}")));
        when(serviceMock.update(any(UserBaseDto.class))).thenReturn(userDto);
        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        servlet.doPut(requestMock, responseMock);

        verify(serviceMock, times(1)).update(any(UserBaseDto.class));
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
        when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1, " +
                "\"username\":\"UpdatedUser\", " +
                "\"password\":\"password\", " +
                "\"email\":\"updated@example.com\"}")));        when(responseMock.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        when(serviceMock.update(any(UserBaseDto.class))).thenThrow(new NoEntityException("Test"));

        servlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
