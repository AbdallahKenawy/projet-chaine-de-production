package com.example.application.views.machines;

import com.example.application.Habilitation;
import com.example.application.Machine;
import com.example.application.Operateur;
import com.example.application.Poste;
import com.example.application.views.MainLayout;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@PageTitle("Machines")
@Route(value = "machines", layout = MainLayout.class)
public class MachinesView extends VerticalLayout {
    
    private Connection con;
    
    private Grid<Machine> GridMachine;
    
    private TextField Reference;
    private TextField Description;
    private TextField Puissance;
    
    private List<Operateur> touslesoperateurs;
    
    
    public MachinesView() throws SQLException {
        
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        
        this.touslesoperateurs=Operateur.tousLesOperateurs(con);
        
        this.Reference=new TextField("Reference");
        this.Description=new TextField("Description");
        this.Puissance=new TextField("Puissance");
        ComboBox <Poste> IdPoste=new ComboBox("ID Poste");
        IdPoste.setAllowCustomValue(false);
        IdPoste.setItems(Poste.tousLesPostes(con));
        Button ajoutemachine=new Button("Ajoute Machine");

        HorizontalLayout detailsmachine=new HorizontalLayout();
        detailsmachine.setPadding(true);
        detailsmachine.setAlignItems(Alignment.BASELINE);
        detailsmachine.setJustifyContentMode(JustifyContentMode.CENTER);
        detailsmachine.add(Reference,Description,Puissance,IdPoste,ajoutemachine);
        add(detailsmachine);
        
        InitGridMachine();
        detailsmachine.setVisible(NiveauAccess.isOperateur() || NiveauAccess.isAdmin());
        
        ajoutemachine.addClickListener(clickEvent -> {
            String ref=this.Reference.getValue();
            String des=this.Description.getValue();
            float puis=Float.parseFloat(this.Puissance.getValue());
            int idposte=IdPoste.getValue().getIdposte();
            try {
                new Machine(ref,des,puis,idposte).savemachine(this.con);
                Notification.show("Machine rajoutee successivement!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);;                  
                this.GridMachine.setItems(Machine.tousLesMachines(this.con));
                this.Reference.clear();
                this.Description.clear();
                this.Puissance.clear();
                IdPoste.clear();
            } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
              
    }
    
    public void InitGridMachine() throws SQLException{
        this.GridMachine=new Grid<>();
        Editor<Machine> editor=GridMachine.getEditor();
        
        Grid.Column<Machine> IdColumn=GridMachine.addColumn(Machine::getIdmachine).setHeader("ID").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Machine> RefColumn=GridMachine.addColumn(Machine::getRefmachine).setHeader("Reference").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Machine> DesColumn=GridMachine.addColumn(Machine::getDesmachine).setHeader("Description").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Machine> PuisColumn=GridMachine.addColumn(Machine::getPuissance).setHeader("Puissance").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Machine> IdposteColumn=GridMachine.addColumn(Machine::getIdposte).setHeader("Poste").setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Machine> StatutColumn=GridMachine.addColumn(new ComponentRenderer<>(machine -> {
            Span badge = new Span();
            switch (machine.getStatut()) {
                case "Disponible":
                    badge.setText("Disponible");
                    badge.getElement().getThemeList().add("badge success");
                    break;
                case "En panne":
                    badge.setText("En panne");
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
        AtomicReference <Machine> machinemodifieewrapper=new AtomicReference<>();
        
        Grid.Column<Machine> editColumn = GridMachine.addComponentColumn(machine -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                    GridMachine.getEditor().editItem(machine);
                    machinemodifieewrapper.set(machine);
            });   
            return editButton;
        }).setWidth("225px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
        
        Grid.Column<Machine> HabilitationColumn = GridMachine.addComponentColumn(machine -> {
            Button HabilitationButton = new Button("Habilitation");
            HabilitationButton.addClickListener(e -> {
                FenetreOperateurs(machine);
            });   
            return HabilitationButton;
        }).setWidth("150px").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);;
       
        
        Binder<Machine> binder = new Binder<>(Machine.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        
        TextField RefField = new TextField();
        RefField.setWidthFull();
        binder.forField(RefField)
                .asRequired("Réference invalide")
                //.withStatusLabel(firstNameValidationMessage)
                .bind(Machine::getRefmachine, Machine::setRefmachine);
        RefColumn.setEditorComponent(RefField);

        TextField DesField = new TextField();
        DesField.setWidthFull();
        binder.forField(DesField)
                .asRequired("Description invalide")
                //.withStatusLabel(lastNameValidationMessage)
                .bind(Machine::getDesmachine, Machine::setDesmachine);
        DesColumn.setEditorComponent(DesField);

        TextField PuissField = new TextField();
        PuissField.setWidthFull();
        binder.forField(PuissField)
                .asRequired("Puissance invalide")
                .withConverter(new StringToFloatConverter("Invalid number format"))
                //.withStatusLabel(emailValidationMessage)
                .bind(Machine::getPuissance, Machine::setPuissance);
        PuisColumn.setEditorComponent(PuissField);
        
        ComboBox <Integer> IdposteField = new ComboBox();
        IdposteField.setAllowCustomValue(false);
        List<Poste> postes = Poste.tousLesPostes(con);
        List<Integer> idPosteValues = postes.stream()
                                            .map(Poste::getIdposte)
                                            .collect(Collectors.toList());
        IdposteField.setItems(idPosteValues);
        IdposteField.setWidthFull();
        binder.forField(IdposteField)
                .asRequired("Puissance invalide")
                //.withStatusLabel(emailValidationMessage)
                .bind(Machine::getIdposte, Machine::setIdposte);
        IdposteColumn.setEditorComponent(IdposteField);
        
        ComboBox <String> Statut = new ComboBox();
        Statut.setAllowCustomValue(false);
        Statut.setItems("Disponible","Production","En panne");
        Statut.setWidthFull();
        binder.forField(Statut)
                .asRequired("Statut invalide")
                //.withStatusLabel(emailValidationMessage)
                .bind(Machine::getStatut, Machine::setStatut);
        StatutColumn.setEditorComponent(Statut);
        
        
        Button saveButton = new Button("Save", e -> {
            String modifiedReference = RefField.getValue();
            String modifiedDescription = DesField.getValue();
            Float modifiedPuissance= Float.parseFloat(PuissField.getValue());
            int modifiedIdposte= IdposteField.getValue();
            String modifiedstatut=Statut.getValue();
            try {
                machinemodifieewrapper.get().updatemachine(con, modifiedReference, modifiedDescription, modifiedPuissance,modifiedIdposte,modifiedstatut);
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
            dialog.setHeader("Supprimer Machine ?");
            dialog.setText(
                    "Vouliez-vous supprimer cette machine? Cette opération supprimera aussi tous les éléments auxquels cette derniere est associée.");
            dialog.setCancelable(true);
            dialog.addCancelListener(event ->dialog.close());
            dialog.setConfirmText("Supprimer");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event ->{
                try {
                    machinemodifieewrapper.get().deletemachine(con);
                    GridMachine.setItems(Machine.tousLesMachines(con));
                    Notification.show("Machine supprimee");
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
        editColumn.setVisible(NiveauAccess.isOperateur() || NiveauAccess.isAdmin());
        HabilitationColumn.setVisible(NiveauAccess.isOperateur() || NiveauAccess.isAdmin());
        GridMachine.setItems(Machine.tousLesMachines(con));
        this.GridMachine.getStyle().set("background-color", "rgba(0, 0, 0, 0)");
        this.GridMachine.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        add(this.GridMachine);
    }
    
    public void FenetreOperateurs(Machine machine){
        Dialog FenetreOperateurs=new Dialog();
                FenetreOperateurs.setHeaderTitle("Operateur Habilite a la machine "+machine.getRefmachine());
                CheckboxGroup<Operateur> operateurs = new CheckboxGroup<>();
                operateurs.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
                Button annuler=new Button("Annuler");
                annuler.addClickListener(e -> FenetreOperateurs.close());
                Button valider=new Button("Valider");
                if(NiveauAccess.isOperateur()){
                    try{
                        operateurs.setItems(Operateur.getOperateurbyId(con, NiveauAccess.getIDOperateur()));
                        operateurs.select(machine.OperateurHabilites(con));
                    }catch (SQLException ex) {
                    Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    ex.printStackTrace();
                    }
                }else{
                try {
                    List <Operateur> touslesoperateurs=Operateur.tousLesOperateurs(con);
                    operateurs.setItems(touslesoperateurs);
                    operateurs.select(machine.OperateurHabilites(con));
                } catch (SQLException ex) {
                    Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    ex.printStackTrace();
                    }
                }             
                valider.addClickListener(e->{
                   Set<Operateur> selectedItems = operateurs.getSelectedItems();
                try {
                machine.SupprimeOperateurHabilites(con);
                    } catch (SQLException ex) {
                Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
                    }
                   for (Operateur operateur : selectedItems){
                       try {
                           new Habilitation(operateur.getIdoperateur(),machine.getIdmachine()).savehabilitation(con);
                       } catch (SQLException ex) {
                           Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                           ex.printStackTrace();
                       }
                    }
                  FenetreOperateurs.close();
                });
                valider.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                FenetreOperateurs.add(operateurs);
                FenetreOperateurs.getFooter().add(annuler);
                FenetreOperateurs.getFooter().add(valider);
                FenetreOperateurs.open();
    }

}
