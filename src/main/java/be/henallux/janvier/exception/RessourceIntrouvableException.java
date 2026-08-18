package be.henallux.janvier.exception;

/**
 * Levee quand une ressource demandee dans l'URL n'existe pas en base
 * (produit ou categorie inconnu). Traduite en page 404 par le GlobalExceptionHandler.
 */
public class RessourceIntrouvableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Cle de message i18n a afficher a l'utilisateur. */
    private final String cleMessage;

    public RessourceIntrouvableException(String cleMessage) {
        super(cleMessage);
        this.cleMessage = cleMessage;
    }

    public String getCleMessage() {
        return cleMessage;
    }
}
