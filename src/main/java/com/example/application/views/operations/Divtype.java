/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operations;

import com.example.application.Typeoperation;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;


public class Divtype extends Div{

    private Connection con;
    private Grid<Typeoperation> GridType;
            
    public Divtype ()throws SQLException{
            
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        setSizeFull();
        TextField Description=new TextField("Description");
        Button ajoutetype=new Button("Ajoute");
        HorizontalLayout nouveautype=new HorizontalLayout(Description,ajoutetype);
        nouveautype.setAlignItems(FlexComponent.Alignment.BASELINE);
        nouveautype.setPadding(true);
        add(nouveautype);
        
        InitGridType();
        
        nouveautype.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        ajoutetype.addClickListener(clickEvemt ->{
            String des=Description.getValue();
            try {
                new Typeoperation(des).savetypeoperation(this.con);
                Notification noti = Notification.show("Type d'operation rajoute successivement!");
                noti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                GridType.setItems(Typeoperation.tousLestypeoperations(this.con));
                Description.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
            
        });
    }
        
    public void InitGridType() throws SQLException{
        GridType=new Grid();
        Editor<Typeoperation> editor=GridType.getEditor();
        
        Grid.Column<Typeoperation> IdColumn=GridType.addColumn(Typeoperation::getIdtype).setHeader("Id Typeoperation").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Typeoperation> DesColumn=GridType.addColumn(Typeoperation::getDestype).setHeader("Description").setTextAlign(ColumnTextAlign.CENTER);
        AtomicReference<Typeoperation> Typemodifieewrapper=new AtomicReference();
        Grid.Column<Typeoperation> editColumn = GridType.addComponentColumn(typeoperation -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridType.getEditor().editItem(typeoperation);
                    Typemodifieewrapper.set(typeoperation);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
        
        Binder<Typeoperation> binder = new Binder<>(Typeoperation.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        TextField DesField = new TextField();
        DesField.setWidthFull();
        binder.forField(DesField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(Typeoperation::getDestype, Typeoperation::setDestype);
        DesColumn.setEditorComponent(DesField);
        
        Button saveButton = new Button("Save", e -> {
            String modifiedDescription = DesField.getValue();
            try {
                Typemodifieewrapper.get().updatetypeoperation(con,modifiedDescription);
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
                dialog.setHeader("Supprimer Type Operation ?");
                dialog.setText(
                        "Vouliez-vous supprimer le type d'operation? Cette opération supprimera aussi tous les éléments auxquels ce dernier est associé.");
                dialog.setCancelable(true);
                dialog.addCancelListener(event ->dialog.close());

                dialog.setConfirmText("Supprimer");
                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(event ->{
                    try {
                        Typemodifieewrapper.get().deletetypeoperation(con);
                        GridType.setItems(Typeoperation.tousLestypeoperations(con));
                        Notification.show("Typeoperation supprimee");
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
        GridType.setItems(Typeoperation.tousLestypeoperations(con));
        GridType.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        GridType.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(GridType);
        }
    
}
