package controller;

import java.util.List;

import application.GestoreRisorse;
import container.RigaFilm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Film;
import model.Utente;

/**
 * Classe per la gestione dell'autenticazione di un utente.
 * A seconda della tipologia di utente inserito (Standard o Admin), viene mostrata un interfaccia di ricerca diversa.
 * Viene effettuato un controllo sulle credenziali utente inserite (Username, Password)
 * 
 * @author Davide Vigna
 */
public class AutenticazioneController {
	
	 @FXML
	 private TextField usernameTF;
	 
	 @FXML
	 private PasswordField passwordTF;
	 
	 @FXML
     private Button btnAccedi;
	 
	 @FXML
	 private Label lblCredErrate;
	 
	 
	 public AutenticazioneController() {
	 }
	 
	 @FXML
	 public void initialize() {
		 lblCredErrate.setVisible(false);
		 btnAccedi.setOnAction((ActionEvent ev)->{
			 gestisciAccesso();
		 });
		 GestoreRisorse.initConnection();
	 }
	 
	 /**
	  * Implementa la fase di autenticazione. 
	  * In caso di successo mostra l'interfaccia di ricerca.
	  * In caso di fallimento mostra messaggio di errore.
	  */
	 private void gestisciAccesso() {
		 GestoreRisorse.setUtenteCorrente(null);
		 lblCredErrate.setVisible(false);
		 if (!usernameTF.getText().trim().equals("") && !passwordTF.getText().trim().equals("")) {
			Utente utente = GestoreRisorse.ricercaUtente(usernameTF.getText(), passwordTF.getText());
			if (utente!=null) {
				 Stage stage = (Stage) btnAccedi.getScene().getWindow();
				 stage.close();
				 GestoreRisorse.setUtenteCorrente(utente);
				 apriInterfacciaRicerca(stage);
			}
			else lblCredErrate.setVisible(true);
		 }
	 }
	 
	 
	 /**
	  * Implementa l'apertura di una nuova interfaccia di ricerca.
	  * 
	  * @param primaryStage -  lo stage che viene modificato
	  */
	 
	 private void apriInterfacciaRicerca(Stage primaryStage) {
		 try {
			 	FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("/views/RicercaView.fxml"));
				Parent root = (Parent) fxmlLoader.load();  
				RicercaController controller = fxmlLoader.<RicercaController>getController();
				
				controller.setLblUsername(usernameTF.getText());
				controller.setNumDocumentiTrovati(GestoreRisorse.NUM_FILM_CARICAMENTO_INIZIALE);
				controller.setLabelNumeroFilmTrovati();
				
				RicercaController.getListafilmcompleta().clear();
				RicercaController.getListafilmavideo().clear();
				
				List<Film> listaFilm = GestoreRisorse.caricaUltimiNFilm(GestoreRisorse.NUM_FILM_CARICAMENTO_INIZIALE);
				List<RigaFilm> listFilm = RigaFilm.ottieniListaRighe(listaFilm);
				
				RicercaController.getListafilmcompleta().addAll(GestoreRisorse.caricaUltimiNFilm(GestoreRisorse.NUM_FILM_CARICAMENTO_INIZIALE));
				RicercaController.getListafilmavideo().addAll(listFilm);
					
				if (GestoreRisorse.getUtenteCorrente().getRuolo().equals(GestoreRisorse.RUOLO_STANDARD)) {

					controller.setLblStat1("Film maggiormente recensiti");
					controller.setLblStat2("Migliori film per anno");
					controller.setBtnStat1("Most reviewed");
					controller.setBtnStat2("Best by Year");
					
					controller.getBtnStat1().setOnAction((ActionEvent ev) -> {
						RicercaController.apriInterfacciaStatisticheFilmPiuRecensiti();
					});
					 
					controller.getBtnStat2().setOnAction((ActionEvent ev) -> {
						RicercaController.apriInterfacciaStatisticheMiglioriPerAnno();
					});
					controller.getBtnAggiungi().setVisible(false);
				}
				else {
					controller.setLblStat1("Dettaglio recensioni per utente");
					controller.setBtnStat1("Detail Reviews");
					 
					controller.getLblStat1().setVisible(true);
					controller.getBtnStat1().setVisible(true);
					controller.getLblStat2().setVisible(false);
					controller.getBtnStat2().setVisible(false);
					
					controller.getBtnStat1().setOnAction((ActionEvent ev) -> {
						RicercaController.apriInterfacciaStatisticheDettaglioRecensioniPerUtente();
					});
					controller.getBtnAggiungi().setVisible(true);
				}
				Scene scene = new Scene(root,1350,760);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Let's Movie - Applicazione" );
				primaryStage.show();
				
				controller.initScrollTable();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
	 }	 
}
