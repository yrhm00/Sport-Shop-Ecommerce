package be.henallux.janvier.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

/**
 * Nettoyage des saisies utilisateur (prevention XSS stocke).
 *
 * Les champs du site sont du texte simple : la politique retenue supprime donc
 * la totalite du balisage HTML avant l'enregistrement en base. L'echappement a
 * l'affichage (c:out / fn:escapeXml dans les JSP) reste la seconde barriere.
 */
@Service
public class SanitizationService {

    /** Politique "texte pur" : aucun element ni attribut HTML autorise. */
    private static final PolicyFactory TEXTE_SIMPLE = new HtmlPolicyBuilder().toFactory();

    /**
     * Renvoie la valeur debarrassee de tout balisage HTML et des espaces inutiles.
     * Renvoie null si l'entree est null.
     */
    public String nettoyer(String valeur) {
        if (valeur == null) {
            return null;
        }
        // Le sanitizer encode les caracteres speciaux : on les redecode pour
        // conserver un texte lisible en base (l'echappement se fait a l'affichage).
        String sansHtml = TEXTE_SIMPLE.sanitize(valeur);
        return org.springframework.web.util.HtmlUtils.htmlUnescape(sansHtml).trim();
    }
}
