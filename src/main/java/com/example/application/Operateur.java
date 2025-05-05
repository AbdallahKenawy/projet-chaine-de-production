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
import java.util.Objects;


public class Operateur {

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
    public String getMdp() {
        return getMotdepasse();
    }

    /**
     * @param motdepasse the motdepasse to set
     */
    public void setMdp(String motdepasse) {
        this.setMotdepasse(motdepasse);
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
    
    public String getCivilite(){
        return this.nom +" "+this.prenom;
    }
    
    public void setCivilite(String Civilite){
        String[] parts = Civilite.split("\\s+");        
        this.setNom(parts[0]);
        this.setPrenom(parts[parts.length-1]);
    }

    /**
     * @return the idposte
     */
    
    private int idoperateur;
    private String nom;
    private String prenom;
    private String identifiant;
    private String motdepasse;
    private String statut;
    
    public Operateur(int idoperateur,String nom, String prenom,String statut, String identifiant,String motdepasse){
        this.idoperateur=idoperateur;
        this.nom=nom.toUpperCase();
        this.prenom=prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
        this.identifiant=identifiant;
        this.motdepasse=motdepasse;
        this.statut=statut;
    }
    
    public Operateur(int idoperateur,String nom, String prenom, String identifiant,String motdepasse)
    {
        this(idoperateur,nom,prenom,"Disponible",identifiant,motdepasse);
    }
    
    public Operateur(String nom, String prenom,String identifiant,String motdepasse){
        this(-1,nom,prenom,identifiant,motdepasse);
    }
    
    public Operateur(int idoperateur,String nom,String prenom){
        this(idoperateur,nom,prenom,prenom.substring(0,1).toLowerCase()+nom.toLowerCase()+"01",prenom.substring(0,1).toLowerCase()+nom.toLowerCase()+"01");
    }
    
    public Operateur(String nom,String prenom){
        this(-1,nom,prenom,prenom.substring(0,1).toLowerCase()+nom.toLowerCase()+"01",prenom.substring(0,1).toLowerCase()+nom.toLowerCase()+"01");
    }
    
    @Override
    public String toString(){
        return this.nom+" "+this.prenom;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Operateur operateur = (Operateur) obj;
        return idoperateur == operateur.idoperateur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idoperateur);
    }
    
    public void saveOperateur(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "insert into operateur (nom,prenom,identifiant,motdepasse,statut) values (?,?,?,?,?)")) {
            pst.setString(1,nom);
            pst.setString(2,prenom );
            pst.setString(3, identifiant);
            pst.setString(4, motdepasse);
            pst.setString(5, statut);
            pst.executeUpdate();
        }
    }
    
    public void deleteOperateur(Connection conn) throws SQLException{
        try (PreparedStatement pst = conn.prepareStatement(
                "delete from operateur where idoperateur=?")) {
            pst.setInt(1, idoperateur);
            pst.executeUpdate();
        }
    }
    
    public void updateOperateurAdmin(Connection conn, String nom,String prenom,String identifiant,String motdepasse,String statut) throws SQLException{
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE operateur SET nom=?, prenom=?,identifiant=?,motdepasse=?,statut=? WHERE idoperateur=?")) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, identifiant);
                pst.setString(4, motdepasse);
                pst.setString(5, statut);
                pst.setInt(6, idoperateur);
                pst.executeUpdate();
            }
    }
    
    public void updateOperateur(Connection conn, String nom,String prenom,String statut) throws SQLException{
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE operateur SET nom=?, prenom=?,statut=? WHERE idoperateur=?")) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, statut);
                pst.setInt(4, idoperateur);
                pst.executeUpdate();
            }
    }
    
    
    public static List<Operateur> tousLesOperateurs(Connection con) throws SQLException {
        List res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(
                "select * from operateur")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Operateur temp= new Operateur (rs.getInt("idoperateur"),rs.getString("nom"),rs.getString("prenom"),rs.getString("statut"), rs.getString("identifiant"),rs.getString("motdepasse"));
                    res.add(temp);
                }
            }
        }
        return res;
    }
    
    public static Operateur getOperateurbyId(Connection con,int id) throws SQLException{
        Operateur res=null;
        try (PreparedStatement pst = con.prepareStatement(
                "select * from operateur where idoperateur=?")) {
            pst.setInt(1,id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res= new Operateur (rs.getInt("idoperateur"),rs.getString("nom"),rs.getString("prenom"),rs.getString("identifiant"),rs.getString("motdepasse"));
                }
            }
        }
        return res;
    }
    
        public List<String> ListeHabilitations(Connection con) throws SQLException{
              List <String> res = new ArrayList<>();
              try (PreparedStatement pst = con.prepareStatement(
                      "select refmachine from habilitation join machine on habilitation.idmachine=machine.idmachine where idoperateur=?")) {
                      pst.setInt(1, idoperateur);
                  try (ResultSet rs = pst.executeQuery()) {
                      while (rs.next()) {
                          res.add(rs.getString("refmachine"));
                      }
                  }
              }
              return res;
          }
        
        public static int verificationOperateur (Connection con,String identifiant,String motdepasse) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "select * from operateur where identifiant=? && motdepasse=?")) {
            pst.setString(1, identifiant);
            pst.setString(2, motdepasse);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idoperateur");
                }else{
                    return 0;
                }
            }
        }
    }
    
    
}
