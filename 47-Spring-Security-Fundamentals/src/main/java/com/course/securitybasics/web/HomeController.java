package com.course.securitybasics.web;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * HomeController - a tiny HTML page for trying FORM LOGIN in the browser.
 *
 * 1. Open http://localhost:8080/ in the browser.
 * 2. You are redirected to /login (Spring's built-in login page).
 * 3. Log in as student / student123 -> you come back here and see your name.
 * 4. Click "Log out" -> Spring shows a confirmation page (GET /logout) whose button
 *    sends POST /logout together with the hidden CSRF token.
 */
@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home(Principal principal) {
        return """
                <html><body style="font-family:sans-serif">
                <h1>Welcome to the College Portal, %s!</h1>
                <p>You logged in with the form login page. Your browser now holds a
                   <code>JSESSIONID</code> cookie, so you stay logged in.</p>
                <p><a href="/api/me">See /api/me as JSON</a></p>
                <p><a href="/logout">Log out</a></p>
                </body></html>
                """.formatted(principal.getName());
    }
}
