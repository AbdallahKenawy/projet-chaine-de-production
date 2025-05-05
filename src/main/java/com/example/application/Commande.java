/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import com.example.application.views.NiveauAccess;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Commande 
{

    /**
     * @return the tempsfin
     */
    public Timestamp getTempsfin() {
        return tempsfin;
    }

    /**
     * @param tempsfin the tempsfin to set
     */
    public void setTempsfin(Timestamp tempsfin) {
        this.tempsfin = tempsfin;
    }

    /**
     * @return the idcommande
     */
    public int getIdcommande() {
        return idcommande;
    }

    /**
     * @param idcommande the idcommande to set
     */
    public void setIdcommande(int idcommande) {
        this.idcommande = idcommande;
    }

    /**
     * @return the refcommande
     */
    public String getRefcommande() {
        return refcommande;
    }

    /**
     * @param refcommande the refcommande to set
     */
    public void setRefcommande(String refcommande) {
        this.refcommande = refcommande;
    }

    /**
     * @return the tempsdebut
     */
    public Timestamp getTempsdebut() {
        return tempsdebut;
    }

    /**
     * @param tempsdebut the tempsdebut to set
     */
    public void setTempsdebut(Timestamp tempsdebut) {
        this.tempsdebut = tempsdebut;
    }
    private int idcommande;
    private int idutilisateur;
    private String refcommande;
    private Timestamp tempsdebut;
    private Timestamp tempsfin;
    
    public Commande(int idcommande,String refcommande,Timestamp tempsdebut,int idutilisateur){
        this.idcommande=idcommande;
        this.refcommande=refcommande;
        this.tempsdebut=tempsdebut;
        this.idutilisateur=idutilisateur;
    }
    
    public Commande(String refcommande,Timestamp tempsdebut,int idutilisateur){
        this(-1,refcommande,tempsdebut,idutilisateur);
    }
    
    public Commande(String refcommande,Timestamp tempsdebut){
        this(-1,refcommande,tempsdebut,0);
    }
    
    @Override
    public String toString(){
        return this.refcommande;
    }
    
    public void savecommande(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "insert into commande (refcommande,datecommande,idutilisateur) values (?,?,?)")) {
            pst.setString(1, this.getRefcommande());
            pst.setTimestamp(2, this.getTempsdebut());
            pst.setInt(3, this.idutilisateur);
            pst.executeUpdate();
        }
    }
    
    public void deletecommande(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "delete from commande where refcommande=?")) {
            pst.setString(1, this.refcommande);
            pst.executeUpdate();
        }
    }
    
    public static List RefProduits (Connection con,String refcommande) throws SQLException{
        List res=new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
        "SELECT refproduit FROM exemplaire WHERE refcommande = ?")) {
            pst.setString(1, refcommande);
            try (ResultSet rs = pst.executeQuery()) {
        while (rs.next()) {
            String refproduit = rs.getString("refproduit");
            if(!res.contains(refproduit)){
                 res.add(refproduit);
                    }
                    }
                }
            }
        return res;
    }
            
   public static List Exemplaires(Connection con,String refcommande,String refproduit) throws SQLException{
       List res=new ArrayList<>();;
       try (PreparedStatement pst = con.prepareStatement(
        "   SELECT * FROM exemplaire WHERE refcommande = ? AND refproduit = ? ORDER BY datedebut")) {
            pst.setString(1, refcommande);
            pst.setString(2, refproduit);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Exemplaire temp=new Exemplaire(refcommande,refproduit,rs.getString("numserie"),rs.getTimestamp("datedebut"));
                    temp.setDatefin(rs.getTimestamp("datefin"));
                    temp.setStatut(rs.getString("statut"));
                    res.add(temp);
                        }       
                    }
                }

       return res;
   }
   
   public static List allcommandes (Connection con) throws SQLException{
       List res=new ArrayList<>();
       try (PreparedStatement pst = con.prepareStatement(
                "select idcommande,refcommande,datecommande,idutilisateur from commande ORDER BY datecommande")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Commande temp= new Commande (rs.getInt("idcommande"),rs.getString("refcommande"),rs.getTimestamp("datecommande"),rs.getInt("idutilisateur"));
                    res.add(temp);
                }
            }
        }
       return res;
   }
   
   public static List commandsutilisateur(Connection con) throws SQLException{
       List res=new ArrayList<>();
       try (PreparedStatement pst = con.prepareStatement(
                "select idcommande,refcommande,datecommande,idutilisateur from commande WHERE idutilisateur=? ORDER BY datecommande")) {
           pst.setInt(1, NiveauAccess.getIDUtilisateur());
           try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Commande temp= new Commande (rs.getInt("idcommande"),rs.getString("refcommande"),rs.getTimestamp("datecommande"),rs.getInt("idutilisateur"));
                    res.add(temp);
                }
            }
        }
       return res;
   }
   
   public static int nbrCommande(Connection con) throws SQLException{
       int res=0;
       try (PreparedStatement pst = con.prepareStatement(
                "SELECT COUNT(*) AS nbr_commande FROM commande;")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res=rs.getInt("nbr_commande");
                }
            }
       }
       return res;
   }
   
   public String formulationdatedebut() {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(getTempsdebut());
    }
   
   public String fromulationdatefin(){
    return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(getTempsfin());
   }

}
    
