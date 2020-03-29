package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Classe che rappresenta la struttura di un document nella collection Film.
 * @author Davide Vigna
 */
public class Film {
	
	private ObjectId id;
	private String nomeFilm;
	private Integer anno;
	private List<String> genere;
	
	private Integer durata;
	private LocalDate dataUscita;
	
	private String tramaCompleta;
	private List<String> paesiProd;
	private Double mediaVoto;
	private Integer numRecensioni;
	private List<Cast> listaCast=new ArrayList<Cast>();
	
	public Film(Document d) {
		
		if (d!=null) {
			
			if (d.get("_id")!=null) {
				id=d.getObjectId("_id");
			}
			
			if (d.get("nome")!=null) {
				nomeFilm=d.getString("nome");
			}
			
			if (d.get("anno")!=null) {
				anno=((Number) d.get("anno")).intValue();
			}
			
			if (d.get("durata_min")!=null) {
				durata=((Number) d.get("durata_min")).intValue();
			}
			
			genere=d.getList("genere", String.class,new ArrayList<String>());
			
			if (d.get("tramaCompleta")!=null) {
				tramaCompleta=d.getString("tramaCompleta");
			}
			else tramaCompleta="";
			
			paesiProd=d.getList("paesi_prod", String.class, new ArrayList<String>());
			
			if (d.get("media_voto")!=null) {
				mediaVoto=((Number) d.get("media_voto")).doubleValue();
			}
			
			if (d.get("num_recensioni")!=null) {
				numRecensioni=((Number) d.get("num_recensioni")).intValue();
			}
			
			Date data=d.getDate("data_uscita");
			if (data!=null) {
				LocalDateTime ldt = LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault());
				dataUscita=ldt.toLocalDate();
			}
			
			if (d.getList("cast", Document.class)!=null) {
				List<Document> listaAttori = d.getList("cast", Document.class);

				for (Iterator<Document> iterator = listaAttori.iterator(); iterator.hasNext();) {
					Document attore = (Document) iterator.next();
					String nomeAttore=attore.getString("nome");
					String nomeRuolo=attore.getString("ruolo");
					Cast c= new Cast(nomeAttore, nomeRuolo);
					listaCast.add(c);
				}
			}
			
		}
	}

	
	/**
	 * Classe rappresentante l'Embedded Document Cast, all'interno di un document nella collection Film.
	 * @author Davide
	 *
	 */
	public static class Cast {

		private String nome;
		private String ruolo;
		
		public Cast(String nome, String ruolo) {
			this.nome=nome;
			this.ruolo=ruolo;
		}
		
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getRuolo() {
			return ruolo;
		}
		public void setRuolo(String ruolo) {
			this.ruolo = ruolo;
		}

	}
	
	//Getter And Setter
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getNomeFilm() {
		return nomeFilm;
	}
	public void setNomeFilm(String nomeFilm) {
		this.nomeFilm = nomeFilm;
	}
	public Integer getAnno() {
		return anno;
	}
	public void setAnno(Integer anno) {
		this.anno = anno;
	}
	public List<String> getGenere() {
		return genere;
	}
	public void setGenere(List<String> genere) {
		this.genere = genere;
	}
	public Integer getDurata() {
		return durata;
	}
	public void setDurata(Integer durata) {
		this.durata = durata;
	}
	public LocalDate getDataUscita() {
		return dataUscita;
	}
	public void setDataUscita(LocalDate dataUscita) {
		this.dataUscita = dataUscita;
	}

	public String getTramaCompleta() {
		return tramaCompleta;
	}


	public void setTramaCompleta(String tramaCompleta) {
		this.tramaCompleta = tramaCompleta;
	}

	public List<String> getPaesiProd() {
		return paesiProd;
	}


	public void setPaesiProd(List<String> paesiProd) {
		this.paesiProd = paesiProd;
	}


	public Double getMediaVoto() {
		return mediaVoto;
	}


	public void setMediaVoto(Double mediaVoto) {
		this.mediaVoto = mediaVoto;
	}


	public Integer getNumRecensioni() {
		return numRecensioni;
	}


	public void setNumRecensioni(Integer numRecensioni) {
		this.numRecensioni = numRecensioni;
	}
	
	
	public List<Cast> getListaCast() {
		return listaCast;
	}

	public void setListaCast(List<Cast> listaCast) {
		this.listaCast = listaCast;
	}
	
	/**
	 * Metodo di utilità per la conversione in stringa dell'elenco dei generi di appartenenza di un film.
	 * @return La stringa di concatenazione di tutti i generi dei film.
	 */
	public String getTuttiGeneriStringa(){
		String tuttiGeneri="";
		for (Iterator<String> iterator = genere.iterator(); iterator.hasNext();){
			String genere = (String) iterator.next();
			tuttiGeneri+=genere;
			if (iterator.hasNext()) tuttiGeneri+=", ";
		}	
		return tuttiGeneri;
	}
	
	public String getTuttiPaesiProduzioneStringa() {
		String tuttiPaesi="";
		for (Iterator<String> iterator = paesiProd.iterator(); iterator.hasNext();) {
			String paesiProd = (String) iterator.next();
			tuttiPaesi+=paesiProd;
			if (iterator.hasNext()) tuttiPaesi+=", ";
		}
		return tuttiPaesi;
	}
	

}
