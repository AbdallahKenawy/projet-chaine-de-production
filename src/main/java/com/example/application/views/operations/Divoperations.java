/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operations;

import com.example.application.Produit;
import com.example.application.Typeoperation;
import com.example.application.touteslesoperations;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;


public class Divoperations extends Div{
    
    private Connection con;
    private Grid<touteslesoperations> GridToutesOps;
    
    public Divoperations () throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        setSizeFull();
        ComboBox <Typeoperation> idtype=new ComboBox("Id Type Operation");
        idtype.setItems(Typeoperation.tousLestypeoperations(this.con));
        ComboBox <Produit> idproduit=new ComboBox("Id Produit");
        idproduit.setItems(Produit.tousLesProduits(this.con));
        Button ajouteop=new Button("Ajoute");
        HorizontalLayout detailsop=new HorizontalLayout(idtype,idproduit,ajouteop);
        detailsop.setAlignItems(Alignment.BASELINE);
        detailsop.setPadding(true);
        add(detailsop);
        
        InitGridToutesOp();
        detailsop.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        ajouteop.addClickListener(clickEvemt ->{
            int IDtype = idtype.getValue().getIdtype();
            int IDproduit=idproduit.getValue().getIdproduit();
            try {
                new touteslesoperations(IDtype,IDproduit).saveoperation(this.con);
                Notification.show("Operation rajoute successivement!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                GridToutesOps.setItems(touteslesoperations.toutesLesOps(this.con));
                idtype.clear();
                idproduit.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
            
        });
    }
    
    public void InitGridToutesOp() throws SQLException{
        GridToutesOps=new Grid(touteslesoperations.toutesLesOps(this.con));
        Editor<touteslesoperations> editor=GridToutesOps.getEditor();
        
        Grid.Column<touteslesoperations> IdOpColumn=GridToutesOps.addColumn(touteslesoperations::getIdtoutesop).setHeader("Reference").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<touteslesoperations> RefProduitColumn=GridToutesOps.addColumn(touteslesoperations::getRefproduit).setHeader("Produit").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<touteslesoperations> DesTypeColumn=GridToutesOps.addColumn(touteslesoperations::getDestype).setHeader("Operation").setTextAlign(ColumnTextAlign.CENTER);
        AtomicReference<touteslesoperations> modifiedtouteslesoperationsWrapper = new AtomicReference<>();

        Grid.Column<touteslesoperations> editColumn = GridToutesOps.addComponentColumn(touteslesoperations -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridToutesOps.getEditor().editItem(touteslesoperations);
                    modifiedtouteslesoperationsWrapper.set(touteslesoperations);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
       
        
        Binder<touteslesoperations> binder = new Binder<>(touteslesoperations.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        ComboBox <Produit> RefProduitField = new ComboBox();
        RefProduitField.setItems(Produit.tousLesProduits(con));
        RefProduitField.setWidthFull();
        binder.forField(RefProduitField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(touteslesoperations::getProduit, touteslesoperations::setProduit);
        RefProduitColumn.setEditorComponent(RefProduitField);

        ComboBox <Typeoperation> DesTypeField = new ComboBox();
        DesTypeField.setItems(Typeoperation.tousLestypeoperations(con));
        DesTypeField.setWidthFull();
        binder.forField(DesTypeField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(touteslesoperations::getTypeop, touteslesoperations::setTypeop);
        DesTypeColumn.setEditorComponent(DesTypeField);
        
        Button saveButton = new Button("Save", e -> {
            int modifiedIdProduit = RefProduitField.getValue().getIdproduit();
            int modifiedIdType = DesTypeField.getValue().getIdtype();
            try {
                modifiedtouteslesoperationsWrapper.get().updateOperation(con, modifiedIdProduit, modifiedIdType);
                //touteslesoperations.updatetouteslesoperations(con,modifiedIdMachine, modifiedIdType, modifiedDuree,modifiedIdposte);
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
            dialog.setHeader("Supprimer cette étape de fabrication ?");
            dialog.setText(
                    "Vouliez-vous la supprimer? Cette opération supprimera aussi tous les éléments auxquels cette derniere est associée.");
            dialog.setCancelable(true);
            dialog.addCancelListener(event ->dialog.close());

            dialog.setConfirmText("Supprimer");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event ->{
                try {
                modifiedtouteslesoperationsWrapper.get().deleteOperation(con);
                GridToutesOps.setItems(touteslesoperations.toutesLesOps(con));
                Notification.show("touteslesoperations supprimee");
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

//        editor.addCancelListener(e -> {
//            firstNameValidationMessage.setText("");
//            lastNameValidationMessage.setText("");
//            emailValidationMessage.setText("");
//        });
        
        editColumn.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        GridToutesOps.setItems(touteslesoperations.toutesLesOps(con));
        GridToutesOps.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        GridToutesOps.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(GridToutesOps);
    }
}
