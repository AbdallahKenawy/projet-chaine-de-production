/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Poste {
    
    private int idposte;
    
    public Poste(int idposte){
        this.idposte=idposte;
    }
    
    public Poste(){
        this(0);
    }
    
    public void savePosteinit(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "insert into poste (idposte) values (?)")) {
            pst.setInt(1, idposte);
            pst.executeUpdate();        
        }
    }
    
    public void savePoste(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "insert into poste VALUES (DEFAULT)")) {
            pst.executeUpdate();
        }
    }
    
    public void deletePoste(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "delete from poste where idposte=?")) {
            pst.setInt(1, idposte);
            pst.executeUpdate();
        }
    }
    
    public static List<Poste> tousLesPostes(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select * from poste")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                        res.add(new Poste(rs.getInt("idposte")));
                }
            }
        }
        return res;
    }
    
    public List <Machine> MachinesAssociees(Connection con) throws SQLException{
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select * from machine where idposte=?")) {
            pst.setInt(1, idposte);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                   res.add(new Machine(rs.getInt("idmachine"),rs.getString("refmachine"),rs.getString("desmachine"),rs.getFloat("puissance"),rs.getInt("idposte"),rs.getString("statut")));
                }
            }
        }
        return res;
    }

    /**
     * @return the idposte
     */
    public int getIdposte() {
        return idposte;
    }

    /**
     * @param idposte the idposte to set
     */
    public void setIdposte(int idposte) {
        this.idposte = idposte;
    }
    
    public String toString(){
        if(this.idposte==-1){
            return "Entrepôt";
        }else{
            return "Poste: "+this.idposte;
        }
    }
    
    public List<String[]> OperationsdansPoste(Connection con) throws SQLException{
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT numserie,machine.refmachine,destype,nom,prenom,tempsdebut,tempsfin FROM phasefabrication\n" +
                "JOIN machine ON machine.refmachine=phasefabrication.refmachine\n" +
                "Join operateur ON phasefabrication.idoperateur=operateur.idoperateur\n" +
                "JOIN poste ON poste.idposte=machine.idposte\n" +
                "Join touteslesoperations ON phasefabrication.idtoutesop=touteslesoperations.idtoutesop\n" +
                "JOIN typeoperation ON touteslesoperations.idtype=typeoperation.idtype\n" +
                "WHERE poste.idposte=? AND CURRENT_TIME < phasefabrication.tempsfin\n" +
                "ORDER BY tempsdebut,numserie"
            )) {
            pst.setInt(1, this.idposte);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[6];
                    row[0] = rs.getString("numserie");
                    row[1] = rs.getString("refmachine");
                    row[2] = rs.getString("destype");
                    row[3] = rs.getString("nom")+" "+rs.getString("Prenom");
                    row[4] = Exemplaire.formulationdate(rs.getTimestamp("tempsdebut"));
                    row[5] = Exemplaire.formulationdate(rs.getTimestamp("tempsfin"));
                    res.add(row);
                }
            }
        }
        return res;
    }
    
}
