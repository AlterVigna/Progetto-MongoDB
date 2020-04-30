package controller;

import org.bson.Document;
import org.bson.types.ObjectId;

import application.GestoreRisorse;
import container.RigaRecensioni;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Recensione;

/**
 * Classe per la gestione inserimento/modifica di una recensione.
 * @author Davide Vigna
 *
 */
public class ModificaRecensioneController {
	
	
	 @FXML
	 private Spinner<Double> spinnerVoto;
	
	 @FXML
	 private TextArea textCommento;
	 
	 @FXML
	 private Button btnAggiungiMod;
	 
	 private int modalita;
	 private ObjectId idFilm;
	 private ObjectId idRecensione;
	 
	 public ModificaRecensioneController() {
	 }
	 
	 @FXML
	 public void initialize() {
		 
		this.spinnerVoto.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0,0.0,0.5));
		 
		this.spinnerVoto.focusedProperty().addListener((s, ov, nv) -> {
			if (nv) return;
			commitEditorText(spinnerVoto);
		});
		 
		btnAggiungiMod.setOnAction((ActionEvent ev) -> {
			 	aggiungiModificaRecensione();
		});
		 
	 }
	 
	/** 
	 * Imposto i valori  della recensione passata come parametro negli elementi grafici.
	 * @param r Recensione da impostare.
	 */
	public void impostaRecensione(Recensione r) {
			idRecensione=r.getIdRecensione();
			idFilm=r.getIdFilm();
			textCommento.setText(r.getRecensione());
			spinnerVoto.getValueFactory().setValue(r.getVoto());
	}
		 
	/**
	 * Metodo che implementa il salvataggio di una nuova o già esistente Recensione.
	 * E' necessario che l'utente inserisca il voto e il commento per procedere correttamente al salvataggio.
	 */
	public void aggiungiModificaRecensione() {
			
		 if (textCommento.getText().trim()!="" && spinnerVoto.getValue()!=null) {
			 
			 Document d= new Document();
			 if (modalita==GestoreRisorse.AGGIUNGI_RECENSIONE) {
				
				 d.put("voto",spinnerVoto.getValue());
				 d.put("commento", textCommento.getText());
				 d.put("id_utente",GestoreRisorse.getUtenteCorrente().getIdUtente());
				 d.put("username",GestoreRisorse.getUtenteCorrente().getUsername());
				 d.put("id_film",idFilm);
				 
				 GestoreRisorse.inserisciRecensione(d);
			 }
			
			 if (modalita==GestoreRisorse.MODIFICA_RECENSIONE) {
				 
				 d.put("_id",idRecensione);	 
				 
				 Document doc= new Document();
				 doc.put("voto",spinnerVoto.getValue());
				 doc.put("commento",textCommento.getText());
				 

				 GestoreRisorse.aggiornaRecensione(idRecensione,d,idFilm);
			 }

			if (modalita==GestoreRisorse.MODIFICA_RECENSIONE){
				Recensione rec=new Recensione();
				rec.setIdRecensione(idRecensione);
				rec.setVoto(spinnerVoto.getValue());
				rec.setRecensione(textCommento.getText());
				
				MostraRecensioniController.aggiornaRigaAVideo(new RigaRecensioni(rec));
			}
			
			System.out.println("Recensione Inserita/Modificata Con successo");
			Stage stage = (Stage) btnAggiungiMod.getScene().getWindow();
			stage.close();
		 }
	}
		
	
	 
	/**
	 * Metodo per il settaggio dello spinner manuale, cioè quando l'utente inserisce il voto da tastiera invece che cliccando sulle frecce.
	 * @param <T> elemento generico
	 * @param spinner L'elemento che si va a modificare
	 */
	private <T> void commitEditorText(Spinner<Double> spinner) {
		    if (!spinner.isEditable()) return;
		    String text = spinner.getEditor().getText();
		    SpinnerValueFactory<Double> valueFactory = spinner.getValueFactory();
		    if (valueFactory != null) {
		        StringConverter<Double> converter = valueFactory.getConverter();
		        if (converter != null) {
		        	try {
		        		 Double value = converter.fromString(text);
		        		 if (value==null) valueFactory.setValue(0.0);
		        		 else valueFactory.setValue(value);
		        	}
		        	catch (Exception e) {
						System.err.println("Errore conversione voto..");
						valueFactory.setValue(0.0);
					}
		        }
		    }
	}

	
	// Getter&Setter
	public ObjectId getIdFilm() {
		return idFilm;
	}

	public void setIdFilm(ObjectId idFilm) {
		this.idFilm = idFilm;
	}

	public Spinner<Double> getSpinnerVoto() {
		return spinnerVoto;
	}

	public void setSpinnerVoto(Spinner<Double> spinnerVoto) {
		this.spinnerVoto = spinnerVoto;
	}

	public TextArea getTextCommento() {
		return textCommento;
	}

	public void setTextCommento(TextArea textCommento) {
		this.textCommento = textCommento;
	}

	public Button getBtnAggiungiMod() {
		return btnAggiungiMod;
	}

	public void setBtnAggiungiMod(Button btnAggiungiMod) {
		this.btnAggiungiMod = btnAggiungiMod;
	}
	
	public int getModalita() {
		return modalita;
	}

	public void setModalita(int modalita) {
		this.modalita = modalita;
		
		 if (modalita==GestoreRisorse.AGGIUNGI_RECENSIONE) {
			 btnAggiungiMod.setText("Aggiungi");
			
		 }
		 if (modalita==GestoreRisorse.MODIFICA_RECENSIONE) {
			 btnAggiungiMod.setText("Modifica");
		 }
	}
	
	public void setVoto (Double voto) {
		this.spinnerVoto.getValueFactory().setValue(voto); 
	}
	
}
