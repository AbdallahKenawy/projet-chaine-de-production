/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.produits;

import com.example.application.Exemplaire;
import com.example.application.Produit;
import com.example.application.views.MainLayout;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Produits")
@Route(value = "produits", layout = MainLayout.class)
public class ProduitsView extends VerticalLayout {
    
    private Connection con;
    private Grid<Produit> GridProduit;
    private VerticalLayout GammeFabrication;
    private VerticalLayout Produitsconcus;
    private ComboBox<Produit> ListeProduits;
    
    public ProduitsView() throws SQLException{
        

        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        TextField Reference=new TextField("Reference");
        TextField Description=new TextField("Description");
        TextField Stock=new TextField("Stock");
        Button ajouteproduit=new Button("Ajoute Produit");
        HorizontalLayout detailsproduit=new HorizontalLayout(Reference,Description,Stock,ajouteproduit);
        detailsproduit.setAlignItems(Alignment.BASELINE);
        detailsproduit.setJustifyContentMode(JustifyContentMode.CENTER);
        detailsproduit.setPadding(true);
        add(detailsproduit);
        
        InitGridProduit();
        detailsproduit.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        ajouteproduit.addClickListener(clickEvent -> {
            String ref=Reference.getValue();
            String des=Description.getValue();
            Float stock=Float.parseFloat(Stock.getValue());
            try {
                new Produit(ref,des,stock).saveproduit(this.con);
                Notification.show("Produit rajoute successivement!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                GridProduit.setItems(Produit.tousLesProduits(this.con));
                Reference.clear();
                Description.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        
        H2 gammefab=new H2("Gamme de fabrication");
        ListeProduits=new ComboBox();
        ListeProduits.setItems(Produit.tousLesProduits(con));
        ListeProduits.addValueChangeListener(e->{
            try {
                GenerateGammeFabrication();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        GammeFabrication=new VerticalLayout();
        Produitsconcus=new VerticalLayout();        
        
        H2 historique=new H2("Historique");
        DateTimePicker debut=new DateTimePicker();
        DateTimePicker fin=new DateTimePicker();
        Button affiche=new Button("Afficher");
        affiche.addClickListener(e->{
            if(debut.getValue()!=null && fin.getValue()!=null){
                try {
                    Produitdansperiode(Timestamp.valueOf(debut.getValue()), Timestamp.valueOf(fin.getValue()));
                } catch (SQLException ex) {
                    Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    ex.printStackTrace();
                }
            }
        });
        HorizontalLayout exemplaireperiode=new HorizontalLayout(debut,fin,affiche);
        exemplaireperiode.setAlignItems(Alignment.BASELINE);
        exemplaireperiode.setJustifyContentMode(JustifyContentMode.CENTER);
        exemplaireperiode.setPadding(true);
        add(gammefab,ListeProduits,GammeFabrication,historique,exemplaireperiode,Produitsconcus);
    }
    
    public void InitGridProduit () throws SQLException{
        
        GridProduit=new Grid(Produit.tousLesProduits(this.con));
        Editor<Produit> editor=GridProduit.getEditor();
        
        Grid.Column<Produit> IdColumn=GridProduit.addColumn(Produit::getIdproduit).setHeader("Id Produit").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Produit> RefColumn=GridProduit.addColumn(Produit::getRefproduit).setHeader("Reference").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Produit> DesColumn=GridProduit.addColumn(Produit::getDesproduit).setHeader("Description").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Produit> StockColumn=GridProduit.addColumn(Produit::getStock).setHeader("Stock").setTextAlign(ColumnTextAlign.CENTER);
        
        AtomicReference<Produit> produitmodifieewrapper=new AtomicReference();
        
        Grid.Column<Produit> editColumn = GridProduit.addComponentColumn(produit -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridProduit.getEditor().editItem(produit);
                    produitmodifieewrapper.set(produit);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0);
        
        Binder<Produit> binder = new Binder<>(Produit.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        TextField RefField = new TextField();
        RefField.setWidthFull();
        binder.forField(RefField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Produit::getRefproduit, Produit::setRefproduit);
        RefColumn.setEditorComponent(RefField);

        TextField DesField = new TextField();
        DesField.setWidthFull();
        binder.forField(DesField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(Produit::getDesproduit, Produit::setDesproduit);
        DesColumn.setEditorComponent(DesField);

        TextField StockField = new TextField();
        StockField.setWidthFull();
        binder.forField(StockField)
                .asRequired("Puissance invalide")
                .withConverter(new StringToFloatConverter("Invalid number format"))
                //.withStatusLabel(emailValidationMessage)
                .bind(Produit::getStock, Produit::setStock);
        StockColumn.setEditorComponent(StockField);
        
        Button saveButton = new Button("Save", e -> {
            String modifiedReference = RefField.getValue();
            String modifiedDescription = DesField.getValue();
            Float modifiedStock= Float.parseFloat(StockField.getValue());
            try {
                produitmodifieewrapper.get().updateProduit(con, modifiedReference, modifiedDescription, modifiedStock);
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
            editor.save();
                });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        Button deleteButton=new Button(VaadinIcon.TRASH.create(), e ->{
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Supprimer Operateur ?");
                dialog.setText(
                        "Vouliez-vous supprimer l'opérateur? Cette opération supprimera aussi tous les éléments auxquels ce dernier est associé.");
                dialog.setCancelable(true);
                dialog.addCancelListener(event ->dialog.close());

                dialog.setConfirmText("Supprimer");
                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(event ->{
                    try {
                        produitmodifieewrapper.get().deleteProduit(con);
                        GridProduit.setItems(Produit.tousLesProduits(con));
                        Notification.show("Produit supprimee");
                        dialog.close();
                    } catch (SQLException ex) {
                        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }
                });
                dialog.open();
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_CONTRAST);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                deleteButton,cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        editColumn.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        GridProduit.setItems(Produit.tousLesProduits(con));
        GridProduit.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        GridProduit.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(GridProduit);
    }
    
    public void GenerateGammeFabrication() throws SQLException{
        GammeFabrication.removeAll();
        if(ListeProduits.getValue()!=null){
            List<String[]> list=ListeProduits.getValue().Opsassociees(con);
            Grid<String[]> grid = new Grid<>();
            Grid.Column<String[]> opavantColumn=grid.addColumn(row -> row[0]).setHeader("opavant").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> destypeavantColumn=grid.addColumn(row -> row[1]).setHeader("Type").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> refmachineavantColumn=grid.addColumn(row -> row[2]).setHeader("Ref Machine").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> dureeavantColumn=grid.addColumn(row -> row[3]).setHeader("Duree Operation").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> opapresColumn=grid.addColumn(row -> row[4]).setHeader("opapres").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> destypeapresColumn=grid.addColumn(row -> row[5]).setHeader("Type").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> refmachineapresColumn=grid.addColumn(row -> row[6]).setHeader("Ref Machine").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> dureeapresColumn=grid.addColumn(row -> row[7]).setHeader("Duree Operation").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> DureeTotaleColumn=grid.addColumn(row -> row[8]).setHeader("Duree Totale").setTextAlign(ColumnTextAlign.CENTER);
            
            HeaderRow headerRow = grid.prependHeaderRow();
            headerRow.join(opavantColumn, destypeavantColumn,refmachineavantColumn,dureeavantColumn).setText("Operation Precedente");
            headerRow.join(opapresColumn, destypeapresColumn, refmachineapresColumn, dureeapresColumn).setText("Operation Suivante");
            
            grid.setItems(list);
            grid.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            GammeFabrication.add(grid);
        }
    }
    
    public void Produitdansperiode(Timestamp debut,Timestamp fin) throws SQLException{
        Produitsconcus.removeAll();
        List<Exemplaire> list=Exemplaire.produitsdansperiode(con,debut,fin);               
        Grid<Exemplaire> grid = new Grid<>();
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
        Grid.Column<Exemplaire> RefcommandeColumn=grid.addColumn(Exemplaire::getRefcommande).setHeader("Reference commande").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Exemplaire> RefproduitColumn=grid.addColumn(Exemplaire::getRefproduit).setHeader("Reference produit").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Exemplaire> debutColumn=grid.addColumn(Exemplaire::formulationdatedebut).setHeader("Date debut").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Exemplaire> finColumn=grid.addColumn(Exemplaire::formulationdatefin).setHeader("Date fin").setTextAlign(ColumnTextAlign.CENTER);
        grid.setItems(list);
        grid.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        Produitsconcus.add(grid);
    }
}
