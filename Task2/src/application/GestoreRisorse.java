package application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import model.Film;
import model.Recensione;
import model.Statistiche;
import model.Utente;

/**
 * Classe per le funzionalità di back-end. Contiene anche le costanti.
 * 
 * @author Davide Vigna
 *
 */
public class GestoreRisorse {

	public static Utente utenteCorrente;

	// Costanti
	public static int NUM_FILM_CARICAMENTO_INIZIALE = 20;
	public static String RUOLO_ADMIN = "admin";
	public static String RUOLO_STANDARD = "standard";
	public static int AGGIUNGI_RECENSIONE = 1;
	public static int MODIFICA_RECENSIONE = 2;

	private static MongoClient mongoClient;
	private static MongoDatabase database;

	static {
		GestoreRisorse.setMongoClient(MongoClients.create("mongodb://192.168.1.192:27017"));
		GestoreRisorse.setDatabase(GestoreRisorse.getMongoClient().getDatabase("DB_TASK2"));
	}

	// Funzioni richiamate in più punti

	/**
	 * Calcola la media dei voti delle recensioni che un film ha ricevuto.
	 * Restituisce oltre alla media, anche il numero delle recensioni con cui si è
	 * calcolata.
	 * 
	 * @param idFilm L'id del film di cui si vuole calcolare la media.
	 * @return Restituisce un document nella forma: ObjectId(_id)  (idFilm) ,
	 *         Double media  (mediaVoto), int num_rec (numero delle recensioni)
	 *         coinvolte.
	 */
	public static AggregateIterable<Document> calcolaMediaVotoRecensioniFilm(ObjectId idFilm) {
		return GestoreRisorse.getDatabase().getCollection("recensioni").aggregate(Arrays.asList(
				Aggregates.match(Filters.eq("id_film", idFilm)),
				Aggregates.group("$id_film", Accumulators.avg("media", "$voto"), Accumulators.sum("count", 1))));
	}

	/**
	 * Ricalcola la media voto delle recensioni associate ad un film ed aggiorna il
	 * film nella collection con il nuovo numero di recensioni e nuovo valore media
	 * calcolato.
	 * 
	 * @param idFilm L'id del film di cui si calcola la media e il quale viene
	 *               aggiornato.
	 */
	public static void aggiornaMediaFilm(ObjectId idFilm) {
		// Ricalcolo media e num_recensioni x film in questione
		AggregateIterable<Document> result = GestoreRisorse.calcolaMediaVotoRecensioniFilm(idFilm);
		if (result != null) {
			double media = 0.0;
			int num_rec = 0;
			for (Document dbObject : result) {
				media = dbObject.getDouble("media");
				num_rec = dbObject.getInteger("count");
			}

			BasicDBObject updateDocument = new BasicDBObject();
			updateDocument.append("$set",
					new BasicDBObject().append("media_voto", media).append("num_recensioni", num_rec));

			GestoreRisorse.getDatabase().getCollection("film").updateOne(Filters.eq("_id", idFilm), updateDocument);
		}
	}

	// Query su Utente

	/**
	 * Query per la ricerca di un Utente specifico.
	 * 
	 * @param username Il nome utente per cui si ricerca.
	 * @param password La password associata a quell'username.
	 * @return Utente - se presente l'utente ricercato con quella password,
	 *         altrimenti null.
	 */
	public static Utente ricercaUtente(String username, String password) {

		Utente utente = null;
		MongoCursor<Document> cursor = GestoreRisorse.getDatabase().getCollection("utenti")
				.find(Filters.and(Filters.eq("username", username), Filters.eq("password", password))).iterator();
		if (cursor.hasNext()) {
			Document d = cursor.next();
			utente = new Utente(d);
		}
		cursor.close();
		return utente;
	}

	// Query su Film

	/**
	 * Query per il conteggio dei film che rispettano un certo criterio di ricerca.
	 * 
	 * @param selectQuery Filtro di ricerca.
	 * @return Numero di Film che rispettano quel filtro di ricerca.
	 */
	public static long numeroFilmRicercati(BasicDBObject selectQuery) {
		long init = System.currentTimeMillis();
		long num = GestoreRisorse.getDatabase().getCollection("film").countDocuments(selectQuery);
		long end = System.currentTimeMillis();
		System.out.println("Tempo impiegato nella count : " + (end - init) + " ms");
		return num;
	}

