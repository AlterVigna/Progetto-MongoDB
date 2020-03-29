package model;

import org.bson.Document;

import application.GestoreRisorse;


/**
 * Classe utilizzata per la rappresentazione delle statistiche.
 * Viene utilizzata sia per le statistiche dell'utente di tipo Standard che Admin.
 * 
 * Gli attributi necessari per la tipologia "Standard" sono: anno, nomeFilm, numeroRecensioni, mediaVoto.
 * Gli attributi necessari per la tipologia "Admin" sono : utente,numRecPositive,numRecNegative,numeroRecensioni, mediaVoto.
 * 
 * @author Davide Vigna
 *
 */
public class Statistiche {

	// campi per statistiche standard
	private Integer anno;
	private String nomeFilm;
	
	// campi per statistiche admin
	private String utente;
	private Long numRecPositive;
	private Long numRecNegative;
	
	
	// campi comuni ad entrambi
	private Long numeroRecensioni;
	private Double mediaVoto;
	
	
	
	public Statistiche (Document d,String tipologia) {
		if (d!=null) {
			
			if (tipologia.equals(GestoreRisorse.RUOLO_STANDARD)) {
				if (d.get("anno")!=null) {
					anno=((Number) d.get("anno")).intValue();
				}
				
				if (d.get("nome")!=null) {
					nomeFilm=d.getString("nome");
				}
			}
			
			if (tipologia.equals(GestoreRisorse.RUOLO_ADMIN)) {
				
				if (d.get("utente")!=null) {
					utente=d.getString("utente");
				}
				
				if (d.get("positive")!=null) {
					numRecPositive=((Number) d.get("positive")).longValue();
				}
				
				if (d.get("negative")!=null) {
					numRecNegative=((Number) d.get("negative")).longValue();
				}
			}
			
			// campi comuni
			if (d.get("media_voto")!=null) {
				mediaVoto=((Number) d.get("media_voto")).doubleValue();
			}
			
			if (d.get("num_recensioni")!=null) {
				numeroRecensioni=((Number) d.get("num_recensioni")).longValue();	
			}
			
		}
	}


	
	//Getter And Setter
	public Integer getAnno() {
		return anno;
	}


	public void setAnno(Integer anno) {
		this.anno = anno;
	}


	public String getNomeFilm() {
		return nomeFilm;
	}


	public void setNomeFilm(String nomeFilm) {
		this.nomeFilm = nomeFilm;
	}


	public Double getMediaVoto() {
		return Math.round(mediaVoto*100.0)/100.0;
	}



	public void setMediaVoto(Double mediaVoto) {
		this.mediaVoto = mediaVoto;
	}



	public Long getNumeroRecensioni() {
		return numeroRecensioni;
	}


	public void setNumeroRecensioni(Long numeroRecensioni) {
		this.numeroRecensioni = numeroRecensioni;
	}


	public String getUtente() {
		return utente;
	}


	public void setUtente(String utente) {
		this.utente = utente;
	}


	public Long getNumRecPositive() {
		return numRecPositive;
	}


	public void setNumRecPositive(Long numRecPositive) {
		this.numRecPositive = numRecPositive;
	}


	public Long getNumRecNegative() {
		return numRecNegative;
	}


	public void setNumRecNegative(Long numRecNegative) {
		this.numRecNegative = numRecNegative;
	}

}
