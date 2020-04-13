package controller;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.bson.Document;
import org.bson.types.ObjectId;


import application.GestoreRisorse;
import container.RigaFilm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Film;

/**
 * Classe che gestisce l'inserimento e la modifica di un Film.
 * @author Davide Vigna
 */
public class ModificaFilmController {
	
	
	 @FXML
	 private Label lblTitoloOp;
	 
	 private ObjectId idFilm;
	 
	 @FXML
	 private TextField titoloTF;
	 
	 @FXML
	 private TextField annoTF;
	 
	 @FXML
	 private ComboBox<String> comboGenere;
	 
	 @FXML
	 private TextField paesiProdTF;
	 
	 @FXML
	 private TextField durataTF;
	 
	 @FXML
	 private TextArea tramaCompletaTA;
	 
	 @FXML
	 private DatePicker dataUscitaDP;
	 
	 @FXML
	 private Button btnInserisciModifica;
	 
	 private final ObservableList<String> listaGeneri=FXCollections.observableArrayList();
	 private final ObservableList<String> listaPaesi=FXCollections.observableArrayList();

	 /** 
	  * Metodo per l'impostazione dei valori all'interno della Combobox Generi.
	  */
	 private  void initComboGeneri(){
			listaGeneri.add("");
			listaGeneri.add("Commedia");
			listaGeneri.add("Poliziesco");
			listaGeneri.add("Sentimentale");
			listaGeneri.add("Animazione");
			listaGeneri.add("Avventura");
			listaGeneri.add("Drammatico");
			listaGeneri.add("Fantastico");
			listaGeneri.add("Fantasy");
			listaGeneri.add("Thriller");
			listaGeneri.add("Western");
			listaGeneri.add("Fantascienza");
			listaGeneri.add("Giallo");
			listaGeneri.add("Biografico");
			listaGeneri.add("Azione");
			listaGeneri.add("Horror");
			listaGeneri.add("Documentario");
	}

	 @FXML
	 public void initialize() {
	
		 initComboGeneri();
		 comboGenere.getItems().setAll(listaGeneri);
		 
		 btnInserisciModifica.setOnAction((ActionEvent ev) -> {
				inserisciModificaFilm();
		 });
	 }


	/** 
	 *  Metodo per il salvataggio di un film.
	 *  Al termine viene chiusa l'interfaccia corrente.
	 */
	public void inserisciModificaFilm() {
		
		Document d= new Document();
		if ("".equals(titoloTF.getText().trim())){
			System.err.println("Titolo mancante");
			return;
		}
		d.put("nome",titoloTF.getText());
		try {
			int anno=Integer.parseInt(annoTF.getText());
			d.put("anno",anno);
		}
		catch(Exception e) {
			System.err.println(" Anno assente oppure conversione anno errata!");
		}
		
		d.put("tramaCompleta",tramaCompletaTA.getText());
		
		if (comboGenere.getSelectionModel().getSelectedItem()!=null && !comboGenere.getSelectionModel().getSelectedItem().equals("")) {
			d.put("genere",Arrays.asList(comboGenere.getSelectionModel().getSelectedItem()));
		}
		else {
			System.err.println("Genere mancante");
			return;
		}
		
		String paesi=paesiProdTF.getText();
		String[] splittati = paesi.split(",");
		if (splittati!=null && splittati.length>0) {
			d.put("paesi_prod", Arrays.asList(splittati));
		}
		if (durataTF.getText()!=null) {
			try {
				int durata=Integer.parseInt(durataTF.getText());
				d.put("durata_min",durata);
			}
			catch(Exception e) {
				
				System.err.println(" Durata assente oppure conversione durata errata!");
			}
		}

		if(dataUscitaDP.getValue()!=null) {
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Date date = Date.from(dataUscitaDP.getValue().atStartOfDay(defaultZoneId).toInstant());
			d.put("data_uscita", date);
		};
		
		
		if (idFilm==null) {
			//Insert
			GestoreRisorse.getDatabase().getCollection("film").insertOne(d);
		}
		else {
			//Update
			d.put("_id", idFilm);
			
			GestoreRisorse.aggiornaFilm(idFilm, d);
		}
		Film film= new Film(d);
		RicercaController.aggiornaRigaVideo(new RigaFilm(film));
		Stage stage = (Stage) btnInserisciModifica.getScene().getWindow();
		stage.close();
	}
	 
	
	
	
	public ObjectId getIdFilm() {
		return idFilm;
	}

	public void setIdFilm(ObjectId idFilm) {
		this.idFilm = idFilm;
	}

	public Label getLblTitoloOp() {
		return lblTitoloOp;
	}


	public void setLblTitoloOp(String lblTitoloOp) {
		this.lblTitoloOp.setText(lblTitoloOp);
	}


	public void setBtnInserisciModifica(String btnInserisciModifica) {
		this.btnInserisciModifica.setText(btnInserisciModifica);
	}


	public TextField getTitoloTF() {
		return titoloTF;
	}


	public void setTitoloTF(TextField titoloTF) {
		this.titoloTF = titoloTF;
	}


	public TextField getAnnoTF() {
		return annoTF;
	}


	public void setAnnoTF(TextField annoTF) {
		this.annoTF = annoTF;
	}


	public ComboBox<String> getComboGenere() {
		return comboGenere;
	}


	public void setComboGenere(ComboBox<String> comboGenere) {
		this.comboGenere = comboGenere;
	}

	public TextField getDurataTF() {
		return durataTF;
	}


	public void setDurataTF(TextField durataTF) {
		this.durataTF = durataTF;
	}


	public TextArea getTramaCompletaTA() {
		return tramaCompletaTA;
	}


	public void setTramaCompletaTA(TextArea tramaCompletaTA) {
		this.tramaCompletaTA = tramaCompletaTA;
	}


	public Button getBtnInserisciModifica() {
		return btnInserisciModifica;
	}


	public void setBtnInserisciModifica(Button btnInserisciModifica) {
		this.btnInserisciModifica = btnInserisciModifica;
	}


	public ObservableList<String> getListaGeneri() {
		return listaGeneri;
	}


	public ObservableList<String> getListaPaesi() {
		return listaPaesi;
	}


	public void setLblTitoloOp(Label lblTitoloOp) {
		this.lblTitoloOp = lblTitoloOp;
	}

	/**
	 * Metodo per l'impostazione dei Paesi Produzione come formato stringa nella textfield.
	 */
	public void setPaesiProdTF() {
		String elencoPaesi="";
		for (Iterator<String> iterator = listaPaesi.iterator(); iterator.hasNext();) {
			String paese = (String) iterator.next();
			elencoPaesi+=paese;
			if (iterator.hasNext()) elencoPaesi+=",";
		}
		this.paesiProdTF.setText(elencoPaesi);
	}
	 
	public TextField getPaesiProdTF() {
		return paesiProdTF;
	}


	public void setPaesiProdTF(TextField paesiProdTF) {
		this.paesiProdTF = paesiProdTF;
	}


	public DatePicker getDataUscitaDP() {
		return dataUscitaDP;
	}


	public void setDataUscitaDP(DatePicker dataUscitaDP) {
		this.dataUscitaDP = dataUscitaDP;
	}
		
}
