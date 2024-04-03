package com.luidmidev.template.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SPAController {
    /**
     * Retorna la página principal de la aplicación React.
     *
     * @return El nombre de la página principal.
     */
    @GetMapping(value = {
            "/",
            "/login",
            "/register",
    })
    public String index() {
        return "forward:/index.html";
    }
}