package com.example.application.views;

import com.example.application.Gestion;
import com.example.application.views.about.AboutView;
import com.example.application.views.commande.CommandeView;
import com.example.application.views.edt.EDTView;
import com.example.application.views.login.LoginView;
import com.example.application.views.machines.MachinesView;
import com.example.application.views.operateurs.OperateursView;
import com.example.application.views.produits.ProduitsView;
import com.example.application.views.operations.OperationsView;
import com.example.application.views.postes.PostesView;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.sql.SQLException;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    

    private H2 viewTitle;
    private String currentTheme="";

    public MainLayout() throws SQLException {
        
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        Button RAZ = new Button("RAZ",new Icon(VaadinIcon.WARNING));
        RAZ.getElement().getStyle().set("margin-left", "auto");
        RAZ.addThemeVariants(ButtonVariant.LUMO_ERROR);
        RAZ.addClickListener(clickEvent -> {WarningDialog();});
        RAZ.setVisible(NiveauAccess.isAdmin());
        
        Button deconnexion = new Button("Deconnexion",new Icon(VaadinIcon.ARROW_CIRCLE_RIGHT));
        deconnexion.getElement().getStyle().set("margin-left", "auto");
        deconnexion.addThemeVariants(ButtonVariant.LUMO_TERTIARY,
        ButtonVariant.LUMO_ERROR);
        deconnexion.addClickListener(clickEvent -> {
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        
        HorizontalLayout buttonContainer=new HorizontalLayout(deconnexion,RAZ);
        buttonContainer.getStyle().set("margin-left", "auto");
        
        addToNavbar(true, toggle,viewTitle,buttonContainer);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Chaine de production");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.INFO_CIRCLE_SOLID.create()));
        nav.addItem(new SideNavItem("Machines", MachinesView.class, LineAwesomeIcon.INDUSTRY_SOLID.create()));
        nav.addItem(new SideNavItem("Operations", OperationsView.class, LineAwesomeIcon.COGS_SOLID.create()));
        nav.addItem(new SideNavItem("Produits", ProduitsView.class, LineAwesomeIcon.GIFTS_SOLID.create()));
        nav.addItem(new SideNavItem("Operateurs", OperateursView.class, LineAwesomeIcon.USER_CIRCLE_SOLID.create()));
        if(NiveauAccess.isAdmin()|| NiveauAccess.isOperateur()){
            nav.addItem(new SideNavItem("EDT", EDTView.class, LineAwesomeIcon.CALENDAR.create()));
        }
        nav.addItem(new SideNavItem("Postes de travail", PostesView.class, LineAwesomeIcon.USERS_COG_SOLID.create()));
        nav.addItem(new SideNavItem("Commande", CommandeView.class, LineAwesomeIcon.SHOPPING_CART_SOLID.create()));
        return nav;
    }
    
    private void WarningDialog(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("RAZ votre base de donnees");
        dialog.add(new Span("Voulez-vous remettre a zero la base de donnees?"));
        dialog.setCancelable(true);
        dialog.setConfirmText("RAZ");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(clickEvent -> {
            try {
                new Gestion(Gestion.connectSurServeurM3()).resetBDD();
                UI.getCurrent().getPage().reload();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        dialog.open();
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        ToggleButton themeButton = new ToggleButton("Jour/Nuit");
        themeButton.setValue(true);
        themeButton.addValueChangeListener(e -> {
            if(themeButton.getValue()==true){
                UI.getCurrent().getElement().getThemeList().remove(Lumo.DARK);        
            }else{       
                UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);    
            }
        });
        layout.add(themeButton);
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
