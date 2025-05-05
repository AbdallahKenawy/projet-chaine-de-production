/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.commande;

import com.example.application.Commande;
import com.example.application.Exemplaire;
import com.example.application.Produit;
import com.example.application.views.MainLayout;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@PageTitle("Commande")
@Route(value = "commande", layout = MainLayout.class)
public class CommandeView extends VerticalLayout{
    
    private Connection con;
    private VerticalLayout contenu;
    private ComboBox<Commande> refscommandes;
    private int indexcommande;
    
    public CommandeView() throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
                
        indexcommande=Commande.nbrCommande(con)+1;
        
        Button commander=new Button("Effectuer une commande",e->{
            try {
                List <Produit> prod=Produit.tousLesProduitsUsinables(con);
                if(prod.isEmpty()){
                    Notification.show("Veuillez verifier la disponibilite des machines et des operateurs").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }else{
                nouvellecommande(prod);
                        }
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
           }
        });
        
        this.refscommandes=new ComboBox();
        refscommandes.setAllowCustomValue(false);
        refscommandes.setLabel("Reference Commande");
        VerticalLayout vlayout=new VerticalLayout(commander,refscommandes);
        vlayout.setJustifyContentMode(JustifyContentMode.CENTER);
        vlayout.setAlignItems(Alignment.CENTER);
        add(vlayout);
        Tabs tabs=new Tabs();
        tabs.setSizeFull();
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.setAutoselect(false);
        add(tabs);
        this.contenu=new VerticalLayout();
        add(contenu);
        if(NiveauAccess.isAdmin() || NiveauAccess.isOperateur()){
                refscommandes.setItems(Commande.allcommandes(con));
            }else{
                refscommandes.setItems(Commande.commandsutilisateur(con));
        }
        refscommandes.addValueChangeListener(e->{
            try {
                if(refscommandes.getValue()!=null){
                    tabs.removeAll();
                    generatetabs(tabs,refscommandes.getValue().getRefcommande());
                }else{
                    Notification.show("Veuillez choisir une commande").addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        tabs.addSelectedChangeListener(e->{
            try {
                contenu.removeAll();
                if(e.getSelectedTab()!=null){
                    generategrid(refscommandes.getValue().getRefcommande(),e.getSelectedTab().getLabel());
                }
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        
        DateTimePicker tempsfincommande = new DateTimePicker();
        tempsfincommande.setLabel("Date de fin de commande");
        //tempsfincommande.setDatePickerI18n(formatfr);
        tempsfincommande.setDatePlaceholder("Date");
        tempsfincommande.setTimePlaceholder("Time");
        tempsfincommande.setReadOnly(true);
        //add(tempsfincommande);
    }

    public void generatetabs(Tabs tabs, String refcommande) throws SQLException {
    List<String> refsproduits = Commande.RefProduits(con, refcommande);
    tabs.removeAll();
    for (String ref : refsproduits) {
        tabs.add(new Tab(ref));
        }
    }
    
    public void generategrid(String refcommande,String refproduit) throws SQLException{
        Grid <Exemplaire> grid=new Grid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        List<Exemplaire> exemplaires = Commande.Exemplaires(con, refcommande, refproduit);
        grid.addComponentColumn(exemplaire -> {
            Span badge = new Span();
            badge.setText(exemplaire.getStatut());
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime dateFin = exemplaire.getDatefin().toLocalDateTime();
            if(exemplaire.getStatut().equals("Annulee")){
                badge.getElement().getThemeList().add("badge error");
            }else if (currentDateTime.isBefore(dateFin)) {
                badge.getElement().getThemeList().add("badge");
            }else{
                badge.setText("Confirmee");
                badge.getElement().getThemeList().add("badge success");
            }

            return badge;
        }).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(Exemplaire::getNumserie).setHeader("Numserie").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(Exemplaire::formulationdatedebut).setHeader("Date debut").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(Exemplaire::formulationdatefin).setHeader("Date fin").setTextAlign(ColumnTextAlign.CENTER);

        grid.setItems(exemplaires);
        contenu.add(grid);
    }
    
    public void nouvellecommande(List<Produit> list) throws SQLException{
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nouvelle Commande");
        VerticalLayout layout=new VerticalLayout();
        ComboBox<Produit> refsproduits=new ComboBox();
        refsproduits.setItems(list);
        dialog.add(layout);
        NumberField quantite=new NumberField();
        quantite.setMin(1);
        quantite.setStepButtonsVisible(true);
        Button ajoutecarte=new Button("Ajouter");
        ajoutecarte.setEnabled(false);
        refsproduits.addValueChangeListener(event ->{
            if(refsproduits.getValue()!=null){
                if(refsproduits.getValue().getStock()<1){
                    Notification.show("Pas assez de stock").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    quantite.clear();
                }else{
                    ajoutecarte.setEnabled(true);
                    quantite.setValue(quantite.getMin());
                    quantite.setMax(refsproduits.getValue().getStock());
                    quantite.addValueChangeListener(e->{
                        if(quantite.getValue()!=null){
                            if(quantite.getValue()>quantite.getMax()){
                                quantite.setValue(quantite.getMax());
                                Notification.show("Pas assez de stock :" + quantite.getMax() + " Exemplaires possibles").addThemeVariants(NotificationVariant.LUMO_WARNING);
                            }
                            if(quantite.getValue()<=0){
                                quantite.setValue(quantite.getMin());
                                Notification.show("Veuillez choisir le nombre d'exemplaires voulues").addThemeVariants(NotificationVariant.LUMO_WARNING);
                            }
                        }
                    });
                }
            }else{
                ajoutecarte.setEnabled(false);
            }
        });
                
        layout.add(new HorizontalLayout(refsproduits,quantite,ajoutecarte));
        
        List<Map<String, Object>> commandeItems = new ArrayList<>();
        Grid<Map<String, Object>> grid = new Grid<>();
        grid.addColumn(item -> item.get("refproduit")).setHeader("Refproduit").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(item -> item.get("quantity")).setHeader("Quantity").setTextAlign(ColumnTextAlign.CENTER);
        
        ajoutecarte.addClickListener(event -> {
            String selectedRefProduit = refsproduits.getValue().getRefproduit();
            int quantityValue = quantite.getValue().intValue();

            if (selectedRefProduit != null && quantityValue > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("refproduit", selectedRefProduit);
                item.put("quantity", quantityValue);
                commandeItems.add(item);
                grid.setItems(commandeItems);
                refsproduits.clear();
                quantite.clear();
                        }
                    });
           
        DatePickerI18n formatfr = new DatePickerI18n();
        formatfr.setDateFormat("dd/MM/yyyy");
        formatfr.setMonthNames(List.of("Janvier", "Fevrier", "Mars", "Avril",
        "Mai", "Juin", "Juillet", "Aout", "Septembere", "Octobre",
        "Novembere", "Decembre"));
        formatfr.setWeekdays(List.of("Dimanche", "Lundi", "Mardi",
        "Mercredi", "Jeudi", "Vendredi", "Dimanche"));
        formatfr.setWeekdaysShort(
        List.of("Di", "Lu", "Ma", "Me", "Je", "Ve", "Sa"));
        formatfr.setToday("Aujourd'hui");
        formatfr.setCancel("Annuler");

        DateTimePicker tempsdebutcommande = new DateTimePicker();
        tempsdebutcommande.setLocale(new Locale("fr"));
        tempsdebutcommande.setDatePickerI18n(formatfr);
        tempsdebutcommande.setLabel("Date de debut de commande");
        tempsdebutcommande.setDatePlaceholder("Date");
        tempsdebutcommande.setTimePlaceholder("Time");
        tempsdebutcommande.setHelperText("Ouvert Lundi-Vendredi, 9:00 - 17:00");
        tempsdebutcommande.setMin(LocalDateTime.now().plusDays(1));

        Button confirmecommande=new Button("Confirm commande");
        tempsdebutcommande.addValueChangeListener(event -> {
        LocalDateTime selectedDateTime = event.getValue();
            if (selectedDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || selectedDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
                Notification.show("Les week-ends ne sont pas autorisés. Veuillez choisir un jour de la semaine.").addThemeVariants(NotificationVariant.LUMO_WARNING);
                confirmecommande.setEnabled(false);
            }else if (selectedDateTime.getHour() < 9 || selectedDateTime.getHour() > 17) {
                Notification.show("L'heure sélectionnée doit être entre 9h et 17h.").addThemeVariants(NotificationVariant.LUMO_WARNING);
                confirmecommande.setEnabled(false);
            }else{
                confirmecommande.setEnabled(true);
            }
        });
        confirmecommande.addClickListener(event -> {
                if(commandeItems.isEmpty()){
                    Notification.show("Ajouter des produits a votre panier").addThemeVariants(NotificationVariant.LUMO_WARNING);
                }else{   
            Commande commande=new Commande("CMD|"+tempsdebutcommande.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE)+"|"+indexcommande,Timestamp.valueOf(tempsdebutcommande.getValue()),NiveauAccess.getIDUtilisateur());
            indexcommande=indexcommande+1;
            try {
                commande.savecommande(con);
                Timestamp debutfabrication=Timestamp.valueOf(commande.getTempsdebut().toLocalDateTime().plusMinutes(5));
                Timestamp finfabrication=Timestamp.valueOf(commande.getTempsdebut().toLocalDateTime().plusMinutes(50));
                forlist:
                for (Map<String, Object> item : commandeItems) {
                    String refproduit = (String) item.get("refproduit");
                    int quantity = (int) item.get("quantity");
                    Produit produit=Produit.getproduitbyRef(con, refproduit);
                    produit.updateProduit(con, (float) (produit.getStock()-quantity));                    
                    for (int i = 0; i < quantity; i++) {
                       if(debutfabrication.toLocalDateTime().toLocalTime().isAfter(LocalTime.of(17, 00)) || finfabrication.toLocalDateTime().toLocalTime().isAfter(LocalTime.of(17, 30))){
                        LocalDateTime debutLocalDateTime = debutfabrication.toLocalDateTime().plusDays(1).withHour(9).withMinute(0);
                        if(debutLocalDateTime.getDayOfWeek()==DayOfWeek.SATURDAY || debutLocalDateTime.getDayOfWeek() == DayOfWeek.SUNDAY){
                            debutLocalDateTime=debutLocalDateTime.plusDays(2).withHour(9).withMinute(0);
                            }
                            debutfabrication=Timestamp.valueOf(debutLocalDateTime);                       
                            }
                           Exemplaire temp=new Exemplaire(commande.getRefcommande(),refproduit,commande.getRefcommande()+"|"+refproduit+"-"+i,debutfabrication);                                              
                           temp.GammeFab(con);
                           finfabrication=temp.getDatefin();
                           commande.setTempsfin(finfabrication);
                           debutfabrication=Timestamp.valueOf(finfabrication.toLocalDateTime().plusMinutes(5));                            
                           
                          }
                       }
                    if(NiveauAccess.isAdmin() || NiveauAccess.isOperateur()){
                        refscommandes.setItems(Commande.allcommandes(con));
                    }else{
                        refscommandes.setItems(Commande.commandsutilisateur(con));
                         }
                } catch (SQLException ex) {
                    Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    ex.printStackTrace();
                }
            dialog.close();
                }
           });
           layout.setAlignItems(Alignment.CENTER);
           layout.setJustifyContentMode(JustifyContentMode.CENTER);
           layout.add(new HorizontalLayout(refsproduits, quantite, ajoutecarte));
           layout.add(grid);
           layout.add(tempsdebutcommande);
           layout.add(confirmecommande);
           dialog.add(layout);
               
           dialog.open();
       }
   
}

