/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.login;

import static com.example.application.Gestion.connectGeneralMySQL;
import com.example.application.Utilisateur;
import com.example.application.Operateur;
import com.example.application.views.NiveauAccess;
import com.example.application.views.about.AboutView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@PageTitle("Login")
@Route(value = "login")
@RouteAlias(value = "")
public class LoginView extends VerticalLayout{
    
    private String host;
    private int port;
    private String database;
    private String user;
    private String pass;
    private Connection con;
    
    public LoginView(){
        
    host="92.222.25.165";
    port=3306;
    database="m3_akenawi01";
    user="m3_akenawi01";
    pass="169e894b";
    
    attemptLogin(host,port,database,user,pass);
        
    TextField usernameField = new TextField("Identifiant");
    PasswordField passwordField = new PasswordField("Mot de Passe");
    
    Button loginButton = new Button("Login", e -> {
        try {
            if(Utilisateur.verificationUtilisateur(con, usernameField.getValue(), passwordField.getValue())!=0){
                NiveauAccess.setIDUtilisateur(Utilisateur.verificationUtilisateur(con, usernameField.getValue(), passwordField.getValue()));
                NiveauAccess.setNiveauAcces("Utilisateur");
                getUI().ifPresent(ui -> ui.navigate(AboutView.class));
            }else if(Operateur.verificationOperateur(con, usernameField.getValue(), passwordField.getValue())!=0){
                NiveauAccess.setIDOperateur(Operateur.verificationOperateur(con, usernameField.getValue(), passwordField.getValue()));
                NiveauAccess.setIDUtilisateur(1);
                NiveauAccess.setNiveauAcces("Operateur");
                getUI().ifPresent(ui -> ui.navigate(AboutView.class));
            }else if(usernameField.getValue().equals("admin") && passwordField.getValue().equals("1234")){
                NiveauAccess.setIDUtilisateur(1);
                NiveauAccess.setNiveauAcces("Admin");
                getUI().ifPresent(ui -> ui.navigate(AboutView.class));
            }else{
                Notification.show("Saisies fausses", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (SQLException ex) {
            Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            ex.printStackTrace();
        }
    });
    
    Button signupButton = new Button("Creer un Compte",e ->{
        SignUp();
        });
    
    Button reglagesAvances = new Button(VaadinIcon.COG.create(),e->{
        Dialog dialog=new Dialog();
        dialog.setHeaderTitle("Reglages Avancees");
        TextField Host=new TextField("Host");
        Host.setValue(host);
        TextField Port=new TextField("Port");
        Port.setValue(String.valueOf(port));
        TextField Database=new TextField("Database");
        Database.setValue(database);
        TextField User=new TextField("User");
        User.setValue(user);
        TextField Pass=new TextField("Password");
        Pass.setValue(pass);
        VerticalLayout reglageslayout=new VerticalLayout();
        reglageslayout.setPadding(true);
        reglageslayout.setSpacing(true);
        reglageslayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        reglageslayout.add(Host,Port,Database,User,Pass);
        dialog.add(reglageslayout);
        Button TestConnection=new Button("Test Connection");
        TestConnection.addClickListener(ev->{
            attemptLogin(Host.getValue(),Integer.parseInt(Port.getValue()),Database.getValue(),User.getValue(),Pass.getValue());
        });
        Button Close=new Button("Fermer",event->dialog.close());
        Button RAZ=new Button("Reglages par defaut");
        RAZ.addClickListener(eve->{
            Host.setValue(host);
            Port.setValue(String.valueOf(port));
            Database.setValue(database);
            User.setValue(user);
            Pass.setValue(pass);
        });
        dialog.getFooter().add(TestConnection,Close);
        dialog.getHeader().add(RAZ);
        dialog.open();
    });
    
    
    this.setJustifyContentMode(JustifyContentMode.CENTER);
    this.setAlignItems(Alignment.CENTER);
    this.setSizeFull();
    
    H1 Titre= new H1("Bienvenue");
    add(Titre,usernameField,passwordField, new HorizontalLayout(signupButton,loginButton,reglagesAvances));
    }
    
    private void attemptLogin(String host,int port,String database, String username, String password) {
    try {
        Connection con = connectGeneralMySQL(host, port, database, username, password);
        if (con != null) {
            try (Statement st = con.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT 1");
                if (rs.next()) {
                    this.con=con;
                    VaadinSession.getCurrent().setAttribute(Connection.class, con);
                    Notification.show("Connection reussite", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    Notification.show("Veuilles reverifier vos donnees", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        } else {
            Notification.show("Erreur connexion a la base de donnees", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    } catch (SQLException ex) {
        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        ex.printStackTrace();
        }
    }
    
    private void SignUp() {
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nouveau Utilisateur");
        TextField NomField = new TextField("Nom");
        TextField PrenomField = new TextField("Prenom");
        TextField IdentifiantField = new TextField("Identifiant");
        PasswordField MDPField = new PasswordField("Mot de Passe");
        RadioButtonGroup<String> statut = new RadioButtonGroup<>();
        statut.setLabel("Statut");
        statut.setItems("Operateur", "Utilisateur");
        VerticalLayout dialogLayout = new VerticalLayout(NomField,PrenomField,IdentifiantField,MDPField,statut);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialog.add(dialogLayout);
        Button SaveButton = new Button("Save");
        SaveButton.addClickListener(e->{
            if(statut.getValue()!=null && NomField.getValue()!="" && PrenomField.getValue()!="" && IdentifiantField.getValue()!="" && MDPField.getValue()!=""){
                if(statut.getValue().equals("Operateur")){
                    try {
                        new Operateur(NomField.getValue(), PrenomField.getValue(), IdentifiantField.getValue(), MDPField.getValue()).saveOperateur(con);
                    } catch (SQLException ex) {
                        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }
                }else{
                    try {
                        new Utilisateur(NomField.getValue(), PrenomField.getValue(), IdentifiantField.getValue(), MDPField.getValue()).saveUtilisateur(con);
                    } catch (SQLException ex) {
                        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }
                }
                Notification.show("Success").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            }else{
                Notification.show("Veuillez remplire tous les champs indiquees").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        Button CancelButton = new Button("Cancel", et -> dialog.close());
        dialog.getFooter().add(SaveButton,CancelButton);
        dialog.open();
    }

}

