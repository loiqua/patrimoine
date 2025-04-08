package school.hei.patrimoine.cas;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class BakoPatrimoineTest {

    @Test
    void testBakoPatrimoine() {
        // Date de début : 8 avril 2025
        LocalDate startDate = LocalDate.of(2025, APRIL, 8);
        // Date de fin : 31 décembre 2025
        LocalDate endDate = LocalDate.of(2025, DECEMBER, 31);

        // Création de la personne Bako
        Personne bako = new Personne("Bako");

        // Création des comptes initiaux
        Compte compteBNI = new Compte("Compte BNI", startDate, ariary(2_000_000));
        Compte compteBMOI = new Compte("Compte BMOI", startDate, ariary(625_000));
        Compte coffreFort = new Compte("Coffre fort", startDate, ariary(1_750_000));

        // Création de l'ordinateur portable
        Materiel ordinateur = new Materiel("Ordinateur Portable", startDate, startDate, ariary(3_000_000), -0.12);

        // Création des transactions récurrentes
        // Salaire mensuel (2 du mois)
        new FluxArgent(
            "Salaire",
            compteBNI,
            startDate,
            endDate,
            2,
            ariary(2_125_000)
        );

        // Virement épargne (3 du mois)
        new FluxArgent(
            "Virement épargne",
            compteBNI,
            startDate,
            endDate,
            3,
            ariary(-200_000)
        );

        // Loyer (26 du mois)
        new FluxArgent(
            "Loyer",
            compteBNI,
            startDate,
            endDate,
            26,
            ariary(-600_000)
        );

        // Dépenses mensuelles (1 du mois)
        new FluxArgent(
            "Dépenses mensuelles",
            compteBNI,
            startDate,
            endDate,
            1,
            ariary(-700_000)
        );

        // Création du patrimoine
        Patrimoine patrimoine = Patrimoine.of("Bako", MGA, startDate, bako, Set.of(compteBNI, compteBMOI, coffreFort, ordinateur));

        // Calcul de la valeur finale
        Patrimoine patrimoineFinal = patrimoine.projectionFuture(endDate);
        int valeurFinale = parseMontant(patrimoineFinal.getValeurComptable());
        System.out.println("Valeur totale du patrimoine au 31 décembre 2025 : " + valeurFinale + " Ar");
    }
} 