	/**
	 * Funzione per il caricamento iniziale degli ultimi N film con data_uscita minore o uguale
	 * ad oggi.
	 * 
	 * @param N Numero di Film da caricare.
	 * @return Lista di Film che rispetta il criterio indicato sopra.
	 */
	public static List<Film> caricaUltimiNFilm(int N) {
		int curr_year = Calendar.getInstance().get(Calendar.YEAR);
		Instant instant = Instant.now();
		Date now = Date.from(instant);

		List<Film> listaFilm = new ArrayList<Film>();

		MongoCursor<Document> cursor = GestoreRisorse.getDatabase().getCollection("film")
				.find(Filters.and(Filters.eq("anno", curr_year), Filters.lte("data_uscita", now))).limit(N).iterator();
		while (cursor.hasNext()) {
			Document d = cursor.next();
			Film film = new Film(d);
			listaFilm.add(film);
		}
		cursor.close();
		return listaFilm;
	}

	/**
	 * Query per la ricerca di Film che rispettano un certo criterio di ricerca.
	 * 
	 * @param selectQuery Filtro di ricerca.
	 * @return Lista di Film che rispettano quel filtro di ricerca
	 */
	public static List<Film> effettuaQueryRicercaFilm(BasicDBObject selectQuery) {

		List<Film> listaFilm = new ArrayList<Film>();
		long init = System.currentTimeMillis();
		MongoCursor<Document> cursor = GestoreRisorse.getDatabase().getCollection("film").find(selectQuery).iterator();

		long end = System.currentTimeMillis();
		System.out.println("Tempo impiegato nella query : " + (end - init) + " ms");

		while (cursor.hasNext()) {
			Document d = cursor.next();
			Film film = new Film(d);
			listaFilm.add(film);
		}
		cursor.close();
		return listaFilm;
	}

	/**
	 * Metodo per l'inserimento di un nuovo Film nella Collection Film.
	 * 
	 * @param d Document rappresentante il nuovo film.
	 */
	public static void inserisciFilm(Document d) {
		GestoreRisorse.getDatabase().getCollection("film").insertOne(d);
	}

	/**
	 * Metodo per l'aggiornamento di un Film esistente nella Collection Film.
	 * 
	 * @param idFilm         L'id del film che si vuole aggiornare.
	 * @param updateDocument Document contenente i campi ed i valori da aggiornare.
	 */
	public static void aggiornaFilm(ObjectId idFilm, BasicDBObject updateDocument) {
		GestoreRisorse.getDatabase().getCollection("film").updateOne(Filters.eq("_id", idFilm), updateDocument);
	}

	/**
	 * Query per l'eliminazione di Film che rispettano un criterio di ricerca.
	 * 
	 * @param deleteQuery Filtro di ricerca.
	 */
	public static void rimuoviFilm(BasicDBObject deleteQuery) {
		GestoreRisorse.getDatabase().getCollection("film").deleteOne(deleteQuery);
		System.out.println("Film eliminato con successo");
	}

	// Query su recensioni

	/**
	 * Query per la ricerca di Recensioni che rispettano un certo criterio di
	 * ricerca.
	 * 
	 * @param selectQuery Filtro di ricerca.
	 * @return Lista di Recensioni che rispettano quel filtro di ricerca.
	 */
	public static List<Recensione> effettuaQueryRicercaRecensioni(BasicDBObject selectQuery) {
		List<Recensione> listaRecensioni = new ArrayList<Recensione>();

		MongoCursor<Document> cursor = GestoreRisorse.getDatabase().getCollection("recensioni").find(selectQuery)
				.iterator();
		while (cursor.hasNext()) {
			Document d = cursor.next();
			Recensione rec = new Recensione(d);
			listaRecensioni.add(rec);
		}
		return listaRecensioni;
	}

	/**
	 * Metodo per l'inserimento di una nuova Recensione nella Collection Recensione.
	 * A seguito dell'inserimento si aggiorna la media del Film e il numero delle
	 * recensioni ad esso associate.
	 * 
	 * @param d      Document rappresentante la nuova Recensione.
	 * @param idFilm L'id del Film che deve essere aggiornato a seguito
	 *               dell'inserimento.
	 */
	public static void inserisciRecensione(Document d, ObjectId idFilm) {
		GestoreRisorse.getDatabase().getCollection("recensioni").insertOne(d);
		GestoreRisorse.aggiornaMediaFilm(idFilm);
	}

	/**
	 * Metodo per l'aggiornamento di una Recensione esistente nella Collection
	 * Recensioni. A seguito dell'aggiornamento si aggiorna la media del Film e il
	 * numero delle recensioni ad esso associate.
	 * 
	 * @param idRecensione   L'id della Recensione che si vuole aggiornare.
	 * @param updateDocument Document contenente i campi ed i valori da aggiornare.
	 * @param idFilm         L'id del film da aggiornare a seguito della rimozione
	 *                       della recensione.
	 */
	public static void aggiornaRecensione(ObjectId idRecensione, BasicDBObject updateDocument, ObjectId idFilm) {
		GestoreRisorse.getDatabase().getCollection("recensioni").updateOne(Filters.eq("_id", idRecensione),
				updateDocument);
		GestoreRisorse.aggiornaMediaFilm(idFilm);
	}

