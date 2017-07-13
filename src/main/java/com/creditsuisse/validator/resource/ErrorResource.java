package com.creditsuisse.validator.resource;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Error Resource. If User send request, which will not be recognized by REST,
 *      or 401 error... etc... then user will see message "Oops... Something happened..."
 */
@RestController
public class ErrorResource implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "Oops... Something happened...";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
