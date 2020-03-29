package model;

import org.bson.Document;
import org.bson.types.ObjectId;


/**
 * Classe che rappresenta la struttura di un document nella collection Utenti.
 * 
 * @author Davide Vigna
 *
 */
public class Utente {
	
	private ObjectId idUtente;
	private String username;
	private String ruolo;
	
	public Utente (Document d) {
		
		if (d!=null) {
			idUtente=d.getObjectId("_id");
			username=d.getString("username");
			ruolo=d.getString("ruolo");	
		}
	}

	public Utente(ObjectId idUtente, String username, String ruolo) {
		super();
		this.idUtente = idUtente;
		this.username = username;
		this.ruolo = ruolo;
	}
	
	public ObjectId getIdUtente() {
		return idUtente;
	}
	public void setIdUtente(ObjectId idUtente) {
		this.idUtente = idUtente;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRuolo() {
		return ruolo;
	}
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	
}
