package be.henallux.janvier.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import be.henallux.janvier.exception.RessourceIntrouvableException;

/**
 * Gestion centralisee des erreurs.
 *
 * Aucune trace technique n'est renvoyee au navigateur : l'utilisateur voit une
 * page d'erreur traduite, le detail part dans les logs du serveur.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView ressourceIntrouvable(RessourceIntrouvableException exception) {
        ModelAndView modelAndView = new ModelAndView("erreur");
        modelAndView.addObject("cleErreur", exception.getCleMessage());
        modelAndView.addObject("codeErreur", HttpStatus.NOT_FOUND.value());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView erreurInattendue(Exception exception, HttpServletRequest request) {
        LOGGER.error("Erreur inattendue sur {}", request.getRequestURI(), exception);
        ModelAndView modelAndView = new ModelAndView("erreur");
        modelAndView.addObject("cleErreur", "error.unexpected");
        modelAndView.addObject("codeErreur", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return modelAndView;
    }
}
