package model;

import org.bson.Document;
import org.bson.types.ObjectId;


/**
 * Classe che rappresenta la struttura di un document nella collection Recensioni.
 * @author Davide Vigna
 */
public class Recensione {
	
	private ObjectId idRecensione;
	private String utente;
	private Double voto;
	private String recensione;
	private ObjectId idFilm;
	private ObjectId idUtente;
	
	// caricata esternamente
	private String nomeFilm;
	

	public Recensione() {
		
	}

	public Recensione (Document d) {
		if (d.get("_id")!=null) {
			idRecensione=d.getObjectId("_id");
		}
		if (d.get("commento")!=null){
			recensione=d.getString("commento");
		}
		
 		if (d.get("username")!=null) {
 			utente=d.getString("username");
 		}
 		if (d.get("voto")!=null) {
 			voto=((Number) d.get("voto")).doubleValue();
 		}
 		if (d.get("id_film")!=null) {
 			idFilm=d.getObjectId("id_film");
 		}
 		
 		
 		
	}

	public Recensione (Document d,String nomeFilm) {
		
		if (d.get("_id")!=null) {
			idRecensione=d.getObjectId("_id");
		}
		if (d.get("commento")!=null){
			recensione=d.getString("commento");
		}
		
 		if (d.get("username")!=null) {
 			utente=d.getString("username");
 		}
 		
 		if (d.get("voto")!=null) {
 			voto=((Number) d.get("voto")).doubleValue();
 		}
 		if (d.get("id_film")!=null) {
 			idFilm=d.getObjectId("id_film");
 		}
 	
	}
	
	
	
	//Getter and Setter
	public ObjectId getIdFilm() {
		return idFilm;
	}


	public void setIdFilm(ObjectId idFilm) {
		this.idFilm = idFilm;
	}


	public ObjectId getIdUtente() {
		return idUtente;
	}


	public void setIdUtente(ObjectId idUtente) {
		this.idUtente = idUtente;
	}
	

	public String getUtente() {
		return utente;
	}


	public void setUtente(String utente) {
		this.utente = utente;
	}


	public Double getVoto() {
		return voto;
	}


	public void setVoto(Double voto) {
		this.voto = voto;
	}


	public String getRecensione() {
		return recensione;
	}


	public void setRecensione(String recensione) {
		this.recensione = recensione;
	}
	
	public String getNomeFilm() {
		return nomeFilm;
	}


	public void setNomeFilm(String nomeFilm) {
		this.nomeFilm = nomeFilm;
	}

	public ObjectId getIdRecensione() {
		return idRecensione;
	}


	public void setIdRecensione(ObjectId idRecensione) {
		this.idRecensione = idRecensione;
	}

}
