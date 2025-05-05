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


public class Realise 
{

    /**
     * @return the machine
     */
    public Machine getMachine() {
        return machine;
    }

    /**
     * @param machine the machine to set
     */
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

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

    private int idmachine;
    private int idtype;
    private float duree;
    private Machine machine;
    private Typeoperation typeop;
    
    /**
     * @return the idmachine
     */
    public int getIdmachine() {
        return idmachine;
    }

    /**
     * @param idmachine the idmachine to set
     */
    public void setIdmachine(int idmachine) {
        this.idmachine = idmachine;
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
     * @return the duree
     */
    public float getDuree() {
        return duree;
    }

    /**
     * @param duree the duree to set
     */
    public void setDuree(float duree) {
        this.duree = duree;
    }
    
    public String getRefmachine(){
        return this.machine.getRefmachine();
    }
    
    public String getDestype(){
        return this.typeop.getDestype();
    }
    
    public Realise (int idmachine, int idtype, float duree){
        this.idmachine=idmachine;
        this.idtype=idtype;
        this.duree=duree;
    }
    
    public String toString(){
        return "IDMachine:"+this.getIdmachine()+"; IDType:"+this.getIdtype()+"; Duree:"+this.getDuree();
    }
    
    public static Realise nouvellerealise (){
        
        System.out.println("IDMachine, IDType, Duree");
        int idmachine=Lire.i();
        int idtype=Lire.i();
        float duree =Lire.f();
        
        return new Realise(idmachine,idtype,duree);
    }
    
    public void saverealise(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into realise (idmachine,idtype,duree) values (?,?,?)")) {
            pst.setInt(1, this.getIdmachine());
            pst.setInt(2, this.getIdtype());
            pst.setFloat(3, this.getDuree());
            pst.executeUpdate();
        }
    }
    
    public  void updateRealise(Connection conn,int newidmachine,int newidtype,Float newduree) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE realise SET idmachine=?, idtype=?, duree=? WHERE idmachine=? AND idtype=? AND duree=?")) {
                pst.setInt(1, newidmachine);
                pst.setInt(2, newidtype);
                pst.setFloat(3, newduree);
                pst.setInt(4, this.idmachine);
                pst.setInt(5, this.idtype);
                pst.setFloat(6, this.duree);
                pst.executeUpdate();
            }
    }
    
    public void deleteRealise(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "DELETE FROM realise WHERE idmachine=? AND idtype=? AND duree=?")) {
            pst.setInt(1, this.idmachine);
            pst.setInt(2, this.idtype);
            pst.setFloat(3, this.duree);
            pst.executeUpdate();
        }
    }
    
    public static List<Realise> tousLesRealise(Connection conn) throws SQLException{
       List res = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT * FROM realise JOIN machine ON realise.idmachine=machine.idmachine JOIN typeoperation ON realise.idtype=typeoperation.idtype")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Realise temp= new Realise (Integer.parseInt(rs.getString("idmachine")),Integer.parseInt(rs.getString("idtype")),rs.getFloat("duree"));
                    temp.setMachine(new Machine(rs.getInt("idmachine"),rs.getString("refmachine"),rs.getString("desmachine"),rs.getFloat("puissance"),rs.getInt("idposte"),rs.getString("statut")));
                    temp.setTypeop(new Typeoperation(rs.getInt("idtype"),rs.getString("destype")));
                    
                    res.add(temp);
                }
            }
        }
        return res;
    } 
}
