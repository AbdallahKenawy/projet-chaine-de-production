/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operations;

import com.example.application.precedenceoperations;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;


public class Divprecedence extends Div{
    
    public Connection con;  
    private Grid<precedenceoperations> GridPrecedences;
    
    public Divprecedence() throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        setSizeFull();
        ComboBox<touteslesoperations> opavant=new ComboBox("Operation Avant");
        opavant.setItems(touteslesoperations.toutesLesOps(this.con));
        ComboBox<touteslesoperations> opapres=new ComboBox("Operation Apres");
        opapres.setItems(touteslesoperations.toutesLesOps(this.con));
        Button ajouteprecedence=new Button("Ajoute");
        HorizontalLayout detailsprecedence=new HorizontalLayout(opavant,opapres,ajouteprecedence);
        detailsprecedence.setAlignItems(FlexComponent.Alignment.BASELINE);
        detailsprecedence.setPadding(true);
        add(detailsprecedence);
        
        InitGridPrecedences();
        
        detailsprecedence.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        
        ajouteprecedence.addClickListener(e->{
            try {
                new precedenceoperations(opavant.getValue().getIdtoutesop(),opapres.getValue().getIdtoutesop()).saveprecedence(con);
                GridPrecedences.setItems(precedenceoperations.toutesLesPrecedences(con)); 
                opavant.clear();
                opapres.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
            opavant.clear();
            opapres.clear();
            Notification.show("Precedence Prise en compte").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
    }
    
    public void InitGridPrecedences() throws SQLException{
        GridPrecedences=new Grid(precedenceoperations.toutesLesPrecedences(this.con));
        Editor<precedenceoperations> editor=GridPrecedences.getEditor();

        Grid.Column<precedenceoperations> RefProduitColumn=GridPrecedences.addColumn(precedenceoperations::getRefproduit).setHeader("Reference Produit").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<precedenceoperations> DesOpAvant=GridPrecedences.addColumn(precedenceoperations::getDesopavant).setHeader("Operation Avant").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<precedenceoperations> DesOpApres=GridPrecedences.addColumn(precedenceoperations::getDesopapres).setHeader("Operation Apres").setTextAlign(ColumnTextAlign.CENTER);
        AtomicReference<precedenceoperations> modifiedprecedenceoperationsWrapper = new AtomicReference<>();

        Grid.Column<precedenceoperations> editColumn = GridPrecedences.addComponentColumn(precedenceoperations -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridPrecedences.getEditor().editItem(precedenceoperations);
                    modifiedprecedenceoperationsWrapper.set(precedenceoperations);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
       
        
        Binder<precedenceoperations> binder = new Binder<>(precedenceoperations.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        ComboBox <touteslesoperations> OpAvantField = new ComboBox();
        OpAvantField.setItems(touteslesoperations.toutesLesOps(con));
        OpAvantField.setWidthFull();
        binder.forField(OpAvantField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(precedenceoperations::getOpavant, precedenceoperations::setOpavant);
        DesOpAvant.setEditorComponent(OpAvantField);

        ComboBox <touteslesoperations> OpApresField = new ComboBox();
        OpApresField.setItems(touteslesoperations.toutesLesOps(con));
        OpApresField.setWidthFull();
        binder.forField(OpApresField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(precedenceoperations::getOpapres, precedenceoperations::setOpapres);
        DesOpApres.setEditorComponent(OpApresField);
        
        Button saveButton = new Button("Save", e -> {
            int modifiedIdOpAvant = OpAvantField.getValue().getIdtoutesop();
            int modifiedIdOpApres = OpApresField.getValue().getIdtoutesop();
            try {
                modifiedprecedenceoperationsWrapper.get().updatePrecedence(con, modifiedIdOpAvant, modifiedIdOpApres);
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
            dialog.setHeader("Supprimer cette precedence ?");
            dialog.setText(
                    "Vouliez-vous la supprimer? Cette opération supprimera aussi tous les éléments auxquels cette derniere est associée.");
            dialog.setCancelable(true);
            dialog.addCancelListener(event ->dialog.close());

            dialog.setConfirmText("Supprimer");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event ->{
                try {
                modifiedprecedenceoperationsWrapper.get().deletePrecedence(con);
                GridPrecedences.setItems(precedenceoperations.toutesLesPrecedences(con));
                Notification.show("precedenceoperations supprimee");
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
        
        editColumn.setVisible(NiveauAccess.isOperateur());
        editColumn.setVisible(NiveauAccess.isAdmin());
        GridPrecedences.setItems(precedenceoperations.toutesLesPrecedences(con));     
        GridPrecedences.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        GridPrecedences.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(GridPrecedences);
        
    }
        
}
