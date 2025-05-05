/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.views;

import com.vaadin.flow.server.VaadinSession;


public class NiveauAccess 
{

    public static void setIDUtilisateur(int id) {
        VaadinSession.getCurrent().setAttribute(ID_UTILISATEUR, String.valueOf(id));
    }
    private static final String CLE_NIVEAU_ACCES = "niveau_acces";
    private static final String ID_OPERATEUR= "idOperateur";
    private static String ID_UTILISATEUR="idUtilisateur";

    public static void setNiveauAcces(String niveau) {
        VaadinSession.getCurrent().setAttribute(CLE_NIVEAU_ACCES, niveau);
    }
    
    public static void setIDOperateur(int id){
        VaadinSession.getCurrent().setAttribute(ID_OPERATEUR, String.valueOf(id));
    }

    public static String getNiveauAcces() {
        return (String) VaadinSession.getCurrent().getAttribute(CLE_NIVEAU_ACCES);
    }
    
    public static int getIDOperateur(){
            if((String)VaadinSession.getCurrent().getAttribute(ID_OPERATEUR)!=null){
        return Integer.parseInt((String)VaadinSession.getCurrent().getAttribute(ID_OPERATEUR));
            }else{
                return -1;
            }
    }
    
    public static int getIDUtilisateur(){
            if((String)VaadinSession.getCurrent().getAttribute(ID_UTILISATEUR)!=null){
        return Integer.parseInt((String)VaadinSession.getCurrent().getAttribute(ID_UTILISATEUR));
            }else{
                return 0;
            }
    }

    public static boolean isOperateur() {
        String niveau = getNiveauAcces();
        return "Operateur".equals(niveau);
    }
    
    public static boolean isAdmin(){
        String niveau = getNiveauAcces();
        return "Admin".equals(niveau);
    }
    
}
