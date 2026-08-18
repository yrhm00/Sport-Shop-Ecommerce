/* ============================================================
   Comportements communs du site.
   ------------------------------------------------------------
   Ce fichier remplace les anciens attributs onclick / onchange
   ecrits dans les pages : la politique de securite du contenu
   (script-src 'self') interdit tout script inline.
   ============================================================ */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {

        /* Demande de confirmation avant une action destructive.
           Le message est fourni par la page dans l'attribut data-confirm,
           ce qui permet de le traduire cote serveur. */
        document.querySelectorAll('[data-confirm]').forEach(function (element) {
            element.addEventListener('submit', function (event) {
                if (!window.confirm(element.getAttribute('data-confirm'))) {
                    event.preventDefault();
                }
            });
        });
    });
})();
