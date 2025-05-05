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
import java.util.Iterator;
import java.util.List;


public class Produit 
{

    /**
     * @return the tempsproduction
     */
    public Float getTempsproduction() {
        return tempsproduction;
    }

    /**
     * @param tempsproduction the tempsproduction to set
     */
    public void setTempsproduction(Float tempsproduction) {
        this.tempsproduction = tempsproduction;
    }

    /**
     * @param idproduit the idproduit to set
     */
    public void setIdproduit(int idproduit) {
        this.idproduit = idproduit;
    }

    /**
     * @return the stock
     */
    public float getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(float stock) {
        this.stock = stock;
    }

    /**
     * @return the idproduit
     */
    public int getIdproduit() {
        return idproduit;
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
     * @return the desproduit
     */
    public String getDesproduit() {
        return desproduit;
    }

    /**
     * @param desproduit the desproduit to set
     */
    public void setDesproduit(String desproduit) {
        this.desproduit = desproduit;
    }
    
    private int idproduit;
    private String refproduit;
    private String desproduit;
    private float stock;
    private Float tempsproduction;
    
    public Produit (int idproduit,String refproduit, String desproduit,float stock){
        this.idproduit=idproduit;
        this.refproduit=refproduit.toUpperCase();
        this.desproduit=desproduit.substring(0,1)+desproduit.substring(1);
        this.stock=stock;
    }
    
    public Produit(String refproduit, String desproduit,float stock){
        this(-1,refproduit,desproduit,stock);
    }
    
    public Produit(String refproduit, String desproduit){
        this(-1,refproduit,desproduit,10);
    }
    
    @Override
    public String toString(){
        return this.getRefproduit()+" | "+this.getDesproduit();
    }
    
    public void saveproduit(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into produits (refproduit,desproduit,stock) values (?,?,?)")) {
            pst.setString(1, this.getRefproduit());
            pst.setString(2, this.getDesproduit());
            pst.setFloat(3, this.getStock());
            pst.executeUpdate();
        }
    }
    
