/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Utilisateur 
{

    /**
     * @return the idutilisateur
     */
    public int getIdutilisateur() {
        return idutilisateur;
    }

    /**
     * @param idutilisateur the idutilisateur to set
     */
    public void setIdutilisateur(int idutilisateur) {
        this.idutilisateur = idutilisateur;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the identifiant
     */
    public String getIdentifiant() {
        return identifiant;
    }

    /**
     * @param identifiant the identifiant to set
     */
    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    /**
     * @return the motdepasse
     */
    public String getMotdepasse() {
        return motdepasse;
    }

    /**
     * @param motdepasse the motdepasse to set
     */
    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }
    private int idutilisateur;
    private String nom;
    private String prenom;
    private String identifiant;
    private String motdepasse;
    
    public Utilisateur(int idutilisateur,String nom,String prenom,String identifiant,String motdepasse){
        this.idutilisateur=idutilisateur;
        this.nom=nom.toUpperCase();
        this.prenom=prenom.substring(0,1).toUpperCase()+prenom.substring(1);
        this.identifiant=identifiant;
        this.motdepasse=motdepasse;
    }
    
    public Utilisateur(String nom,String prenom,String identifiant,String motdepasse){
        this(-1,nom,prenom,identifiant,motdepasse);
    }
    
    public void saveUtilisateur(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "insert into utilisateur (nom,prenom,identifiant,motdepasse) values (?,?,?,?)")) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, identifiant);
            pst.setString(4, motdepasse);
            pst.executeUpdate();
        }
    }
    
    public static int verificationUtilisateur (Connection con,String identifiant,String motdepasse) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "select * from utilisateur where identifiant=? && motdepasse=?")) {
            pst.setString(1, identifiant);
            pst.setString(2, motdepasse);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idutilisateur");
                }else{
                    return 0;
                }
            }
        }
    }
    
}
