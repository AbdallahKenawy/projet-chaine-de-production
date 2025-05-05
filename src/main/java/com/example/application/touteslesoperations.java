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


public class touteslesoperations 
{    

    /**
     * @return the typeop
     */
    public Typeoperation getTypeop() {
        return typeop;
    }

    /**
     * @param typeop the typeop to set
     */
    public void setTypeop(Typeoperation typeop) {
        this.typeop = typeop;
    }

    /**
     * @return the produit
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * @param produit the produit to set
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    private int idtoutesop;
    private int idtype;
    private int idproduit;
    private Produit produit;
    private Typeoperation typeop;
    
    /**
     * @param idtoutesop the idtoutesop to set
     */
    public void setIdtoutesop(int idtoutesop) {
        this.idtoutesop = idtoutesop;
    }

    /**
     * @return the destype
     */
    public String getDestype() {
        return this.typeop.getDestype();
    }

    /**
     * @return the refproduit
     */
    public String getRefproduit() {
        return this.produit.getRefproduit();
    }


    /**
     * @return the idtoutesop
     */
    public int getIdtoutesop() {
        return idtoutesop;
    }

    /**
     * @return the idtype
     */
    public int getIdtype() {
        return idtype;
    }

    /**
     * @param idtype the idtype to set
     */
    public void setIdtype(int idtype) {
        this.idtype = idtype;
    }

    /**
     * @return the idproduit
     */
    public int getIdproduit() {
        return idproduit;
    }

    /**
     * @param idproduit the idproduit to set
     */
    public void setIdproduit(int idproduit) {
        this.idproduit = idproduit;
    }
    
    public touteslesoperations (int idtoutesop,int idtype, int idproduit)
    {
        this.idtoutesop=idtoutesop;
        this.idtype=idtype;
        this.idproduit=idproduit;
    }
    
    public touteslesoperations (int idtype, int idproduit){
        this(-1,idtype,idproduit);
    }
    
    @Override
    public String toString(){
        return this.getIdtoutesop()+" | "+this.getProduit().getRefproduit()+" | "+this.getTypeop().getDestype();
    }
    
    public static touteslesoperations nouvelleoperation(){
        System.out.println("IDType, IDProduit");
        int idtype=Lire.i();
        int idproduit=Lire.i();
        
        return new touteslesoperations(idtype,idproduit);
    }
    
    public void saveoperation(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into touteslesoperations (idtype,idproduit) values (?,?)")) {
            pst.setInt(1, this.getIdtype());
            pst.setInt(2, this.getIdproduit());
            pst.executeUpdate();
        }
    }
    
    public void updateOperation(Connection conn,int newidproduit,int newidtype) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE touteslesoperations SET idproduit=?, idtype=? WHERE idtoutesop=?")) {
                pst.setInt(1, newidproduit);
                pst.setInt(2, newidproduit);
                pst.setInt(3, this.idtoutesop);
                pst.executeUpdate();
            }
    }
    
    public void deleteOperation(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "DELETE FROM touteslesoperations WHERE idtoutesop=?")) {
            pst.setInt(1, this.idtoutesop);
            pst.executeUpdate();
        }
    }
    
    public static List<touteslesoperations> toutesLesOps(Connection conn) throws SQLException{
       List res = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT * FROM touteslesoperations JOIN produits ON touteslesoperations.idproduit=produits.idproduit JOIN typeoperation on touteslesoperations.idtype=typeoperation.idtype ORDER BY refproduit")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    touteslesoperations temp= new touteslesoperations (rs.getInt("idtoutesop"),rs.getInt("idtype"),rs.getInt("idproduit"));
                    temp.setProduit(new Produit(rs.getInt("idproduit"),rs.getString("refproduit"),rs.getString("desproduit"),rs.getFloat("stock")));
                    temp.setTypeop(new Typeoperation(rs.getInt("idtype"),rs.getString("destype")));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public static touteslesoperations getOpbyId(Connection con, int id) throws SQLException{
        touteslesoperations res=null;
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM touteslesoperations JOIN produits ON touteslesoperations.idproduit=produits.idproduit JOIN typeoperation on touteslesoperations.idtype=typeoperation.idtype WHERE idtoutesop=?")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res= new touteslesoperations (Integer.parseInt(rs.getString("idtoutesop")),Integer.parseInt(rs.getString("idtype")),Integer.parseInt(rs.getString("idproduit")));
                    res.setProduit(new Produit(rs.getInt("idproduit"),rs.getString("refproduit"),rs.getString("desproduit"),rs.getFloat("stock")));
                    res.setTypeop(new Typeoperation(rs.getInt("idtype"),rs.getString("destype")));
               }
        }
        return res;
      }
    }
}
