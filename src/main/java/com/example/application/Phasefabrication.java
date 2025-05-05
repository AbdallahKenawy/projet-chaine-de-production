/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class Phasefabrication 
{

    /**
     * @return the idtouteslesoperations
     */
    public int getIdtouteslesoperations() {
        return idtouteslesoperations;
    }

    /**
     * @param idtouteslesoperations the idtouteslesoperations to set
     */
    public void setIdtouteslesoperations(int idtouteslesoperations) {
        this.idtouteslesoperations = idtouteslesoperations;
    }

    /**
     * @return the idcommande
     */
    public String getRefmachine() {
        return refmachine;
    }

    /**
     * @param idcommande the idcommande to set
     */
    public void setRefmachine(String refmachine) {
        this.refmachine = refmachine;
    }

    /**
     * @return the idoperateur
     */
    public int getIdoperateur() {
        return idoperateur;
    }

    /**
     * @param idoperateur the idoperateur to set
     */
    public void setIdoperateur(int idoperateur) {
        this.idoperateur = idoperateur;
    }

    /**
     * @return the numserie
     */
    public String getNumserie() {
        return numserie;
    }

    /**
     * @param numserie the numserie to set
     */
    public void setNumserie(String numserie) {
        this.numserie = numserie;
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
    private int idtouteslesoperations;
    private String refmachine;
    private int idoperateur;
    private String numserie;
    private Timestamp tempsdebut;
    private Timestamp tempsfin;
    
    public Phasefabrication(int idtouteslesoperations, String refmachine,int idoperateur, String numserie,Timestamp tempsdebut,Timestamp tempsfin){
        this.idtouteslesoperations=idtouteslesoperations;
        this.refmachine=refmachine;
        this.idoperateur=idoperateur;
        this.numserie=numserie;
        this.tempsdebut=tempsdebut;
        this.tempsfin=tempsfin;
    }
    
    public void savePhasefabrication(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into phasefabrication (idtoutesop,refmachine,idoperateur,numserie,tempsdebut,tempsfin) values (?,?,?,?,?,?)")) {
            pst.setInt(1, this.getIdtouteslesoperations());
            pst.setString(2, this.getRefmachine());
            pst.setInt(3, this.getIdoperateur());
            pst.setString(4, this.numserie);
            pst.setTimestamp(5, this.tempsdebut);
            pst.setTimestamp(6, this.tempsfin);
            pst.executeUpdate();
        }
    }
    
    public List<Phasefabrication> touteslesphases (Connection conn) throws SQLException{
        List res = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(
                "select * from phasefabrication")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Phasefabrication temp= new Phasefabrication (rs.getInt("idtoutesop"),rs.getString("refmachine"),rs.getInt("idoperateur"),rs.getString("numserie"),rs.getTimestamp("tempsdebut"),rs.getTimestamp("tempsfin"));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public static Operateur OperateurDisponible(Connection con,String refmachine) throws SQLException{
        Operateur res=null;
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM operateur\n" +
                "JOIN habilitation ON habilitation.idoperateur=operateur.idoperateur\n" +
                "JOIN machine ON habilitation.idmachine=machine.idmachine\n" +
                "WHERE refmachine= ? AND operateur.statut='Disponible'\n" +
                "LIMIT 1")) {
            pst.setString(1, refmachine);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res= new Operateur (rs.getInt("idoperateur"),rs.getString("nom"),rs.getString("prenom"),rs.getString("identifiant"),rs.getString("motdepasse"));
                }
            }
        }
        return res;
    }
    
}
