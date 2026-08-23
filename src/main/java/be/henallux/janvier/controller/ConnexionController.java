package be.henallux.janvier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/connexion")
public class ConnexionController {

    @GetMapping
    public String showConnexion() {
        return "connexion";
    }
}