	/**
	 * Metodo per la rimozione di una recensione dalla Collection Recensioni. A
	 * seguito della rimozione si aggiorna la media del Film e il numero delle
	 * recensioni associate.
	 * 
	 * @param idRecensione L'id della recensione da rimuovere.
	 * @param idFilm       L'id del film da aggiornare a seguito della rimozione
	 *                     della recensione.
	 */
	public static void rimuoviRecensione(ObjectId idRecensione, ObjectId idFilm) {

		GestoreRisorse.getDatabase().getCollection("recensioni").deleteOne(Filters.eq("_id", idRecensione));
		GestoreRisorse.aggiornaMediaFilm(idFilm);
		idRecensione = null;
		System.out.println("Recensione eliminata con successo");
	}

	// Query per statistiche

	/**
	 * Query per la ricerca di tutti i Film che hanno ottenuto almeno una
	 * recensione, con durata maggiore o uguale a 60, che non siano come Genere Documentario, in ordine
	 * decrescente per Numero Recensioni e a parità di numero recensioni, in ordine
	 * decrescente per media voto. Metodo per le statistiche Utente tipologia
	 * Standard.
	 * 
	 * @return Lista dei film dal maggiornmente recensito al meno.
	 */
	public static List<Statistiche> statisticheMostReviewed() {

		// db.film.aggregate({$match:{"num_recensioni":{$gt:0},"durata_min":{$gte:60},
		// "genere": {$nin:
		// ["Documentario"]}}},{$sort:{"num_recensioni":-1,"media_voto":-1}}).pretty()
		List<Statistiche> listaStatistiche = new ArrayList<Statistiche>();
		Bson match = Aggregates.match(Filters.and(Filters.gt("num_recensioni", 0), Filters.gte("durata_min", 60),
				Filters.nin("genere", "Documentario")));
		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("num_recensioni", -1);
		ordinamento.put("media_voto", -1);
		Bson order = Aggregates.sort(ordinamento);
		
		long init = System.currentTimeMillis();
		
		AggregateIterable<Document> result = GestoreRisorse.getDatabase().getCollection("film")
				.aggregate(Arrays.asList(match, order));
		
		long end = System.currentTimeMillis();
		System.out.println("Tempo impiegato nella QueryStat1 : " + (end - init) + " ms");
		
		for (Document dbObject : result) {
			Statistiche sus = new Statistiche(dbObject, GestoreRisorse.RUOLO_STANDARD);
			listaStatistiche.add(sus);
		}
		return listaStatistiche;
	}

	/**
	 * Query per la ricerca di tutti i migliori Film per anno. I Film ricercati
	 * devono avere ricevuto almeno 5 recensioni e la media voto deve essere
	 * maggiore degli altri nel rispettivo anno. A parità di media voto si considera
	 * il film con più recensioni. Questa Lista viene restituita in ordine
	 * crescente per anno.
	 * 
	 * Metodo per le statistiche Utente tipologia Standard.
	 * 
	 * @return Lista dei Film che rispettano la caratteristica.
	 */
	public static List<Statistiche> statisticheBestByYear() {

		// db.film.aggregate({$match:{"num_recensioni":{$gt:5}}},{"$sort":
		// {"media_voto":
		// -1,"num_recensioni":-1}},{$group:{_id:{anno:"$anno"},voto:{$max:"$media_voto"},nome_film:{$first:"$nome"},num_recensioni:{$first:"$num_recensioni"}}},
		// {$project:
		// {_id:0,"anno":"$_id.anno","nome":"$nome_film","media_voto":"$voto","num_recensioni":"$num_recensioni"}},{"$sort":
		// {"anno": 1}}).pretty()

		List<Statistiche> listaStatistiche = new ArrayList<Statistiche>();
		long init = System.currentTimeMillis();
		
		Bson match = Aggregates.match(Filters.and(Filters.gt("num_recensioni", 5)));
		
		long end = System.currentTimeMillis();
		System.out.println("Tempo impiegato nella QueryStat1 : " + (end - init) + " ms");
		
		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("media_voto", -1);
		ordinamento.put("num_recensioni", -1);

		Bson sort1 = Aggregates.sort(ordinamento);
		BasicDBObject objGroupId = new BasicDBObject();
		objGroupId.put("anno", "$anno");

		Bson group = Aggregates.group(objGroupId, Accumulators.max("voto", "$media_voto"),
				Accumulators.first("nome_film", "$nome"), Accumulators.first("num_recensioni", "$num_recensioni"));

		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("_id", 0);
		projectFields.put("anno", "$_id.anno");
		projectFields.put("nome", "$nome_film");
		projectFields.put("media_voto", "$voto");
		projectFields.put("num_recensioni", "$num_recensioni");

		Bson project = Aggregates.project(projectFields);

		BasicDBObject ordinamento2 = new BasicDBObject();
		ordinamento2.put("anno", 1);

		Bson sort2 = Aggregates.sort(ordinamento2);

		AggregateIterable<Document> result = GestoreRisorse.getDatabase().getCollection("film")
				.aggregate(Arrays.asList(match, sort1, group, project, sort2));

		for (Document dbObject : result) {
			Statistiche sus = new Statistiche(dbObject, GestoreRisorse.RUOLO_STANDARD);
			listaStatistiche.add(sus);
		}

		return listaStatistiche;
	}

