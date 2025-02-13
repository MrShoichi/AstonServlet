package ru.shoichi.films.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class JwtCookieUtil {
    private static final String COOKIE_AUTHORIZE = "Authorization";
    private static final int MAX_AGE = 60 * 60;

    public static Cookie createCookieAuthorization(String token) {
        Cookie cookie = new Cookie(COOKIE_AUTHORIZE, token);
        cookie.setMaxAge(MAX_AGE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public static String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_AUTHORIZE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
