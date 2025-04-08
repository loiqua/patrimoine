package school.hei.patrimoine.cas;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
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

class BakoPatrimoineGraphTest {

    @Test
    void testBakoPatrimoineGraph() {
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
            .title("Évolution du Patrimoine de Bako (Avril - Décembre 2025)")
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