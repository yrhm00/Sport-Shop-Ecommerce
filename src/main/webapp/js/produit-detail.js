/* ============================================================
   Page detail produit : adapte la quantite maximale et le bouton
   d'ajout au stock de la taille selectionnee.
   Les libelles sont lus depuis les attributs data-* du formulaire
   pour rester traduits.
   ============================================================ */
(function () {
    'use strict';

    function majStock(select, quantite, bouton, libelles) {
        var option = select.options[select.selectedIndex];
        var stock = parseInt(option.getAttribute('data-stock'), 10);

        if (isNaN(stock) || stock <= 0) {
            quantite.disabled = true;
            bouton.disabled = true;
            bouton.classList.remove('btn-success');
            bouton.classList.add('btn-secondary');
            bouton.textContent = libelles.rupture;
            return;
        }

        quantite.max = stock;
        if (parseInt(quantite.value, 10) > stock) {
            quantite.value = stock;
        }
        quantite.disabled = false;
        bouton.disabled = false;
        bouton.classList.remove('btn-secondary');
        bouton.classList.add('btn-success');
        bouton.textContent = libelles.ajouter;
    }

    document.addEventListener('DOMContentLoaded', function () {
        var formulaire = document.getElementById('form-ajout-panier');
        var select = document.getElementById('taille');
        var quantite = document.getElementById('quantite');
        var bouton = document.getElementById('btn-add');

        if (!formulaire || !select || !quantite || !bouton) {
            return;
        }

        var libelles = {
            ajouter: formulaire.getAttribute('data-libelle-ajouter'),
            rupture: formulaire.getAttribute('data-libelle-rupture')
        };

        select.addEventListener('change', function () {
            majStock(select, quantite, bouton, libelles);
        });
        majStock(select, quantite, bouton, libelles);
    });
})();