    public void updateProduit(Connection conn,String ref, String des, float stock) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(
                "UPDATE produits SET refproduit = ?, desproduit = ?, stock = ? WHERE idproduit = ?")) {
            pst.setString(1, ref);
            pst.setString(2, des);
            pst.setFloat(3, stock);
            pst.setInt(4, idproduit);
            pst.executeUpdate();
        }
    }
    
    public void updateProduit(Connection conn,float stock) throws SQLException {
            this.updateProduit(conn, refproduit, desproduit, stock);
    }

    public void deleteProduit(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "delete from produits where idproduit=?")) {
            pst.setInt(1, idproduit);
            pst.executeUpdate();
        }
    
    }
    
    public static List<Produit> tousLesProduits(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select idproduit,refproduit,desproduit,stock from produits")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Produit temp= new Produit (rs.getInt("idproduit"),rs.getString("refproduit"),rs.getString("desproduit"),rs.getFloat("stock"));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public static Produit getproduitbyRef(Connection con,String ref)throws SQLException{
        Produit res=null;
        try (PreparedStatement pst = con.prepareStatement(
                "select * from produits where refproduit=?")) {
            pst.setString(1, ref);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res= new Produit (rs.getInt("idproduit"),rs.getString("refproduit"),rs.getString("desproduit"),rs.getFloat("stock"));                  
                }
               }
        }
        return res;
    }
    
    public List<String[]> Opsassociees(Connection con)throws SQLException{
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
            "    WITH RankedRealise AS (\n" +
            "    SELECT\n" +
            "        po.opavant AS opavant,\n" +
            "        po.opapres AS opapres,\n" +
            "        typeoperation.destype AS destypeavant,\n" +
            "        machine.refmachine AS refmachineavant,\n" +
            "        typeoperation_apres.destype AS destypeapres,\n" +
            "        machine_apres.refmachine AS refmachineapres,\n" +
            "        realise.duree AS dureeavant,\n" +
            "        realise_apres.duree AS dureeapres,\n" +
            "        realise.duree+realise_apres.duree AS dureetotale,\n" +
            "        machine.statut AS statutavant,\n" +
            "        machine_apres.statut AS machine_apresstatut,\n" +
            "        machine.idposte AS posteavant,\n" +
            "        machine_apres.idposte AS machine_apresposte\n" +
            "    FROM precedenceoperations po\n" +
            "    JOIN touteslesoperations tloAvant ON tloAvant.idtoutesop = po.opavant\n" +
            "    JOIN touteslesoperations tloApres ON tloApres.idtoutesop = po.opapres\n" +
            "    JOIN typeoperation ON typeoperation.idtype = tloAvant.idtype\n" +
            "    JOIN typeoperation AS typeoperation_apres ON typeoperation_apres.idtype = tloApres.idtype\n" +
            "    JOIN realise ON tloAvant.idtype = realise.idtype\n" +
            "    JOIN realise AS realise_apres ON tloApres.idtype = realise_apres.idtype\n" +
            "    JOIN machine ON realise.idmachine = machine.idmachine\n" +
            "    JOIN machine AS machine_apres ON realise_apres.idmachine = machine_apres.idmachine\n" +
            "\n" +
            "    WHERE tloAvant.idproduit = ? AND machine.statut = 'Disponible' AND machine_apres.statut='Disponible' AND machine.idposte !='-1' AND machine_apres.idposte !='-1' \n" +
            "),\n" +
            "RankedMachines AS (\n" +
            "    SELECT\n" +
            "        opavant,\n" +
            "        opapres,\n" +
            "        destypeavant,\n" +
            "        refmachineavant,\n" +
            "        dureeavant,\n" +
            "        refmachineapres,\n" +
            "        destypeapres,\n" +
            "        dureeapres,\n" +
            "        dureetotale,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY opavant, opapres ORDER BY dureetotale) AS row_num\n" +
            "    FROM RankedRealise\n" +
            ")\n" +
            "SELECT\n" +
            "    opavant,\n" +
            "    destypeavant,\n" +
            "    refmachineavant,\n" +
            "    dureeavant,\n" +
            "    opapres,\n" +
            "    destypeapres,\n" +
            "    refmachineapres,\n" +
            "    dureeapres,\n" +
            "    SUM(dureetotale) AS total_duration\n" +
            "FROM RankedMachines\n" +
            "WHERE row_num = 1\n" +
            "GROUP BY\n" +
            "    opavant,\n" +
            "    destypeavant,\n" +
            "    refmachineavant,\n" +
            "    dureeavant,\n" +
            "    opapres,\n" +
            "    destypeapres,\n" +
            "    refmachineapres,\n" +
            "    dureeapres;\n" +
            "")) {
            pst.setInt(1, this.getIdproduit());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[9];
                    row[0] = rs.getString("opavant");
                    row[1] = rs.getString("destypeavant");
                    row[2] = rs.getString("refmachineavant");
                    row[3] = rs.getString("dureeavant");
                    row[4] = rs.getString("opapres");
                    row[5] = rs.getString("destypeapres");
                    row[6] = rs.getString("refmachineapres");
                    row[7] = rs.getString("dureeapres");
                    row[8] = rs.getString("total_duration");
                    res.add(row);
                }
            }
        }
        for (int i = 0; i < res.size(); i++) {
            String[] currentRow = res.get(i);
            String opapres = currentRow[4];
            for (int j = i + 1; j < res.size(); j++) {
                String[] nextRow = res.get(j);
                String opavant = nextRow[0];
                if (opapres!=null && opavant!=null && opapres.equals(opavant)) {
                    res.remove(j);
                    res.add(i + 1, nextRow);
                    break;
                }
                
                if (opapres!=null && opavant!=null && opapres.equals(nextRow[4])) {
                    if(j==1){
                        res.add(j, new String[9]);
                        break;
                    }else{
                        res.add(j-1, new String[9]);
                        break;
                    }
                }
            }
        }
        return res;
    }
    
    public static List<Produit> tousLesProduitsUsinables(Connection con) throws SQLException {
        List<Produit> res = Produit.tousLesProduits(con);
        Iterator<Produit> iterator = res.iterator();
        while (iterator.hasNext()) {
            Produit prod = iterator.next();
            if (prod.Opsassociees(con).isEmpty()) {
                iterator.remove();
                continue;
            }
            for(String[] operation : prod.Opsassociees(con)){
                if(operation[0]!=null){
                    if(Phasefabrication.OperateurDisponible(con, operation[2])==null || Phasefabrication.OperateurDisponible(con, operation[6])==null){
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        return res;
    }

}
