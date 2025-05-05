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


public class precedenceoperations 
{

    /**
     * @return the opavant
     */
    public touteslesoperations getOpavant() {
        return opavant;
    }

    /**
     * @param opavant the opavant to set
     */
    public void setOpavant(touteslesoperations opavant) {
        this.opavant = opavant;
    }

    /**
     * @return the opapres
     */
    public touteslesoperations getOpapres() {
        return opapres;
    }

    /**
     * @param opapres the opapres to set
     */
    public void setOpapres(touteslesoperations opapres) {
        this.opapres = opapres;
    }

    /**
     * @return the desopavant
     */
    public String getDesopavant() {
        return this.opavant.getIdtoutesop()+" | "+this.getOpavant().getDestype();
    }

    /**
     * @return the desopapres
     */
    public String getDesopapres() {
        return this.opapres.getIdtoutesop()+" | "+this.getOpapres().getDestype();
    }

    /**
     * @return the refproduit
     */
    public String getRefproduit() {
        return refproduit;
    }

    /**
     * @param refproduit the refproduit to set
     */
    public void setRefproduit(String refproduit) {
        this.refproduit = refproduit;
    }

    /**
     * @return the idopavant
     */
    public int getIdopavant() {
        return idopavant;
    }

    /**
     * @param idopavant the idopavant to set
     */
    public void setIdopavant(int idopavant) {
        this.idopavant = idopavant;
    }

    /**
     * @return the idopapres
     */
    public int getIdopapres() {
        return idopapres;
    }

    /**
     * @param idopapres the idopapres to set
     */
    public void setIdopapres(int idopapres) {
        this.idopapres = idopapres;
    }
    private int idopavant;
    private int idopapres;
    private touteslesoperations opavant;
    private touteslesoperations opapres;
    private String refproduit;
    
    public precedenceoperations(int opavant,int opapres){
        this.idopavant=opavant;
        this.idopapres=opapres;
    }
    
    public String toString(){
    return this.getIdopavant()+";"+this.getIdopapres();
    }
    
    public static precedenceoperations nouvelleprecedence(){
        System.out.println("OPAvant, OPApres");
        int opavant=Lire.i();
        int opapres=Lire.i();
        
        return new precedenceoperations (opavant,opapres);
    }
    
    public void saveprecedence(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into precedenceoperations (opavant,opapres) values (?,?)")) {
            pst.setInt(1, this.getIdopavant());
            pst.setInt(2, this.getIdopapres());
            pst.executeUpdate();
        }
    }
    
    public  void updatePrecedence(Connection conn,int newopavant,int newopapres) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE precedenceoperations SET opavant=?, opapres=? WHERE opavant=? AND opapres=?")) {
                pst.setInt(1, newopavant);
                pst.setInt(2, newopapres);
                pst.setInt(3, this.idopavant);
                pst.setInt(4, this.idopapres);
                pst.executeUpdate();
            }
    }
    
    public void deletePrecedence(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "DELETE FROM precedenceoperations WHERE opavant=? AND opapres=?")) {
            pst.setInt(1, this.idopavant);
            pst.setInt(2, this.idopapres);
            pst.executeUpdate();
        }
    }
    
    public static List<precedenceoperations> toutesLesPrecedences(Connection conn) throws SQLException{
       List res = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(
                "select opavant,opapres,refproduit from precedenceoperations join touteslesoperations on opavant=touteslesoperations.idtoutesop join produits on touteslesoperations.idproduit=produits.idproduit")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    precedenceoperations temp= new precedenceoperations (rs.getInt("opavant"),rs.getInt("opapres"));
                    temp.setOpavant(touteslesoperations.getOpbyId(conn, temp.getIdopavant()));
                    temp.setOpapres(touteslesoperations.getOpbyId(conn, temp.getIdopapres()));
                    temp.setRefproduit(rs.getString("refproduit"));
                    res.add(temp);
                }
            }
        }
        return res;
    }     
}
