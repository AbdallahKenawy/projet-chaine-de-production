/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operations;

import com.example.application.Machine;
import com.example.application.Realise;
import com.example.application.Typeoperation;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;


public class Divrealise extends Div{
    
    private Connection con;
    private Grid<Realise> GridRealise;
    
    public Divrealise() throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        setSizeFull();
        ComboBox<Machine> idmachine=new ComboBox("Machine");
        idmachine.setItems(Machine.tousLesMachines(this.con));
        ComboBox<Typeoperation> idtype=new ComboBox("Type Operation");
        idtype.setItems(Typeoperation.tousLestypeoperations(this.con));
        TextField duree=new TextField("Duree de l'operation");
        Button ajouterealise=new Button("Ajoute");
        HorizontalLayout detailsrealise=new HorizontalLayout(idmachine,idtype,duree,ajouterealise);
        detailsrealise.setAlignItems(FlexComponent.Alignment.BASELINE);
        detailsrealise.setPadding(true);
        add(detailsrealise);
        
        InitGridRealise();
        
        detailsrealise.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        ajouterealise.addClickListener(clickEvemt ->{
            int IDmachine = idmachine.getValue().getIdmachine();
            int IDtype=idtype.getValue().getIdtype();
            float Duree=Float.parseFloat(duree.getValue());
            try {
                new Realise(IDmachine,IDtype,Duree).saverealise(this.con);
                Notification noti = Notification.show("Operation de realisation rajoute!");
                noti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                GridRealise.setItems(Realise.tousLesRealise(con));
                idmachine.clear();
                idtype.clear();
                duree.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
            
        });
    }
    
    private void InitGridRealise() throws SQLException{
        this.GridRealise=new Grid<>();
        Editor<Realise> editor=GridRealise.getEditor();
        
        Grid.Column<Realise> RefMachineColumn=GridRealise.addColumn(Realise::getRefmachine).setHeader("Machine").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Realise> DesTypeColumn=GridRealise.addColumn(Realise::getDestype).setHeader("Operation").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Realise> DureeColumn=GridRealise.addColumn(Realise::getDuree).setHeader("Duree").setTextAlign(ColumnTextAlign.CENTER);
        AtomicReference<Realise> modifiedRealiseWrapper = new AtomicReference<>();

        Grid.Column<Realise> editColumn = GridRealise.addComponentColumn(realise -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridRealise.getEditor().editItem(realise);
                    modifiedRealiseWrapper.set(realise);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
       
        
        Binder<Realise> binder = new Binder<>(Realise.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        ComboBox <Machine> RefMachineField = new ComboBox();
        RefMachineField.setItems(Machine.tousLesMachines(con));
        RefMachineField.setWidthFull();
        binder.forField(RefMachineField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Realise::getMachine, Realise::setMachine);
        RefMachineColumn.setEditorComponent(RefMachineField);

        ComboBox <Typeoperation> DesTypeField = new ComboBox();
        DesTypeField.setItems(Typeoperation.tousLestypeoperations(con));
        DesTypeField.setWidthFull();
        binder.forField(DesTypeField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(Realise::getTypeop, Realise::setTypeop);
        DesTypeColumn.setEditorComponent(DesTypeField);

        TextField DureeField = new TextField();
        DureeField.setWidthFull();
        binder.forField(DureeField)
                .asRequired("Puissance invalide")
                .withConverter(new StringToFloatConverter("Invalid number format"))
                //.withStatusLabel(emailValidationMessage)
                .bind(Realise::getDuree, Realise::setDuree);
        DureeColumn.setEditorComponent(DureeField);
        
        Button saveButton = new Button("Save", e -> {
            int modifiedIdMachine = RefMachineField.getValue().getIdmachine();
            int modifiedIdType = DesTypeField.getValue().getIdtype();
            Float modifiedDuree= Float.parseFloat(DureeField.getValue());
            try {
                modifiedRealiseWrapper.get().updateRealise(con, modifiedIdMachine, modifiedIdType, modifiedDuree);
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
            dialog.setHeader("Supprimer l'operation de realisation ?");
            dialog.setText(
                    "Vouliez-vous la supprimer? Cette opération supprimera aussi tous les éléments auxquels cette derniere est associée.");
            dialog.setCancelable(true);
            dialog.addCancelListener(event ->dialog.close());

            dialog.setConfirmText("Supprimer");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event ->{
                try {
                    modifiedRealiseWrapper.get().deleteRealise(con);
                    GridRealise.setItems(Realise.tousLesRealise(con));
                    Notification.show("Realise supprimee");
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
        GridRealise.setItems(Realise.tousLesRealise(con));
        this.GridRealise.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        this.GridRealise.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(this.GridRealise);
    }
    
}
