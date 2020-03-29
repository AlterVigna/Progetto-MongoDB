package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Film;


/**
 * Classe per la mostra dettaglio di un film.
 * Vengono visualizzate una serie di informazioni aggiuntive  non presenti nella tabella di Ricerca.
 * Queste sono Paesi produzione, Numero delle recensioni associate, media Voto, la trama completa e il cast delle persone che hanno preso parte al film.
 * @author Davide Vigna
 *
 */
public class MostraFilmDettaglioController {
	
	 @FXML
	 private Label lblTitolo;
	 
	 @FXML
	 private Label lblGenere;
	 
	 @FXML
	 private Label lblDataDiUscita;
	 
	 @FXML
	 private Label lblDurata;
	 
	 @FXML
	 private Label lblPaesiProduzione;
	 
	 @FXML
	 private Label lblNumRec;
	 
	 @FXML
	 private Label lblMediaVoto;
	 
	 @FXML
	 private TextArea textTramaCompleta;
	 
	 @FXML
	 private TableView<Film.Cast> tabellaCast;
	 
	 @FXML
	 private TableColumn<?, ?> colNome;
	 
	 @FXML
	 private TableColumn<?, ?> colRuolo;
	 
	 private final ObservableList<Film.Cast> listaCast=FXCollections.observableArrayList();
	 
	 public MostraFilmDettaglioController() {
		 
	 }
	 
	 @FXML
	 public void initialize() {
		 colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		 colRuolo.setCellValueFactory(new PropertyValueFactory<>("ruolo"));
		 tabellaCast.setItems(listaCast);
	 }
	  
	public ObservableList<Film.Cast> getListaCast() {
		return listaCast;
	}
	 
	public void setLblTitolo(String stringTitle) {
		this.lblTitolo.setText(stringTitle);
	}

	public void setLblGenere(String stringGenere) {
		this.lblGenere.setText(stringGenere); 
	}

	public void setLblDataDiUscita(String stringDataUscita) {
		this.lblDataDiUscita.setText(stringDataUscita);
	}

	public void setLblDurata(String stringDurata) {
		this.lblDurata.setText(stringDurata);
	}

	public void setLblPaesiProduzione(String stringPaesiProduzione) {
		this.lblPaesiProduzione.setText(stringPaesiProduzione);
	}

	public void setTextTramaCompleta(String textTramaCompleta) {
		this.textTramaCompleta.setText(textTramaCompleta);
	}

	public void setTabellaCast(TableView<Film.Cast> tabellaCast) {
		this.tabellaCast = tabellaCast;
	}

	public void setLblNumRec(String lblNumRec) {
		this.lblNumRec.setText(lblNumRec);
	}
	
	public void setLblMediaVoto(String lblMediaVoto) {
		this.lblMediaVoto.setText(lblMediaVoto);
	}
	
}
