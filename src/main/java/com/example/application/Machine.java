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


public class Machine {

    /**
     * @return the statut
     */
    public String getStatut() {
        return statut;
    }

    /**
     * @param statut the statut to set
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * @param idmachine the idmachine to set
     */
    public void setIdmachine(int idmachine) {
        this.idmachine = idmachine;
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

    /**
     * @return the idmachine
     */
    public int getIdmachine() {
        return idmachine;
    }

    /**
     * @return the refmachine
     */
    public String getRefmachine() {
        return refmachine;
    }

    /**
     * @param refmachine the refmachine to set
     */
    public void setRefmachine(String refmachine) {
        this.refmachine = refmachine;
    }

    /**
     * @return the desmachine
     */
    public String getDesmachine() {
        return desmachine.substring(0, 1).toUpperCase() + desmachine.substring(1);
    }

    /**
     * @param desmachine the desmachine to set
     */
    public void setDesmachine(String desmachine) {
        this.desmachine = desmachine;
    }

    /**
     * @return the puissance
     */
    public float getPuissance() {
        return puissance;
    }

    /**
     * @param puissance the puissance to set
     */
    public void setPuissance(float puissance) {
        this.puissance = puissance;
    }
    
    private int idmachine;
    private String refmachine;
    private String desmachine;
    private float puissance;
    private int idposte;
    private String statut;
    
    public Machine(int idmachine,String refmachine ,String desmachine,float puissance,int idposte,String statut){
        this.idmachine=idmachine;
        this.desmachine=desmachine.substring(0,1).toUpperCase()+desmachine.substring(1);
        this.refmachine=refmachine.toUpperCase();
        this.puissance=puissance;
        this.idposte=idposte;
        this.statut=statut;
    }
    
    public Machine(String refmachine ,String desmachine,float puissance){
        this(-1,refmachine,desmachine,puissance,-1,"Disponible");
    }
    
    public Machine(String refmachine ,String desmachine,float puissance,int idposte){
        this(-1,refmachine,desmachine,puissance,idposte,"Disponible");
    }
    
    @Override
    public String toString (){
        return this.refmachine+" | "+this.desmachine+" | "+this.puissance + "(W)";
    }
    
    public static Machine nouvellemachine() throws SQLException{
        System.out.println("REF,DES,PUISSANCE");
        String ref=Lire.S();
        String des=Lire.S();
        float puissance=Lire.f();
        return new Machine (ref,des,puissance);
    }
    
    public void savemachine(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into machine (refmachine,desmachine,puissance,idposte,statut) values (?,?,?,?,?)")) {
            pst.setString(1, this.refmachine);
            pst.setString(2, this.desmachine);
            pst.setFloat(3, this.puissance);
            pst.setInt(4, this.idposte);
            pst.setString(5, "Disponible");
            pst.executeUpdate();
        }
    }
    
    public void deletemachine(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "delete from machine where idmachine=?")) {
            pst.setInt(1, this.idmachine);
            pst.executeUpdate();
        }
    }
    
    public void updatemachine(Connection conn, String ref,String des,float puis,int idposte,String statut) throws SQLException{
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE machine SET refmachine=?, desmachine=?, puissance=?, idposte=?, statut=? WHERE idmachine=?")) {
                pst.setString(1, ref);
                pst.setString(2, des);
                pst.setFloat(3, puis);
                pst.setInt(4, idposte);
                pst.setString(5, statut);
                pst.setInt(6, this.idmachine);
                pst.executeUpdate();
            }
                if(statut.equals("En panne")){
                    this.MachineenPanne(conn);
            }
    }
    
    public static List<Machine> tousLesMachines(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select * from machine")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Machine temp= new Machine (rs.getInt("idmachine"),rs.getString("refmachine"),rs.getString("desmachine"),rs.getFloat("puissance"),rs.getInt("idposte"),rs.getString("statut"));
                    if(!temp.statut.equals("En panne")){
                        temp.EtatMachine(con);    
                    }
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public List<Operateur> OperateurHabilites(Connection con) throws SQLException{
        List res = new ArrayList<>();       
        try (PreparedStatement pst = con.prepareStatement(
                 "select * from habilitation join operateur on habilitation.idoperateur=operateur.idoperateur where idmachine=?")) {
                pst.setInt(1, this.idmachine);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Operateur(rs.getInt("idoperateur"),rs.getString("nom"),rs.getString("prenom")));
                }
            }
        }
        return res;
    }
    
    public void SupprimeOperateurHabilites(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                 "DELETE from habilitation where idmachine=?")) {
                pst.setInt(1, this.idmachine);
                pst.executeUpdate();
        }
    }
    
    public void EtatMachine(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                 "Select * FROM phasefabrication\n" +
                 "WHERE phasefabrication.refmachine=? AND CURRENT_TIME BETWEEN phasefabrication.tempsdebut AND phasefabrication.tempsfin")) {
                pst.setString(1, this.refmachine);
            try (ResultSet rs = pst.executeQuery()) {
                if(rs.next())
                    this.setStatut("Production");
                else{
                    this.setStatut("Disponible");
                }
            }
        }
    }
    
    public void MachineenPanne(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "UPDATE exemplaire\n" +
                "SET exemplaire.statut = 'Annulee'\n" +
                "WHERE EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM phasefabrication\n" +
                "    WHERE exemplaire.numserie = phasefabrication.numserie\n" +
                "      AND phasefabrication.refmachine = ?\n" +
                "      AND CURRENT_TIME < phasefabrication.tempsdebut AND CURRENT_TIME < phasefabrication.tempsfin\n" +
                ");"
                )){
                pst.setString(1, this.refmachine);
                pst.executeUpdate();
                }
        
        try (PreparedStatement pst = con.prepareStatement(
                "DELETE phasefabrication\n" +
                "FROM phasefabrication\n" +
                "JOIN exemplaire ON phasefabrication.numserie = exemplaire.numserie\n" +
                "WHERE exemplaire.statut = 'Annulee';\n" +
                ""
                )){
                pst.executeUpdate();
                }
    }
}
