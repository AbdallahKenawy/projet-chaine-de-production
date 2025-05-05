/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.postes;

import com.example.application.Machine;
import com.example.application.Poste;
import com.example.application.views.MainLayout;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@PageTitle("Postes")
@Route(value = "postes", layout = MainLayout.class)
public class PostesView extends VerticalLayout {
    
    private Connection con;
    private ComboBox <Poste> IdsPostes;
    private VerticalLayout planning;
    
    public PostesView() throws SQLException{
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        Button NouveauPoste=new Button("Nouveau Poste");
        NouveauPoste.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        Button supprimeposte=new Button(VaadinIcon.TRASH.create());
        supprimeposte.setVisible(NiveauAccess.isOperateur()||NiveauAccess.isAdmin());
        
        NouveauPoste.addClickListener(e->{
            try {
                new Poste(1).savePoste(con);
                IdsPostes.setItems(Poste.tousLesPostes(con));
                Notification.show("Poste creee").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        IdsPostes=new ComboBox();
        IdsPostes.setAllowCustomValue(false);
        IdsPostes.setLabel("Reference Poste");
        IdsPostes.setItems(Poste.tousLesPostes(con));
        
        Grid <Machine> MachinesAssignees=new Grid();
        MachinesAssignees.addColumn(Machine::getRefmachine).setHeader("Machine associees").setTextAlign(ColumnTextAlign.CENTER);
        MachinesAssignees.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        Grid <Machine>MachinesNonAssignees=new Grid();
        MachinesNonAssignees.addColumn(Machine::getRefmachine).setHeader("Non-assignees").setTextAlign(ColumnTextAlign.CENTER);
        MachinesNonAssignees.setItems(new Poste(-1).MachinesAssociees(con));
        MachinesNonAssignees.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        HorizontalLayout detailsposte=new HorizontalLayout(IdsPostes,supprimeposte);
        detailsposte.setAlignItems(Alignment.BASELINE);
        detailsposte.setJustifyContentMode(JustifyContentMode.CENTER);
        VerticalLayout vlayout=new VerticalLayout(NouveauPoste,detailsposte);
        vlayout.setJustifyContentMode(JustifyContentMode.CENTER);
        vlayout.setAlignItems(Alignment.CENTER);
        add(vlayout);
        Button assigne=new Button(VaadinIcon.ARROW_LEFT.create());
        Button dissocie=new Button(VaadinIcon.ARROW_RIGHT.create());
        VerticalLayout buttons=new VerticalLayout(assigne,dissocie);
        buttons.setWidth("10%");
        buttons.setAlignItems(Alignment.CENTER);
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);
        
        assigne.setVisible(NiveauAccess.isOperateur());
        dissocie.setVisible(NiveauAccess.isOperateur());
        assigne.setVisible(NiveauAccess.isAdmin());
        dissocie.setVisible(NiveauAccess.isAdmin());
        HorizontalLayout hlayout=new HorizontalLayout(MachinesAssignees,buttons,MachinesNonAssignees);
        hlayout.setWidthFull();
        add(hlayout);
        
        H2 titre=new H2("Emploi du temps");
        this.planning=new VerticalLayout();
        add(titre,planning);
        
        IdsPostes.addValueChangeListener(e->{
            try {
                if(IdsPostes.getValue()!=null){
                    if(IdsPostes.getValue().getIdposte()!=-1){
                        titre.setText("Emploi du temps "+IdsPostes.getValue().toString());
                        MachinesAssignees.setItems(IdsPostes.getValue().MachinesAssociees(con));
                        OperationsdansPoste();
                    }else{
                        titre.setText("Emploi du temps");
                    }
                }
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        supprimeposte.addClickListener(e->{
            try {
                for (Machine machine : IdsPostes.getValue().MachinesAssociees(con)){     
                    machine.updatemachine(con, machine.getRefmachine(), machine.getDesmachine(), machine.getPuissance(), -1,machine.getStatut());
                }
                IdsPostes.getValue().deletePoste(con);
                IdsPostes.clear();
                Notification.show("Poste Supprimee");
                IdsPostes.setItems(Poste.tousLesPostes(con));
                MachinesAssignees.setItems(Collections.emptyList());
                MachinesNonAssignees.setItems(new Poste(-1).MachinesAssociees(con));
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        
        
        assigne.addClickListener(e->{
            if(IdsPostes.getValue()!=null){
                for(Machine machine : MachinesNonAssignees.getSelectedItems()){
                    try {
                        machine.updatemachine(con,machine.getRefmachine(), machine.getDesmachine(), machine.getPuissance(), IdsPostes.getValue().getIdposte(),machine.getStatut());
                        Notification.show("Changement de poste reussite").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        MachinesAssignees.setItems(IdsPostes.getValue().MachinesAssociees(con));
                        MachinesNonAssignees.setItems(new Poste(-1).MachinesAssociees(con));
                    } catch (SQLException ex) {
                        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }

                }
            }else{
                Notification.show("Veuillez choisir une poste").addThemeVariants(NotificationVariant.LUMO_WARNING);          
            }
        });
        
        dissocie.addClickListener(e->{
            for(Machine machine : MachinesAssignees.getSelectedItems()){
                if(IdsPostes.getValue()!=null){
                    try {
                        machine.updatemachine(con, machine.getRefmachine(), machine.getDesmachine(), machine.getPuissance(), -1,machine.getStatut());
                        Notification.show("Machine enlevee de poste").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        MachinesAssignees.setItems(IdsPostes.getValue().MachinesAssociees(con));
                        MachinesNonAssignees.setItems(new Poste(-1).MachinesAssociees(con));
                    } catch (SQLException ex) {
                        Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }
                }else{
                    Notification.show("Veuillez choisir une poste").addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
            }
        });
        
        
    }
    
    public void OperationsdansPoste() throws SQLException{
        planning.removeAll();
        if(this.IdsPostes.getValue()!=null){
            List<String[]> list=this.IdsPostes.getValue().OperationsdansPoste(con);
            Grid<String[]> grid = new Grid<>();
            Grid.Column<String[]> NumserieColumn=grid.addColumn(row -> row[0]).setHeader("Numero Serie").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> refmachineColumn=grid.addColumn(row -> row[1]).setHeader("Machine").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> destypeColumn=grid.addColumn(row -> row[2]).setHeader("Operation").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> operateurColumn=grid.addColumn(row -> row[3]).setHeader("Operateur").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> tempsdebutColumn=grid.addColumn(row -> row[4]).setHeader("Debut").setTextAlign(ColumnTextAlign.CENTER);
            Grid.Column<String[]> tempsfinColumn=grid.addColumn(row -> row[5]).setHeader("Fin").setTextAlign(ColumnTextAlign.CENTER);
   
            grid.setItems(list);
            grid.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER/*, GridVariant.LUMO_NO_ROW_BORDERS*/);
            planning.add(grid);
        }
    }
    
}
