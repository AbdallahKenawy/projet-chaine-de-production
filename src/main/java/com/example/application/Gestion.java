/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Gestion 
{
  private Connection conn;
  
  public Gestion(Connection conn){
      this.conn= conn;
  }
  
      /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }
  
 public static Connection connectGeneralMySQL(String host,
            int port, String database,
            String user, String pass)
            throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    } 
 
  public static Connection connectSurServeurM3() throws SQLException {
        return connectGeneralMySQL("92.222.25.165", 3306,
                "m3_akenawi01", "m3_akenawi01",
                "169e894b");
    }
  
  public void creeSchema() throws SQLException {
      this.getConn().setAutoCommit(false);
        try (Statement st = this.getConn().createStatement()) {
            st.executeUpdate(
                    "create table machine (\n"
                    + "    idmachine integer not null primary key AUTO_INCREMENT,\n"
                    + "    refmachine varchar(50) not null unique,\n"
                    + "    desmachine text not null,\n"
                    + "    puissance double not null,\n"
                    + "    idposte integer not null,\n"
                    + "    statut varchar(50) not null\n"       
                    + ")\n"
            );
            st.executeUpdate(
                    "create table typeoperation (\n"
                    + "    idtype integer not null primary key AUTO_INCREMENT,\n"
                    + "    destype text not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "create table realise (\n"
                    + "    idmachine integer not null,\n"
                    + "    idtype integer not null,\n"
                    + "    duree float not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table realise \n"
                    +"  add constraint fk_realise_idmachine \n"
                    + "    foreign key (idmachine) references machine(idmachine) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table realise \n"
                    + "    add constraint fk_realise_idtype \n"
                    + "    foreign key (idtype) references typeoperation(idtype) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(    
                    "create table produits (\n"
                    + "    idproduit integer not null primary key AUTO_INCREMENT,\n"
                    + "    refproduit varchar(50) not null unique,\n"
                    + "    desproduit text not null,\n"
                    + "    stock float not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "create table touteslesoperations (\n"
                    + "    idtoutesop integer not null primary key AUTO_INCREMENT,\n"
                    + "    idtype integer not null,\n"
                    + "    idproduit integer not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table touteslesoperations \n"
                    +"  add constraint fk_touteslesop_idtype \n"
                    + "    foreign key (idtype) references typeoperation(idtype) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table touteslesoperations \n"
                    + "    add constraint fk_touteslesop_idproduit \n"
                    + "    foreign key (idproduit) references produits(idproduit) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table precedenceoperations (\n"
                    + "    opavant integer not null,\n"
                    + "    opapres integer not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table precedenceoperations \n"
                    +"  add constraint fk_precedenceoperations_opavant \n"
                    + "    foreign key (opavant) references touteslesoperations(idtoutesop) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table precedenceoperations \n"
                    + "    add constraint fk_precedenceoperations_opapres \n"
                    + "    foreign key (opapres) references touteslesoperations(idtoutesop) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table operateur (\n"
                    + "    idoperateur integer not null primary key AUTO_INCREMENT,\n"
                    + "    nom varchar(50) not null,\n"
                    + "    prenom varchar(50) not null,\n"
                    + "    statut varchar(50) not null,\n"        
                    + "    identifiant varchar(50) not null unique,\n"
                    + "    motdepasse varchar(50) not null \n"    
                    + ")\n"
            );
            st.executeUpdate(
                    "create table poste (\n"
                    + "    idposte integer not null primary key AUTO_INCREMENT\n"     
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table machine \n"
                    + "    add constraint fk_machine_idposte \n"
                    + "    foreign key (idposte) references poste(idposte) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table commande (\n"
                    + "    idcommande integer not null primary key AUTO_INCREMENT,\n"
                    + "    idutilisateur integer not null,\n"
                    + "    refcommande varchar(50) not null unique,\n"
                    + "    datecommande timestamp not null\n"
                    + ")\n"
            );
            
            st.executeUpdate(
                    "create table exemplaire (\n"
                    + "    statut varchar(50) not null,\n"
                    + "    refcommande varchar(50) not null,\n"
                    + "    refproduit varchar(50) not null,\n"
                    + "    numserie varchar(50) not null unique,\n"
                    + "    datedebut timestamp not null,\n"
                    + "    datefin timestamp not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table exemplaire \n"
                    +"  add constraint fk_exemplaire_refcommande \n"
                    + "    foreign key (refcommande) references commande(refcommande) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table exemplaire \n"
                    +"  add constraint fk_exemplaire_refproduit \n"
                    + "    foreign key (refproduit) references produits(refproduit) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table habilitation (\n"
                    + "    idoperateur Integer not null,\n"
                    + "    idmachine Integer not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table habilitation \n"
                    +"  add constraint fk_habilitation_idoperateur \n"
                    + "    foreign key (idoperateur) references operateur(idoperateur) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table habilitation \n"
                    +"  add constraint fk_habilitation_idmachine \n"
                    + "    foreign key (idmachine) references machine(idmachine) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table utilisateur (\n"
                    + "     idutilisateur integer not null primary key AUTO_INCREMENT,\n"
                    + "     nom varchar(50) not null, \n"
                    + "     prenom varchar(50) not null, \n"
                    + "     identifiant varchar(50) not null unique, \n"
                    + "     motdepasse varchar(50) not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table commande \n"
                    +"  add constraint fk_commande_idutilisateur \n"
                    + "    foreign key (idutilisateur) references utilisateur(idutilisateur) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "create table phasefabrication (\n"
                    + "     idtoutesop integer not null,\n"
                    + "     refmachine varchar(50) not null, \n"
                    + "     idoperateur integer not null, \n"
                    + "     numserie varchar(50) not null, \n"
                    + "     tempsdebut timestamp not null,\n"
                    + "     tempsfin timestamp not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table phasefabrication \n"
                    +"  add constraint fk_phasefabrication_idtoutesop \n"
                    + "    foreign key (idtoutesop) references touteslesoperations(idtoutesop) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table phasefabrication \n"
                    +"  add constraint fk_phasefabrication_refmachine \n"
                    + "    foreign key (refmachine) references machine(refmachine) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table phasefabrication \n"
                    +"  add constraint fk_phasefabrication_idoperateur \n"
                    + "    foreign key (idoperateur) references operateur(idoperateur) \n"
                    + "    ON DELETE CASCADE \n"
            );
            st.executeUpdate(
                    "alter table phasefabrication \n"
                    +"  add constraint fk_phasefabrication_numserie \n"
                    + "    foreign key (numserie) references exemplaire(numserie) \n"
                    + "    ON DELETE CASCADE \n"
            );

            this.getConn().commit();
        } catch (SQLException ex) {
            this.getConn().rollback();
            throw ex;
        } finally {
            this.getConn().setAutoCommit(true);
        }
    }
    
    public void deleteSchema() throws SQLException {
        try (Statement st = this.getConn().createStatement()) {
            try {
                st.executeUpdate("alter table commande drop constraint fk_commande_idutilisateur");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table precedenceoperations drop constraint fk_precedenceoperations_opapres");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table precedenceoperations drop constraint fk_precedenceoperations_opavant");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table touteslesoperations drop constraint fk_touteslesop_idproduit");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table touteslesoperations drop constraint fk_touteslesop_idtype");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table realise drop constraint fk_realise_idtype");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table realise drop constraint fk_realise_idmachine");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table exemplaire drop constraint fk_exemplaire_refcommande");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table exemplaire drop constraint fk_exemplaire_refproduit");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table machine drop constraint fk_machine_idposte");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table habilitation drop constraint fk_habilitation_idmachine");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table habilitation drop constraint fk_habilitation_idoperateur");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table phasefabrication drop constraint fk_phasefabrication_idtoutesop");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table phasefabrication drop constraint fk_phasefabrication_refmachine");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table phasefabrication drop constraint fk_phasefabrication_idoperateur");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table phasefabrication drop constraint fk_phasefabrication_numserie");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table phasefabrication");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table utilisateur");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table habilitation");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table poste");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table commande");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table exemplaire");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table operateur");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("drop table precedenceoperations");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table touteslesoperations");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table produits");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table realise");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table typeoperation");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table machine");
            } catch (SQLException ex) {
            }
        }
    }
    
    public void initSchema() throws SQLException {
        
        new Poste(-1).savePosteinit(this.getConn());
        new Utilisateur("auth","auth","auth","auth").saveUtilisateur(this.getConn());
        
        new Machine("F01","rapide",20).savemachine(this.getConn());
        new Machine("F02","lente",10).savemachine(this.getConn());
        
        new Typeoperation("fraisage").savetypeoperation(this.getConn());
        new Typeoperation("polissage").savetypeoperation(this.getConn());
        
        new Realise(1,1,20).saverealise(this.getConn());
        new Realise(1,2,30).saverealise(this.getConn());
        new Realise(2,1,5).saverealise(this.getConn());
        
        new Produit("T23","tel").saveproduit(this.getConn());
        new Produit("P12","ordi").saveproduit(this.getConn());
        new Produit("T03","table").saveproduit(this.getConn());
        
        new touteslesoperations(1,1).saveoperation(this.getConn());
        new touteslesoperations(1,1).saveoperation(this.getConn());
        new touteslesoperations(2,1).saveoperation(this.getConn());
        new touteslesoperations(2,2).saveoperation(this.getConn());
        new touteslesoperations(1,2).saveoperation(this.getConn());
        new touteslesoperations(1,2).saveoperation(this.getConn());
        new touteslesoperations(2,2).saveoperation(this.getConn());
        new touteslesoperations(2,3).saveoperation(this.getConn());
        
        new precedenceoperations(1,3).saveprecedence(this.getConn());
        new precedenceoperations(2,3).saveprecedence(this.getConn());
        new precedenceoperations(4,5).saveprecedence(this.getConn());
        new precedenceoperations(4,6).saveprecedence(this.getConn());
        new precedenceoperations(5,7).saveprecedence(this.getConn());
        new precedenceoperations(6,7).saveprecedence(this.getConn());
    }
    
    public void resetBDD() throws SQLException{
        this.deleteSchema();
        this.creeSchema();
        new Poste(-1).savePosteinit(this.getConn());
        new Utilisateur("auth","auth","auth","auth").saveUtilisateur(this.getConn());
    }
    
  
  public void menuPrincipal() throws SQLException{
      int rep= -1;
      while(rep !=0){
           int i=1;
           System.out.println("Menu");
           System.out.println((i++) + ") Creer Schema");
           System.out.println((i++) + ") Supprimer schema ");
           System.out.println((i++) + ") RAZ schema ");        
           System.out.println("0) Fin");
           rep = Lire.i();
           try {
               int s=1;
               if(rep==s++){
                   this.creeSchema();
               } else if (rep==s++){
                   this.deleteSchema();
               } else if (rep==s++){
                   this.resetBDD();
                }
               } catch (SQLException ex){
               ex.printStackTrace();
           }

      }
      
  }
  
  public static void debut (){
      try{
          Connection con = connectSurServeurM3();
          Gestion gestionnaire =new Gestion (con);
          gestionnaire.menuPrincipal();
      } catch (SQLException ex){
          throw new Error (ex);
      }
    }
  
    public static void main(String[] args) {
        debut();
    }
}
