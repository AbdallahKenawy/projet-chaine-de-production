/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.operations;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Operations")
@Route(value = "operations", layout = MainLayout.class)
public class OperationsView extends VerticalLayout{
    
    private Connection con;
    private Map<Tab, Div> tabToCustomDivMap = new HashMap<>();
    
    private Div divtype;
    private Div divprecedence;
    private Div divrealise;
    private Div divtouteslesop;
    
    public OperationsView() throws SQLException{


        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        Tabs tabs = new Tabs();
        tabs.setSizeFull();
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        
        Tab tabType = new Tab("Types d'operation");
        Tab tabRealise = new Tab("Temps de realisation");
        Tab tabToutesLesOp = new Tab("Toutes les operations");
        Tab tabPrecedence = new Tab("Precedence des operations");
        
        tabs.add(tabType, tabRealise, tabToutesLesOp, tabPrecedence);
        add(tabs);
        
        tabs.addSelectedChangeListener(event -> {
            removeAll();
            add(tabs);
           try {
            if (event.getSelectedTab().equals(tabType)) {
                add(new Divtype());
            } else if (event.getSelectedTab().equals(tabRealise)) {
                add(new Divrealise());
            } else if (event.getSelectedTab().equals(tabToutesLesOp)) {
                add(new Divoperations());
            } else if (event.getSelectedTab().equals(tabPrecedence)) {
                add(new Divprecedence());
            }
           }catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        tabs.setSelectedTab(tabType);
        add(new Divtype());
    }

}
