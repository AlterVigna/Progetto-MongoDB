package container;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.GestoreRisorse;
import controller.RicercaController;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import model.Film;

/**
 * Classe per la rappresentazione di una riga nella Tabella dei film.
 * E' composta da 2 elementi. Il model che contiene tutte le informazioni da mostrare a video del film.
 * Un elemento Hbox che contiene le opzioni che si possono fare su quello specifico elemento mostrato a video.
 * 
 * @author Davide Vigna
 *
 */
public class RigaFilm {
	
	private Film model;
	private HBox opzioni= new HBox();
	
	public RigaFilm(Film model) {
		super();
		this.model = model;
		
		opzioni.getChildren().clear();
		
		List<Button> listaPulsanti= new ArrayList<Button>();
		
		if (GestoreRisorse.getUtenteCorrente().getRuolo().equals(GestoreRisorse.RUOLO_STANDARD)) {
			
			Button btnInfo= new Button("Info");
			Button btnAdd= new Button("Aggiungi");
			Button btnShow= new Button("Mostra");
		
			btnInfo.setTooltip(new Tooltip("Dettaglio Film"));
			btnInfo.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaDettaglioFilm(this.model.getId());
			});
		
			btnAdd.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaDettaglioRecensione(null,this.model.getId(),this.model.getNomeFilm(),GestoreRisorse.AGGIUNGI_RECENSIONE);
			});
			
			btnShow.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaMostraTutteLeRecensioniDiFilm(this.model);
			});
				
			listaPulsanti.add(btnInfo);
			listaPulsanti.add(btnAdd);
			listaPulsanti.add(btnShow);
		}
		else {
			
			Button btnShow= new Button("Mostra");
			btnShow.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaMostraTutteLeRecensioniDiFilm(this.model);
			});
			
			Button btnModifica=new Button("Modifica");
			btnModifica.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaInserisciFilm(this.model.getId());
			});
			
			Button btnElimina=new Button("Elimina");
			btnElimina.setOnAction((ActionEvent ev) -> {
				RicercaController.rimuoviFilm(this.model.getId());
			});
			
			listaPulsanti.add(btnShow);
			listaPulsanti.add(btnModifica);
			listaPulsanti.add(btnElimina);
		}
		opzioni.getChildren().addAll(listaPulsanti);
	}
	
	/**
	 * Metodo per l'incapsulamento dei model all'interno del contenitore RigaFilm.
	 * Passatogli in ingresso una lista di Model Film restituisce una lista di container RigaFilm
	 * @param lista La lista di model Film
	 * @return Lista di container RigaFilm
	 */
	public static List<RigaFilm> ottieniListaRighe(List<Film> lista){
		List<RigaFilm> listaRighe= new ArrayList<RigaFilm>();
		
		for (Iterator<Film> iterator = lista.iterator(); iterator.hasNext();) {
			Film model = (Film) iterator.next();
			listaRighe.add(new RigaFilm(model));
		}
		return listaRighe;
	}
	
		
	public Film getModel() {
		return model;
	}
	
	public void setModel(Film model) {
		this.model = model;
	}
	public HBox getOpzioni() {
		return opzioni;
	}
	public void setOpzioni(HBox opzioni) {
		this.opzioni = opzioni;
	}
	
	
	// Delegate methods per l'interfacciamento con la TableView
	public String getNomeFilm() {
		return model.getNomeFilm();
	}
	
	public void setNomeFilm(String nomeFilm) {
		model.setNomeFilm(nomeFilm);
	}

	public Integer getAnno() {
		return model.getAnno();
	}

	public void setAnno(Integer anno) {
		model.setAnno(anno);
	}

	public List<String> getGenere() {
		return model.getGenere();
	}

	public void setGenere(List<String> genere) {
		model.setGenere(genere);
	}

	public Integer getDurata() {
		return model.getDurata();
	}

	public void setDurata(Integer durata) {
		model.setDurata(durata);
	}

	public LocalDate getDataUscita() {
		return model.getDataUscita();
	}

	public void setDataUscita(LocalDate dataUscita) {
		model.setDataUscita(dataUscita);
	}

}
