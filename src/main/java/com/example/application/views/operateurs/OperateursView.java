/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operateurs;

import com.example.application.Operateur;
import com.example.application.views.MainLayout;
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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Operateurs")
@Route(value = "operateurs", layout = MainLayout.class)
public class OperateursView extends VerticalLayout {
    
    private Connection con;
    private Grid<Operateur> GridOperateur;
    
    public OperateursView() throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        setSpacing(false);
        
        TextField Nom=new TextField("Nom");
        TextField Prenom=new TextField("Prenom");
        Button ajouteOperateur=new Button("Ajoute");
        HorizontalLayout detailsoperateur=new HorizontalLayout(Nom,Prenom,ajouteOperateur);
        detailsoperateur.setAlignItems(FlexComponent.Alignment.BASELINE);
        detailsoperateur.setPadding(true);
        add(detailsoperateur);
        
        detailsoperateur.setVisible(NiveauAccess.isOperateur()|| NiveauAccess.isAdmin());
        ajouteOperateur.addClickListener(e->{
            if(Nom.getValue()!=null && Prenom.getValue()!=null)
                {
                try {
                    new Operateur(Nom.getValue(),Prenom.getValue()).saveOperateur(con);
                    GridOperateur.setItems(Operateur.tousLesOperateurs(con));
                    Nom.clear();
                    Prenom.clear();
                } catch (SQLException ex) {
                    Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    ex.printStackTrace();
                    }
            }else{
                Notification.show("Renseigner toutes les cases avant d'ajouter").addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        
        InitGridOperateurs();
    }
    
    public void InitGridOperateurs() throws SQLException{
        this.GridOperateur=new Grid<>();
        Editor<Operateur> editor=GridOperateur.getEditor();
        
        Grid.Column<Operateur> IdColumn=GridOperateur.addColumn(Operateur::getIdoperateur).setHeader("Id Operateur").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Operateur> NomColumn = GridOperateur.addColumn(Operateur::getNom).setHeader("Nom").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Operateur> PrenomColumn = GridOperateur.addColumn(Operateur::getPrenom).setHeader("Prenom").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Operateur> StatutColumn=GridOperateur.addColumn(new ComponentRenderer<>(operateur -> {
            Span badge = new Span();
            switch (operateur.getStatut()) {
                case "Disponible":
                    badge.setText("Disponible");
                    badge.getElement().getThemeList().add("badge success");
                    break;
                case "Vacances":
                    badge.setText("Vacances");
                    badge.getElement().getThemeList().add("badge error");
                    break;
                case "Production":
                    badge.setText("Production");
                    badge.getElement().getThemeList().add("badge");
                    break;
                default:
                    badge.setText("Err");
                    break;
            }
            return badge;
        })).setHeader("Statut").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Operateur> IdentifiantColumn = GridOperateur.addColumn(Operateur::getIdentifiant).setHeader("Identifiant").setTextAlign(ColumnTextAlign.CENTER);
        IdentifiantColumn.setVisible(NiveauAccess.isAdmin());
        Grid.Column<Operateur> MDPColumn = GridOperateur.addColumn(Operateur::getMotdepasse).setHeader("MDP").setTextAlign(ColumnTextAlign.CENTER);  
        MDPColumn.setVisible(NiveauAccess.isAdmin());
        Grid.Column<Operateur> HabilitationsColumn = GridOperateur.addColumn(new ComponentRenderer<>(operateur -> {
            try {
            List <String> MachinesPermises = operateur.ListeHabilitations(con);
            Span span = new Span(String.join(" ; ", MachinesPermises));
            span.getStyle().set("white-space", "pre-line");
            return span;
        } catch (SQLException ex) {
            Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            ex.printStackTrace();
            return new Span("Erreur SQL");
        }
        }))
        .setHeader("Habilitation").setTextAlign(ColumnTextAlign.CENTER);
        
        AtomicReference<Operateur> operateurmodifiewrapper=new AtomicReference();
        Grid.Column<Operateur> editColumn = GridOperateur.addComponentColumn(Operateur -> {
            Button editButton = new Button("Edit");
            if (NiveauAccess.isAdmin()){
                editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridOperateur.getEditor().editItem(Operateur);
                    operateurmodifiewrapper.set(Operateur);
                });
            }else if (NiveauAccess.isOperateur() && NiveauAccess.getIDOperateur()==Operateur.getIdoperateur()) {
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridOperateur.getEditor().editItem(Operateur);
                    operateurmodifiewrapper.set(Operateur);
            }); 
            }else{
                editButton.setEnabled(false);
            }
            return editButton;
        }).setWidth("225px").setFlexGrow(0);
       
        
        Binder<Operateur> binder = new Binder<>(Operateur.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        TextField NomField = new TextField();
        NomField.setWidthFull();
        binder.forField(NomField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Operateur::getNom, Operateur::setNom);
        NomColumn.setEditorComponent(NomField);
        
        TextField PrenomField = new TextField();
        PrenomField.setWidthFull();
        binder.forField(PrenomField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Operateur::getPrenom, Operateur::setPrenom);
        PrenomColumn.setEditorComponent(PrenomField);

        TextField IdentifiantField = new TextField();
        IdentifiantField.setWidthFull();
        binder.forField(IdentifiantField)
            .asRequired("Identifiant invalide")
            //.withStatusLabel(firstNameValidationMessage)
            .bind(Operateur::getIdentifiant, Operateur::setIdentifiant);
        IdentifiantColumn.setEditorComponent(IdentifiantField);

        TextField MDPField = new TextField();
        MDPField.setWidthFull();
        binder.forField(MDPField)
                .asRequired("Mot de passe invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Operateur::getMotdepasse, Operateur::setMotdepasse);
        MDPColumn.setEditorComponent(MDPField);
        
        ComboBox <String> Statut = new ComboBox();
        Statut.setAllowCustomValue(false);
        Statut.setItems("Disponible","Production","Vacances");
        Statut.setWidthFull();
        binder.forField(Statut)
                .asRequired("Statut invalide")
                //.withStatusLabel(emailValidationMessage)
                .bind(Operateur::getStatut, Operateur::setStatut);
        StatutColumn.setEditorComponent(Statut);
        
        Button saveButton = new Button("Save", e -> {
            String modifiednom = NomField.getValue();
            String modifiedprenom = PrenomField.getValue();
            String modifiedidentifiant=IdentifiantField.getValue();
            String modifiedmotdepasse=MDPField.getValue();
            String modifiedstaut=Statut.getValue();
            try {
                if(NiveauAccess.isAdmin()){
                    operateurmodifiewrapper.get().updateOperateurAdmin(con,modifiednom,modifiedprenom,modifiedidentifiant,modifiedmotdepasse,modifiedstaut);
                }else{
                    operateurmodifiewrapper.get().updateOperateur(con, modifiednom, modifiedprenom,modifiedstaut);
                }
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
                        operateurmodifiewrapper.get().deleteOperateur(con);
                        GridOperateur.setItems(Operateur.tousLesOperateurs(con));
                        Notification.show("Operateur supprimee");
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
        
        editColumn.setVisible(NiveauAccess.isOperateur()|| NiveauAccess.isAdmin());
        GridOperateur.setItems(Operateur.tousLesOperateurs(con));
        GridOperateur.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        GridOperateur.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        GridOperateur.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
        add(GridOperateur);
    }
    
}
