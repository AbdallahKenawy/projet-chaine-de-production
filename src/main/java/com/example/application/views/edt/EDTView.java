/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views.edt;

import com.example.application.views.MainLayout;
import com.example.application.views.NiveauAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Timezone;


@PageTitle("EDT")
@Route(value = "edt", layout = MainLayout.class)
public class EDTView extends VerticalLayout
{
    private Connection con;
    private FullCalendar calendar;
    private LocalDate time;
    
    public EDTView() throws SQLException{
        this.con=VaadinSession.getCurrent().getAttribute(Connection.class);
        time=LocalDate.now();
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.FRENCH);
        
        setWidthFull();
        setHeightFull();
        
        calendar = FullCalendarBuilder.create().build();
        Timezone tzParisFrance = new Timezone(ZoneId.of("Europe/Paris"));
        calendar.setTimezone(tzParisFrance);
        calendar.setLocale(Locale.FRENCH);
        
        calendar.setHeightFull();
        calendar.setWidthFull();
        
        calendar.setBusinessHours(new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0),BusinessHours.DEFAULT_BUSINESS_WEEK));
        
        calendar.changeView(CalendarViewImpl.values()[4]);
        EDTOperateur();
        
        calendar.addEntryClickedListener(event -> {
            Notification.show(event.getEntry().getDescription(),3000,Position.BOTTOM_START);
        });
        
        Span info=new Span(calendar);
        info.setText(time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(0,1).toUpperCase()+time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(1) +" "+time.getYear());
        Button prevMonthButton = new Button(LineAwesomeIcon.ANGLE_LEFT_SOLID.create());
        prevMonthButton.addClickListener(event -> {
                calendar.previous();
                time=time.minusWeeks(1);
                info.setText(time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(0,1).toUpperCase()+time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(1)+" "+time.getYear());
        });

        Button today=new Button(LineAwesomeIcon.HOME_SOLID.create());
        today.addClickListener(event ->{
            calendar.gotoDate(LocalDate.now());
            time=LocalDate.now();
            info.setText(time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(0,1).toUpperCase()+time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(1) +" "+time.getYear());
                });
        
        Button nextMonthButton = new Button(LineAwesomeIcon.ANGLE_RIGHT_SOLID.create());
        nextMonthButton.addClickListener(event -> {
            calendar.next();
            time=time.plusWeeks(1);
            info.setText(time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(0,1).toUpperCase()+time.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).substring(1) +" "+time.getYear());
                });
        
        HorizontalLayout layout=new HorizontalLayout(prevMonthButton,today,nextMonthButton,info);
        layout.setAlignItems(Alignment.BASELINE);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setPadding(true);
        
        add(layout,calendar);
    }
    
    public List<String> EDTOperateur() throws SQLException{
        calendar.getEntryProvider().asInMemory().removeAllEntries();
        List res = new ArrayList<>();
        if(NiveauAccess.isOperateur()){
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT machine.idposte,typeoperation.destype,phasefabrication.numserie,machine.refmachine,tempsdebut,tempsfin FROM phasefabrication \n" +
                "JOIN machine ON machine.refmachine=phasefabrication.refmachine\n" +
                "JOIN touteslesoperations ON touteslesoperations.idtoutesop=phasefabrication.idtoutesop\n" +
                "JOIN typeoperation ON typeoperation.idtype=touteslesoperations.idtype\n" +
                "JOIN exemplaire ON exemplaire.numserie=phasefabrication.numserie\n" +
                "WHERE idoperateur=? AND exemplaire.statut!='Annulee'")) {
            pst.setInt(1, NiveauAccess.getIDOperateur());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                        Entry entry = new Entry();                       
                        entry.setTitle("Poste:"+String.valueOf(rs.getInt("idposte")));
                        entry.setDescription(rs.getString("numserie")+" | "+rs.getString("refmachine")+" | "+rs.getString("destype"));                      
                        entry.setStart(rs.getTimestamp("tempsdebut").toLocalDateTime());
                        entry.setEnd(rs.getTimestamp("tempsfin").toLocalDateTime());
                        if(LocalDateTime.now().isBefore(entry.getStart())){
                            entry.setColor("#ED7F97");
                        }else if(LocalDateTime.now().isAfter(entry.getEnd())){
                            entry.setColor("#54DE6E");
                        }else{
                            entry.setColor("#d8ab41");
                        }
                        calendar.getEntryProvider().asInMemory().addEntries(entry);
                    }
                }
            }
        }else{
            try (PreparedStatement pst = con.prepareStatement(
                "SELECT machine.idposte,CONCAT(operateur.nom, ' ', operateur.prenom) AS operateur_full_name,typeoperation.destype,numserie,machine.refmachine,tempsdebut,tempsfin FROM phasefabrication \n" +
                "JOIN machine ON machine.refmachine=phasefabrication.refmachine\n" +
                "JOIN touteslesoperations ON touteslesoperations.idtoutesop=phasefabrication.idtoutesop\n" +
                "JOIN typeoperation ON typeoperation.idtype=touteslesoperations.idtype\n" +
                "JOIN operateur ON operateur.idoperateur=phasefabrication.idoperateur\n" +
                "")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                        Entry entry = new Entry();                       
                        entry.setTitle("Poste:"+String.valueOf(rs.getInt("idposte")));
                        entry.setDescription(rs.getString("operateur_full_name")+" | "+rs.getString("numserie")+" | "+rs.getString("refmachine")+" | "+rs.getString("destype") );                      
                        entry.setStart(rs.getTimestamp("tempsdebut").toLocalDateTime());
                        entry.setEnd(rs.getTimestamp("tempsfin").toLocalDateTime());
                        if(LocalDateTime.now().isBefore(entry.getStart())){
                            entry.setColor("#ED7F97");
                        }else if(LocalDateTime.now().isAfter(entry.getEnd())){
                            entry.setColor("#54DE6E");
                        }else{
                            entry.setColor("#d8ab41");
                        }
                        calendar.getEntryProvider().asInMemory().addEntries(entry);                    
                    }
                }
            }
            
        }
        return res;
    
}
    
}
