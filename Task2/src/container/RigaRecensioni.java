package container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.GestoreRisorse;
import controller.MostraRecensioniController;
import controller.RicercaController;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import model.Recensione;

/**
 * Classe per la rappresentazione di una riga nella Tabella delle recensioni. E'
 * composta da 2 elementi. Il model che contiene tutte le informazioni da
 * mostrare a video di una recensione. Un elemento Hbox che contiene le opzioni
 * che si possono fare su quello specifico elemento mostrato a video.
 * 
 * @author Davide Vigna
 *
 */
public class RigaRecensioni {

	private Recensione model;
	private HBox opzioni = new HBox();

	public RigaRecensioni(Recensione model) {
		super();
		this.model = model;

		opzioni.getChildren().clear();

		List<Button> listaPulsanti = new ArrayList<Button>();

		if (GestoreRisorse.getUtenteCorrente().getRuolo().equals(GestoreRisorse.RUOLO_STANDARD)
				&& GestoreRisorse.getUtenteCorrente().getUsername().equals(this.model.getUtente())) {

			Button modificaRecensione = new Button("Modifica");
			Button eliminaRecensione = new Button("Elimina");
			modificaRecensione.setTooltip(new Tooltip("Modifica Recensione"));

			modificaRecensione.setOnAction((ActionEvent ev) -> {
				RicercaController.apriInterfacciaDettaglioRecensione(this.model.getIdRecensione(),
						this.model.getIdFilm(), this.model.getNomeFilm(),
						GestoreRisorse.MODIFICA_RECENSIONE);
			});

			eliminaRecensione.setOnAction((ActionEvent ev) -> {
				GestoreRisorse.rimuoviRecensione(this.model.getIdRecensione(), this.model.getIdFilm());
				MostraRecensioniController.rimuoviRigaAVideo(this.model.getIdRecensione());
			});

			listaPulsanti.add(modificaRecensione);
			listaPulsanti.add(eliminaRecensione);
		}

		if (GestoreRisorse.getUtenteCorrente().getRuolo().equals("admin")) {
			Button eliminaRecensione = new Button("Elimina");
			listaPulsanti.add(eliminaRecensione);

			eliminaRecensione.setOnAction((ActionEvent ev) -> {
				GestoreRisorse.rimuoviRecensione(this.model.getIdRecensione(), this.model.getIdFilm());
				MostraRecensioniController.rimuoviRigaAVideo(this.model.getIdRecensione());
			});
		}
		opzioni.getChildren().addAll(listaPulsanti);

	}

	/**
	 * Metodo per l'incapsulamento dei model all'interno del contenitore
	 * RigaRecensione. Passatogli in ingresso una lista di Model Recensione
	 * restituisce una lista di container RigaRecensione.
	 * 
	 * @param lista    La lista di Model recensioni.
	 * @param nomeFilm Il nome del film associato alla recensione. Questa
	 *                 informazione non è presente nella collection Recensioni,
	 *                 quindi ce la imposto al momento che mi serve. Viene
	 *                 utilizzata solamente per mostrare il nome del film relativo alla
	 *                 recensione.
	 * @return Lista di Container di tipo RigaRecensioni
	 */
	public static List<RigaRecensioni> ottieniListaRighe(List<Recensione> lista, String nomeFilm) {
		List<RigaRecensioni> listaRighe = new ArrayList<RigaRecensioni>();

		for (Iterator<Recensione> iterator = lista.iterator(); iterator.hasNext();) {
			Recensione model = (Recensione) iterator.next();
			model.setNomeFilm(nomeFilm);
			listaRighe.add(new RigaRecensioni(model));
		}
		return listaRighe;
	}

	public Recensione getModel() {
		return model;
	}

	public void setModel(Recensione model) {
		this.model = model;
	}

	public HBox getOpzioni() {
		return opzioni;
	}

	public void setOpzioni(HBox opzioni) {
		this.opzioni = opzioni;
	}

	// Delegate method
	public String getUtente() {
		return model.getUtente();
	}

	public void setUtente(String utente) {
		model.setUtente(utente);
	}

	public Double getVoto() {
		return model.getVoto();
	}

	public void setVoto(Double voto) {
		model.setVoto(voto);
	}

	public String getRecensione() {
		return model.getRecensione();
	}

	public void setRecensione(String recensione) {
		model.setRecensione(recensione);
	}

}
