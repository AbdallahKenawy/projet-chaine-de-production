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


public class Habilitation 
{
    private int idoperateur;
    private int idmachine;
    
    public Habilitation(int idoperateur,int idmachine){
        this.idoperateur=idoperateur;
        this.idmachine=idmachine;
    }
    
    public void savehabilitation(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into habilitation (idoperateur,idmachine) values (?,?)")) {
            pst.setInt(1, this.idoperateur);
            pst.setInt(2, this.idmachine);
            pst.executeUpdate();
        }
    }
    
    public static void deletehabiltiation(Connection conn,int idoperateur, int idmachine) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "delete from habilitation where idoperateur=? && idmachine=?")) {
            pst.setInt(1, idoperateur);
            pst.setInt(2, idmachine);
            pst.executeUpdate();
        }
    }
    
//    public static void updatemachine(Connection conn,int idoperateur, int idmachine) throws SQLException{
//            try (PreparedStatement pst = conn.prepareStatement(
//                    "UPDATE machine SET idoperateur=?, idmachine=?, puissance=?, idposte=? WHERE idmachine=?")) {
//                pst.setString(1, ref);
//                pst.setString(2, des);
//                pst.setFloat(3, puis);
//                pst.setInt(4, idposte);
//                pst.setInt(5, idmachine);
//                pst.executeUpdate();
//            }
//    }
    
    public static List<Habilitation> tousLesHabilitations(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select idoperateur,idmachine from habilitation")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Habilitation temp= new Habilitation (rs.getInt("idoperateur"),rs.getInt("idmachine"));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    
    
}
