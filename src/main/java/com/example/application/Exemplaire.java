/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Exemplaire {

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
     * @return the refcommande
     */
    public String getRefcommande() {
        return refcommande;
    }

    /**
     * @param refcommande the refcommande to set
     */
    public void setRefcommande(String refcommande) {
        this.refcommande = refcommande;
    }

    /**
     * @return the numserie
     */
    public String getNumserie() {
        return numserie;
    }

    /**
     * @param numserie the numserie to set
     */
    public void setNumserie(String numserie) {
        this.numserie = numserie;
    }

    /**
     * @return the datedebut
     */
    public Timestamp getDatedebut() {
        return datedebut;
    }

    /**
     * @param datedebut the datedebut to set
     */
    public void setDatedebut(Timestamp datedebut) {
        this.datedebut = datedebut;
    }

    /**
     * @return the datefin
     */
    public Timestamp getDatefin() {
        return datefin;
    }

    /**
     * @param datefin the datefin to set
     */
    public void setDatefin(Timestamp datefin) {
        this.datefin = datefin;
    }
    
    private String statut;
    private String refproduit;
    private String refcommande;
    private String numserie;
    private Timestamp datedebut;
    private Timestamp datefin;
    
    public Exemplaire(String refcommande,String refproduit,String numserie,Timestamp datedebut){
        this.refcommande=refcommande;
        this.refproduit=refproduit;
        this.numserie=numserie;
        this.datedebut=datedebut;
        this.statut ="En cours";
    }
    
    public void saveexemplaire(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "insert into exemplaire (statut,refcommande,refproduit,numserie,datedebut,datefin) values (?,?,?,?,?,?)")) {
            pst.setString(1, getStatut());
            pst.setString(2, this.getRefcommande());
            pst.setString(3, this.getRefproduit());
            pst.setString(4, this.getNumserie());
            pst.setTimestamp(5, this.getDatedebut());
            pst.setTimestamp(6, this.getDatefin());
            pst.executeUpdate();
        }
    }
    
   public String formulationdatedebut() {
       return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(this.datedebut);
   }
   
   public String formulationdatefin() {
       return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(this.datefin);
   }
   
   public static String formulationdate(Timestamp date){
       return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(date);
   }
   
   public void GammeFab(Connection con) throws SQLException{
       List<String[]> operations=Produit.getproduitbyRef(con, refproduit).Opsassociees(con);
       List<Phasefabrication> res=new ArrayList();
       if(!operations.isEmpty()){
            int idop;
            Long DureeTotale=0l;
            String[]rowinit=operations.get(0);
            int iddebutfab=Integer.parseInt(rowinit[0]);
            String refmachine=rowinit[2];
            int idoperateur=Phasefabrication.OperateurDisponible(con, rowinit[2]).getIdoperateur();
            Timestamp debutoperation=this.datedebut;
            Timestamp finoperation=Timestamp.valueOf(this.datedebut.toLocalDateTime().plusMinutes((long)Double.parseDouble(rowinit[3])));
            res.add(new Phasefabrication(iddebutfab,refmachine,idoperateur,this.numserie,debutoperation,finoperation));
            debutoperation=finoperation;
            DureeTotale=DureeTotale+(long)Double.parseDouble(rowinit[3]);
            for(String[] ligne : operations){
                if(ligne[0]==null){
                    break;
                }else{
                    idop=Integer.parseInt(ligne[4]);
                    refmachine=ligne[6];
                    idoperateur=Phasefabrication.OperateurDisponible(con, refmachine).getIdoperateur();
                    finoperation=Timestamp.valueOf(debutoperation.toLocalDateTime().plusMinutes((long)Double.parseDouble(ligne[7])));
                    res.add(new Phasefabrication(idop,refmachine,idoperateur,this.numserie,debutoperation,finoperation));
                    DureeTotale=DureeTotale+(long)Double.parseDouble(ligne[7]);
                    debutoperation=finoperation;
                    }
                }
            this.setDatefin(Timestamp.valueOf(this.datedebut.toLocalDateTime().plusMinutes(DureeTotale)));
            this.saveexemplaire(con);
            for(Phasefabrication phase : res){
                phase.savePhasefabrication(con);
            }
        }
   }
   
   public static List<Exemplaire> produitsdansperiode(Connection con, Timestamp debut,Timestamp fin) throws SQLException{
       List<Exemplaire> res=new ArrayList();
       try (PreparedStatement pst = con.prepareStatement(
                "SELECT *\n" +
                "FROM exemplaire\n" +
                "WHERE exemplaire.datedebut > ? AND exemplaire.datefin < ?;\n" +
                "")){
            pst.setTimestamp(1, debut);
            pst.setTimestamp(2, fin);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Exemplaire temp=new Exemplaire(rs.getString("refcommande"),rs.getString("refproduit"),rs.getString("numserie"),rs.getTimestamp("datedebut"));
                    temp.setStatut(rs.getString("statut"));
                    temp.setDatefin(rs.getTimestamp("datefin"));
                    res.add(temp);
                }
            }
       }
       return res;
   }

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
}