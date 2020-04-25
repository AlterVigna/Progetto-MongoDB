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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import model.Film;
import model.Recensione;
import model.Statistica;
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
	//public static String INDIRIZZO_DATABASE ="mongodb://192.168.1.192:27017,192.168.1.191:27017,192.168.1.191:27018";
	public static String INDIRIZZO_DATABASE = "mongodb://192.168.1.192:27017";
	public static String NOME_DATABASE = "DB_TASK2";

	public static int NUM_FILM_CARICAMENTO_INIZIALE = 20;
	public static int NUM_RIGHE_A_VIDEO=25;
	public static String RUOLO_ADMIN = "admin";
	public static String RUOLO_STANDARD = "standard";
	public static int AGGIUNGI_RECENSIONE = 1;
	public static int MODIFICA_RECENSIONE = 2;

	private static MongoClient mongoClient;
	private static MongoDatabase database;

	
	/** 
	 * Inizializzo la connessione col database - set di replica
	 */
	public static void initConnection() {
		if (GestoreRisorse.getMongoClient()==null && GestoreRisorse.getDatabase()==null) {
			GestoreRisorse.setMongoClient(MongoClients.create(INDIRIZZO_DATABASE));
			GestoreRisorse.setDatabase(GestoreRisorse.getMongoClient().getDatabase(NOME_DATABASE));
		}
	}
	
	
	
	// Funzione richiamata in più punti

	/**
	 * Calcola la media dei voti delle recensioni che un film ha ricevuto e il
	 * numero delle recensioni associate ad un film ed aggiorna il film nella
	 * collection con il nuovo numero di recensioni e nuovo valore media calcolato.
	 * 
	 * @param idFilm L'id del film di cui si calcola la media e il quale viene
	 *               aggiornato.
	 */
	public static void aggiornaMediaFilm(ObjectId idFilm) {
		// Ricalcolo media e num_recensioni x film in questione

		Bson match = Aggregates.match(Filters.eq("id_film", idFilm));
		Bson group = Aggregates.group("$id_film", Accumulators.avg("media", "$voto"), Accumulators.sum("count", 1));

		AggregateIterable<Document> result = GestoreRisorse.getDatabase().getCollection("recensioni")
				.aggregate(Arrays.asList(match, group)).allowDiskUse(true);
		if (result != null) {
			double media = 0.0;
			int num_rec = 0;
			for (Document dbObject : result) {
				media = dbObject.getDouble("media");
				num_rec = dbObject.getInteger("count");
			}

			Document d = new Document();
			d.put("media_voto", media);
			d.put("num_recensioni", num_rec);

			GestoreRisorse.aggiornaFilm(idFilm, d);
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
		/*
		 * db.utenti.find({"username":"param1","password":"param2"})
		 */
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
	 * Funzione per il caricamento iniziale degli ultimi N film con data_uscita
	 * minore o uguale ad oggi.
	 * 
	 * @param N Numero di Film da caricare.
	 * @return Lista di Film che rispetta il criterio indicato sopra.
	 */
	public static List<Film> caricaUltimiNFilm(int N) {

		/*
		 * db.film.find({"anno":{"$eq":2020}, "data_uscita":{"$lte":data_corrente}})
		 * .sort({“data_uscita”:-1}).limit(N)
		 */

		int curr_year = Calendar.getInstance().get(Calendar.YEAR);
		Instant instant = Instant.now();
		Date now = Date.from(instant);

		List<Film> listaFilm = new ArrayList<Film>();

		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("data_uscita", -1);

		MongoCursor<Document> cursor = GestoreRisorse.getDatabase().getCollection("film")
				.find(Filters.and(Filters.eq("anno", curr_year), Filters.lte("data_uscita", now))).sort(ordinamento)
				.limit(N).iterator();
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

		/* db.film.find({Filtro in input}) */

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
		System.out.println("Film inserito con successo");
	}

	/**
	 * Metodo per l'aggiornamento di un Film esistente nella Collection Film.
	 * 
	 * @param idFilm         L'id del film che si vuole aggiornare.
	 * @param updateDocument Document contenente i campi ed i valori da aggiornare.
	 */
	public static void aggiornaFilm(ObjectId idFilm, Document d) {

		BasicDBObject updateDocument = new BasicDBObject();
		updateDocument.append("$set", d);

		GestoreRisorse.getDatabase().getCollection("film").updateOne(Filters.eq("_id", idFilm), updateDocument);
		System.out.println("Film aggiornato con successo");
	}

	/**
	 * Query per l'eliminazione di Film che rispettano un criterio di ricerca.
	 * 
	 * @param idFilm L'id del film da eliminare.
	 */
	public static void rimuoviFilm(ObjectId idFilm) {
		GestoreRisorse.getDatabase().getCollection("film").deleteOne(Filters.eq("_id", idFilm));
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
		/*
		 * db.recensioni.find({filtro})
		 */
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
	 * @param d Document rappresentante la nuova Recensione.
	 */
	public static void inserisciRecensione(Document d) {
		GestoreRisorse.getDatabase().getCollection("recensioni").insertOne(d);
		GestoreRisorse.aggiornaMediaFilm(d.getObjectId("id_film"));
	}

	/**
	 * Metodo per l'aggiornamento di una Recensione esistente nella Collection
	 * Recensioni. A seguito dell'aggiornamento si aggiorna la media del Film e il
	 * numero delle recensioni ad esso associate.
	 * 
	 * @param idRecensione L'id della Recensione che si vuole aggiornare.
	 * @param doc          Document contenente i campi ed i valori da aggiornare.
	 * @param idFilm       L'id del film da aggiornare a seguito della rimozione
	 *                     della recensione.
	 */
	public static void aggiornaRecensione(ObjectId idRecensione, Document doc, ObjectId idFilm) {

		BasicDBObject updateDocument = new BasicDBObject();
		updateDocument.append("$set", doc);

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
	 * recensione, con durata maggiore o uguale a 60, che non siano come Genere
	 * Documentario, in ordine decrescente per Numero Recensioni e a parità di
	 * numero recensioni, in ordine decrescente per media voto. Metodo per le
	 * statistiche Utente tipologia Standard.
	 * 
	 * @return Lista dei film dal maggiornmente recensito al meno.
	 */
	public static List<Statistica> statisticheMostReviewed() {

		// db.film.find({"num_recensioni":{$gt:0}},"durata_min":{$gte:60}, "genere":
		// {$nin:
		// ["Documentario"]}},{anno:1,nome:1,num_recensioni:1,media_voto:1}).sort({num_recensioni:-1,media_voto:-1})

		List<Statistica> listaStatistiche = new ArrayList<Statistica>();

		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("num_recensioni", -1);
		ordinamento.put("media_voto", -1);

		BasicDBObject selectQuery = new BasicDBObject();
		selectQuery.put("num_recensioni", new BasicDBObject("$gt", 0));
		selectQuery.put("durata_min", new BasicDBObject("$gte", 60));
		selectQuery.put("genere", new BasicDBObject("$nin", Arrays.asList("Documentario")));

		BasicDBObject projectObj = new BasicDBObject();
		projectObj.put("anno", 1);
		projectObj.put("nome", 1);
		projectObj.put("num_recensioni", 1);
		projectObj.put("media_voto", 1);

		long init = System.currentTimeMillis();
		FindIterable<Document> ris = GestoreRisorse.getDatabase().getCollection("film").find(selectQuery)
				.projection(projectObj).sort(ordinamento);
		long end = System.currentTimeMillis();
		System.out.println("Tempo impiegato nella Senza Aggregate : " + (end - init) + " ms");

		for (Document dbObject : ris) {
			Statistica sus = new Statistica(dbObject, GestoreRisorse.RUOLO_STANDARD);
			listaStatistiche.add(sus);
		}

		return listaStatistiche;
	}

	/**
	 * Query per la ricerca di tutti i migliori Film per anno. I Film ricercati
	 * devono avere ricevuto almeno 5 recensioni e la media voto deve essere
	 * maggiore degli altri nel rispettivo anno. A parità di media voto si considera
	 * il film con più recensioni. Questa Lista viene restituita in ordine crescente
	 * per anno.
	 * 
	 * Metodo per le statistiche Utente tipologia Standard.
	 * 
	 * @return Lista dei Film che rispettano la caratteristica.
	 */
	public static List<Statistica> statisticheBestByYear() {

		// db.film.aggregate([{$match:{"num_recensioni":{$gt:5}}},{"$sort":
		// {"media_voto":
		// -1,"num_recensioni":-1}},{$group:{_id:{anno:"$anno"},voto:{$max:"$media_voto"},nome_film:{$first:"$nome"},num_recensioni:{$first:"$num_recensioni"}}},{$project:
		// {_id:0,"anno":"$_id.anno","nome":"$nome_film","media_voto":"$voto","num_recensioni":"$num_recensioni"}},{"$sort":
		// {"anno": 1}}],{allowDiskUse:true})

		List<Statistica> listaStatistiche = new ArrayList<Statistica>();
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
				.aggregate(Arrays.asList(match, sort1, group, project, sort2)).allowDiskUse(true);

		for (Document dbObject : result) {
			Statistica sus = new Statistica(dbObject, GestoreRisorse.RUOLO_STANDARD);
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
	public static List<Statistica> statisticheDetailReviews() {

		// db.recensioni.aggregate([{$group:
		// {_id:{utente:"$id_utente"},utente:{$first:"$username"},totRec:{$sum:1},media:{$avg:"$voto"},positive:{$sum
		// : { $cond : [ {$gte : [ "$voto", 6 ]} , 1, 0 ] } }, negative:{$sum : { $cond
		// : [ {$lt : [ "$voto", 6 ]} , 1, 0 ] } }}},{$project:
		// {_id:0,utente:1,"num_recensioni":"$totRec", "media_voto":"$media",positive:1,
		// negative:1}},{$sort: {"num_recensioni": -1 ,"media_voto":
		// -1}}],{allowDiskUse:true})

		List<Statistica> listaStatistiche = new ArrayList<Statistica>();

		BasicDBObject objGroupId = new BasicDBObject();
		objGroupId.put("idUt", "$id_utente");

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

		Bson group = Aggregates.group(objGroupId, Accumulators.first("utente", "$username"),
				Accumulators.sum("totRec", 1), Accumulators.avg("media", "$voto"),
				Accumulators.sum("positive", new BsonDocument("$cond", condPos)),
				Accumulators.sum("negative", new BsonDocument("$cond", condNeg)));

		BasicDBObject projectFields = new BasicDBObject();
		projectFields.put("_id", 0);
		projectFields.put("utente", "$utente");
		projectFields.put("num_recensioni", "$totRec");
		projectFields.put("media_voto", "$media");
		projectFields.put("positive", 1);
		projectFields.put("negative", 1);

		Bson project = Aggregates.project(projectFields);

		BasicDBObject ordinamento = new BasicDBObject();
		ordinamento.put("num_recensioni", -1);
		ordinamento.put("media", -1);

		Bson sort1 = Aggregates.sort(ordinamento);

		AggregateIterable<Document> result = GestoreRisorse.getDatabase().getCollection("recensioni")
				.aggregate(Arrays.asList(group, project, sort1)).allowDiskUse(true);

		for (Document dbObject : result) {
			Statistica sus = new Statistica(dbObject, GestoreRisorse.RUOLO_ADMIN);
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
