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

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.SwingWrapper;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.FluxArgent;
import school.hei.patrimoine.modele.possession.Materiel;

class TianaPatrimoineGraphTest {

    @Test
    void testTianaPatrimoineGraph() {
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

        // Création des données pour le graphique
        List<LocalDate> dates = new ArrayList<>();
        List<Double> valeurs = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            Patrimoine patrimoineProjete = patrimoine.projectionFuture(currentDate);
            valeurs.add(parseMontant(patrimoineProjete.getValeurComptable()) / 1_000_000.0); // Conversion en millions
            currentDate = currentDate.plusDays(1);
        }

        // Création du graphique
        XYChart chart = new XYChartBuilder()
            .width(1200)
            .height(800)
            .title("Évolution du Patrimoine de Tiana (Avril 2025 - Mars 2026)")
            .xAxisTitle("Date")
            .yAxisTitle("Valeur (Millions Ar)")
            .build();

        // Personnalisation du style
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setXAxisTicksVisible(true);
        chart.getStyler().setYAxisTicksVisible(true);
        chart.getStyler().setXAxisLabelRotation(45);

        // Ajout des données
        chart.addSeries("Patrimoine", 
            dates.stream().mapToDouble(d -> d.toEpochDay()).toArray(),
            valeurs.stream().mapToDouble(Double::doubleValue).toArray());

        // Affichage du graphique
        JFrame frame = new SwingWrapper<>(chart).displayChart();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Attendre que l'utilisateur ferme la fenêtre
        try {
            Thread.sleep(30000); // Attendre 30 secondes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 