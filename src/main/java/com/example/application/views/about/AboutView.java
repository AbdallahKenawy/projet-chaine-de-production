package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);

        Div backgroundImageDiv = new Div();
        backgroundImageDiv.getStyle().set("background-image", "url('images/MM.png')");
        backgroundImageDiv.getStyle().set("background-size", "contain");
        backgroundImageDiv.getStyle().set("background-repeat", "no-repeat");
        backgroundImageDiv.getStyle().set("background-position", "center");
        backgroundImageDiv.getStyle().set("height", "100%");
        backgroundImageDiv.getStyle().set("width", "100%");
        backgroundImageDiv.getStyle().set("position", "absolute");
        backgroundImageDiv.getStyle().set("opacity", "0.5");
        backgroundImageDiv.getStyle().set("z-index", "-1");
        add(backgroundImageDiv);
        
        H1 titre=new H1("Histoire");
        Paragraph paragraphe = new Paragraph("Fondée en 1960 par l'ingénieur visionnaire Jr. Uhry Frédéric, Altitude Engineering & Microsystems (AEM) allie l'aéronautique à l'informatique, se positionnant comme un pionnier de l'innovation. Partenaire clé de l'aérospatiale, AEM a façonné l'avenir du voyage aérien avec des solutions électroniques de pointe. De la navigation précise aux cockpits électroniques révolutionnaires, AEM demeure un leader mondial, étendant son influence à l'intelligence artificielle et à l'Internet des objets. L'héritage riche en réalisations et l'engagement continu envers l'innovation font d'AEM le choix privilégié pour les projets aérospatiaux majeurs, redéfinissant constamment l'expérience aéronautique avec des solutions avant-gardistes.");
        H1 titre2=new H1("Instructions");
        String fullDescription = """
            En tant qu'utilisateur, vous avez le droit de passer des commandes en choisissant des produits dans un catalogue et en spécifiant les quantités. Vous pouvez accéder à tous les onglets en tant que lecteur sans pouvoir modifier les informations du site.

            En tant qu'opérateur, vous partagez les mêmes droits que l'utilisateur, mais vous avez également la possibilité de modifier les paramètres dans chaque onglet. Vous pouvez gérer les machines, les opérations, les produits, les opérateurs, les emplois du temps et les postes de travail.

            Pour l'administrateur, toutes les fonctionnalités de l'opérateur sont disponibles, avec en plus la gestion des identifiants, des mots de passe, la réinitialisation des bases de données et l'accès à l'emploi du temps de tous les opérateurs.
        """;
        
        Paragraph paragraph2=new Paragraph(fullDescription);
        add(titre,paragraphe,titre2,paragraph2);
    }

}
