package controller;

import container.RigaStatistiche;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * Classe per la visualizzazione delle statistiche, sia per utente Standard che Admin.
 * Attributi utilizzati per la tipologia Standard: colAnno,colNomeFilm,colVoto,colNumRec.
 * Attributi utilizzati per la tipologia Admin: colUtente,colTotNumRec,colNumRecPos,colNumRecNeg,colMediaRec.
 * 
 * @author Davide
 *
 */
public class MostraStatisticheController {

	 @FXML
	 private Label lblStringaDesQuery;
	 
	 // Colonne interfaccia utente standard
	 @FXML
	 private TableColumn<?, ?> colAnno;
	 
	 @FXML
	 private TableColumn<?, ?> colNomeFilm;
	 
	 @FXML
	 private TableColumn<?, ?> colVoto;
	
	 @FXML
	 private TableColumn<?, ?> colNumRec;
	 
	 
	 // Colonne interfaccia utente admin

	 @FXML
	 private TableColumn<?, ?> colUtente;
	 
	 @FXML
	 private TableColumn<?, ?> colTotNumRec;
	 
	 @FXML
	 private TableColumn<?, ?> colNumRecPos;
	 
	 @FXML
	 private TableColumn<?, ?> colNumRecNeg;
	 
	 @FXML
	 private TableColumn<?, ?> colMediaRec;
	 
	 
	 @FXML
	 private TableView<RigaStatistiche> tabellaStat;
	 private final ObservableList<RigaStatistiche> listaStatistiche=FXCollections.observableArrayList();
	 
	 public MostraStatisticheController() {
	 }
	 
	 @FXML
	 public void initialize() {
		 
		 // Colonne interfaccia utente standard
		 if(colAnno!=null) colAnno.setCellValueFactory(new PropertyValueFactory<>("anno"));
		 if(colNomeFilm!=null) colNomeFilm.setCellValueFactory(new PropertyValueFactory<>("nomeFilm"));
		 if(colVoto!=null)colVoto.setCellValueFactory(new PropertyValueFactory<>("mediaVoto"));
		 if(colNumRec!=null) colNumRec.setCellValueFactory(new PropertyValueFactory<>("numeroRecensioni"));
		 
		 // Colonne interfaccia utente admin
		 if(colUtente!=null) colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
		 if(colTotNumRec!=null) colTotNumRec.setCellValueFactory(new PropertyValueFactory<>("numeroRecensioni"));
		 if(colNumRecPos!=null) colNumRecPos.setCellValueFactory(new PropertyValueFactory<>("numRecPositive"));
		 if(colNumRecNeg!=null) colNumRecNeg.setCellValueFactory(new PropertyValueFactory<>("numRecNegative"));
		 if(colMediaRec!=null) colMediaRec.setCellValueFactory(new PropertyValueFactory<>("mediaVoto"));
		 
		 tabellaStat.setItems(listaStatistiche);
	 }
	 
	  

	public ObservableList<RigaStatistiche> getListaStatistiche() {
		return listaStatistiche;
	}

	public Label getLblStringaDesQuery() {
		return lblStringaDesQuery;
	}

	public void setLblStringaDesQuery(String testo) {
		this.lblStringaDesQuery.setText(testo);
	}	 
	 
}
