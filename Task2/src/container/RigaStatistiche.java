package container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Statistica;


/**
 * Classe per la rappresentazione di una riga nella Tabella delle statistiche.
 * Viene utilizzata sia per le statistiche per utenti tipo Standard che Admin.
 * @author Davide Vigna 
 *
 */
public class RigaStatistiche {
	
	
	private Statistica model;

	public RigaStatistiche(Statistica model) {
		this.model=model;
	}
	
	/**
	 * Metodo per l'incapsulamento dei model all'interno del contenitore RigaStatistiche.
	 * Passatogli in ingresso una lista di Model Statistica restituisce una lista di container RigaStatistiche
	 * @param lista La lista di model Film
	 * @return Lista di container RigaStatistiche
	 */
	public static List<RigaStatistiche> ottieniListaRighe(List<Statistica> lista){
		List<RigaStatistiche> listaRighe= new ArrayList<RigaStatistiche>();
		
		for (Iterator<Statistica> iterator = lista.iterator(); iterator.hasNext();) {
			Statistica model = (Statistica) iterator.next();
			listaRighe.add(new RigaStatistiche(model));
		}
		return listaRighe;
	}
	
	
	
	public Statistica getModel() {
		return model;
	}

	public void setModel(Statistica model) {
		this.model = model;
	}

	
	// Delegate methods per l'interfacciamento con la TableView
	
	public Integer getAnno() {
		return model.getAnno();
	}

	public void setAnno(Integer anno) {
		model.setAnno(anno);
	}

	public String getNomeFilm() {
		return model.getNomeFilm();
	}

	public void setNomeFilm(String nomeFilm) {
		model.setNomeFilm(nomeFilm);
	}

	public Double getMediaVoto() {
		return model.getMediaVoto();
	}

	public void setMediaVoto(Double mediaVoto) {
		model.setMediaVoto(mediaVoto);
	}

	public Long getNumeroRecensioni() {
		return model.getNumeroRecensioni();
	}

	public void setNumeroRecensioni(Long numeroRecensioni) {
		model.setNumeroRecensioni(numeroRecensioni);
	}

	public String getUtente() {
		return model.getUtente();
	}

	public void setUtente(String utente) {
		model.setUtente(utente);
	}

	public Long getNumRecPositive() {
		return model.getNumRecPositive();
	}

	public void setNumRecPositive(Long numRecPositive) {
		model.setNumRecPositive(numRecPositive);
	}

	public Long getNumRecNegative() {
		return model.getNumRecNegative();
	}

	public void setNumRecNegative(Long numRecNegative) {
		model.setNumRecNegative(numRecNegative);
	}
	
	
	
	

}