	/**
	 * Query per il calcolo del numero di recensioni effettutate da ciascun Utente,
	 * suddivise in totaliRecensioni, numero recensioni positive, numero recensioni
	 * negative e media voto complessiva per Utente. La lista degli Utenti viene
	 * restituita in ordine decrescente per numero di recensioni totali effettuate.
	 * 
	 * @return Lista di Statistiche che rispettano la caratteristica.
	 */
	public static List<Statistiche> statisticheDetailReviews() {

		// db.recensioni.aggregate({$group:
		// {_id:{utente:"$username"},num_recensioni:{$sum:1},media:{$avg:"$voto"},positive:{$sum
		// : { $cond : [ {$gte : [ "$voto", 6 ]} , 1, 0 ] } }, negative:{$sum : { $cond
		// : [ {$lt : [ "$voto", 6 ]} , 1, 0 ] } }}},{$sort: {"totRec": -1 ,"media":
		// -1}})

		List<Statistiche> listaStatistiche = new ArrayList<Statistiche>();

		BasicDBObject objGroupId = new BasicDBObject();
		objGroupId.put("utente", "$username");

		BsonArray condPos = new BsonArray();
		BsonArray gte = new BsonArray();
		gte.add(new BsonString("$voto"));
		gte.add(new BsonInt64(6));

		condPos.add(new BsonDocument("$gte", gte));
		condPos.add(new BsonInt64(1));
		condPos.add(new BsonInt64(0));

		BsonArray condNeg = new BsonArray();
		BsonArray lt = new BsonArray();
		lt.add(new BsonString("$voto"));
		lt.add(new BsonInt64(6));

		condNeg.add(new BsonDocument("$lt", lt));
		condNeg.add(new BsonInt64(1));
		condNeg.add(new BsonInt64(0));

		Bson group = Aggregates.group(objGroupId, Accumulators.sum("totRec", 1), Accumulators.avg("media", "$voto"),
				Accumulators.sum("positive", new BsonDocument("$cond", condPos)),
				Accumulators.sum("negative", new BsonDocument("$cond", condNeg)));

		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("_id", 0);
		projectFields.put("utente", "$_id.utente");
		projectFields.put("num_recensioni", "$totRec");
		projectFields.put("media_voto", "$media");
		projectFields.put("positive", "$positive");
		projectFields.put("negative", "$negative");

		Bson project = Aggregates.project(projectFields);

		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("num_recensioni", -1);
		ordinamento.put("media", -1);

		Bson sort1 = Aggregates.sort(ordinamento);

		AggregateIterable<Document> result = GestoreRisorse.getDatabase().getCollection("recensioni")
				.aggregate(Arrays.asList(group, project, sort1));

		for (Document dbObject : result) {
			Statistiche sus = new Statistiche(dbObject, GestoreRisorse.RUOLO_ADMIN);
			listaStatistiche.add(sus);
		}

		return listaStatistiche;
	}

	
	public static MongoClient getMongoClient() {
		return mongoClient;
	}
	public static void setMongoClient(MongoClient mongoClient) {
		GestoreRisorse.mongoClient = mongoClient;
	}

	public static MongoDatabase getDatabase() {
		return database;
	}
	public static void setDatabase(MongoDatabase database) {
		GestoreRisorse.database = database;
	}
	public static Utente getUtenteCorrente() {
		return utenteCorrente;
	}
	public static void setUtenteCorrente(Utente utenteCorrente) {
		GestoreRisorse.utenteCorrente = utenteCorrente;
	}

}
