package com.course.restbasics;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * @Controller vs @RestController - side by side.
 *
 * A plain @Controller is meant for SERVER-SIDE HTML pages (topic 36, Thymeleaf):
 * by default its String return value is treated as a VIEW NAME (a template file to render).
 *
 * Here we add @ResponseBody to one method, which means "write my return value directly into
 * the response body". @RestController simply puts @ResponseBody on EVERY method for you.
 *
 * Try: http://localhost:8080/
 */
@Controller
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() {
        return """
                <html>
                  <body style="font-family: sans-serif">
                    <h1>Book API is running</h1>
                    <p>This HTML came from a <b>@Controller</b> method with <b>@ResponseBody</b>.</p>
                    <p>Try the JSON API: <a href="/api/books">/api/books</a></p>
                  </body>
                </html>
                """;
    }
}
