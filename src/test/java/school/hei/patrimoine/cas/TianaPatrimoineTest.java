package school.hei.patrimoine.cas;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.modele.evolution.SerieComptableTemporelle.parseMontant;
import static school.hei.patrimoine.modele.Devise.MGA;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.FluxArgent;
import school.hei.patrimoine.modele.possession.Materiel;

class TianaPatrimoineTest {

    @Test
    void testTianaPatrimoine() {
        // Date de début : 8 avril 2025
        LocalDate startDate = LocalDate.of(2025, APRIL, 8);
        // Date de fin : 31 mars 2026
        LocalDate endDate = LocalDate.of(2026, MARCH, 31);

        // Création de la personne Tiana
        Personne tiana = new Personne("Tiana");

        // Création du compte bancaire initial
        Compte compteBancaire = new Compte("Compte Bancaire", startDate, ariary(60_000_000));

        // Création du terrain bâti (appréciation de 10% par an)
        Materiel terrain = new Materiel("Terrain Bâti", startDate, startDate, ariary(100_000_000), 0.10);

        // Dépenses mensuelles (1 du mois)
        new FluxArgent(
            "Dépenses familiales",
            compteBancaire,
            startDate,
            endDate,
            1,
            ariary(-4_000_000)
        );

        // Projet entrepreneurial
        // Dépenses mensuelles (5 du mois) de juin à décembre
        new FluxArgent(
            "Dépenses projet",
            compteBancaire,
            LocalDate.of(2025, MAY, 1),
            LocalDate.of(2025, DECEMBER, 31),
            5,
            ariary(-5_000_000)
        );

        // Avance de 10% (1 mai 2025)
        new FluxArgent(
            "Avance projet",
            compteBancaire,
            LocalDate.of(2025, MAY, 1),
            LocalDate.of(2025, MAY, 1),
            1,
            ariary(7_000_000)
        );

        // Solde de 90% (31 janvier 2026)
        new FluxArgent(
            "Solde projet",
            compteBancaire,
            LocalDate.of(2026, JANUARY, 31),
            LocalDate.of(2026, JANUARY, 31),
            31,
            ariary(63_000_000)
        );

        // Prêt bancaire
        // Réception du prêt (27 juillet 2025)
        new FluxArgent(
            "Réception prêt",
            compteBancaire,
            LocalDate.of(2025, JULY, 27),
            LocalDate.of(2025, JULY, 27),
            27,
            ariary(20_000_000)
        );

        // Remboursements mensuels (27 du mois) d'août 2025 à juillet 2026
        new FluxArgent(
            "Remboursement prêt",
            compteBancaire,
            LocalDate.of(2025, AUGUST, 27),
            LocalDate.of(2026, JULY, 27),
            27,
            ariary(-2_000_000)
        );

        // Création du patrimoine
        Patrimoine patrimoine = Patrimoine.of("Tiana", MGA, startDate, tiana, Set.of(compteBancaire, terrain));

        // Calcul de la valeur finale
        Patrimoine patrimoineFinal = patrimoine.projectionFuture(endDate);
        int valeurFinale = parseMontant(patrimoineFinal.getValeurComptable());
        System.out.println("Valeur totale du patrimoine au 31 mars 2026 : " + valeurFinale + " Ar");
    }
} 