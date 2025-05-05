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


public class Typeoperation {

    /**
     * @return the idtype
     */
    public int getIdtype() {
        return idtype;
    }

    /**
     * @return the destype
     */
    public String getDestype() {
        return destype.substring(0, 1).toUpperCase() + destype.substring(1);
    }

    /**
     * @param destype the destype to set
     */
    public void setDestype(String destype) {
        this.destype = destype;
    }
    
    private int idtype;
    private String destype;
    
    public Typeoperation(int idtype,String destype){
        this.idtype=idtype;
        this.destype=destype.substring(0, 1).toUpperCase() + destype.substring(1);
    }
    
    public Typeoperation(String dectype){
        this(-1,dectype);
    }
    
    @Override
    public String toString (){
        return this.destype;
        }
      
    public static Typeoperation nouvelletypeoperation() throws SQLException{
      System.out.println("DESCRIPTION");
      String des=Lire.S();
      return new Typeoperation(des);
    }
    
    public void savetypeoperation(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into typeoperation (destype) values (?)")) {
            pst.setString(1, this.destype);
            pst.executeUpdate();
        }
    }
    
    public void updatetypeoperation(Connection con,String des) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                    "UPDATE typeoperation SET destype=? WHERE idmachine=?")) {
                pst.setString(1, des);
                pst.setInt(2, idtype);
                pst.executeUpdate();
            }
    }
    
    public void deletetypeoperation(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "delete from typeoperation where idtype=?")) {
            pst.setInt(1, idtype);
            pst.executeUpdate();
        }
    }
  
    public static List<Typeoperation> tousLestypeoperations(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select idtype,destype from typeoperation")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Typeoperation temp= new Typeoperation (rs.getInt("idtype"),rs.getString("destype"));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public static Typeoperation getTypeopbyId(Connection con, int id) throws SQLException{
        Typeoperation res=null;
        try (PreparedStatement pst = con.prepareStatement(
                "select * from typeoperation where idtype=?")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res= new Typeoperation (rs.getInt("idtype"),rs.getString("destype"));                  
                }
               }
        }
        return res;
    }
    
    
}
