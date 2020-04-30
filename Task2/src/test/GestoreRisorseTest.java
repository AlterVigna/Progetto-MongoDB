package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import application.GestoreRisorse;
import model.Film;
import model.Recensione;
import model.Statistica;
import model.Utente;


public class GestoreRisorseTest {

	private static MongoClient mongoClient;
	private static MongoDatabase database;

	private static Document utente1;
	private static Document utente2;
	
	private static  Document film1;
	private static  Document film2;
	
	private static Document rec1;
	private static Document rec2;
	private static Document rec3;
	private static Document rec4;
	
	 
	@BeforeClass
	public static void setup() {
		 System.out.println("Inizio TEST di Gestore Risorse ... \n");
		 try {
			 	mongoClient=MongoClients.create(GestoreRisorse.INDIRIZZO_DATABASE);
				database=mongoClient.getDatabase(GestoreRisorse.NOME_DATABASE); 
				
				GestoreRisorse.setMongoClient(mongoClient);
				GestoreRisorse.setDatabase(database);
				if (mongoClient!=null && database!=null) {
					 
					 System.out.println("Connessione creata con successo");
					 
					 // CREAZIONE DOCUMENTI PER I TEST
					 
					 utente1=new Document();
					 utente1.put("_id",new ObjectId());
					 utente1.put("username","utente_test1");
					 utente1.put("password","test");
					 utente1.put("ruolo","standard");
					 utente1.put("type","test");
					 
					 utente2=new Document();
					 utente2.put("_id",new ObjectId());
					 utente2.put("username","utente_test2");
					 utente2.put("password","test");
					 utente2.put("ruolo","standard");
					 utente2.put("type","test");
					 
					 database.getCollection("utenti").insertMany(Arrays.asList(utente1,utente2));
					 
					 film1=new Document();
					 film1.put("_id",new ObjectId());
					 film1.put("nome","Film Test1");
					 film1.put("anno",2020);
					 film1.put("tramaCompleta","");
					 film1.put("genere",Arrays.asList("Commedia","Drammatico"));
					 film1.put("paesi_prod",Arrays.asList("Italia"));
					 film1.put("durata_min",120);
					 film1.put("data_uscita",new Date());
					 film1.put("type","test");
					 
					 film2= new Document();
					 film2.put("_id",new ObjectId());
					 film2.put("nome","Film Test2");
					 film2.put("anno",2019);
					 film2.put("tramaCompleta","");
					 film2.put("genere",Arrays.asList("Commedia","Drammatico"));
					 film2.put("paesi_prod",Arrays.asList("Italia"));
					 film2.put("durata_min",105);
					 film2.put("data_uscita",new Date());
					 film2.put("type","test");
					 
					 System.out.println("Inserimento documenti di Test..");
					 database.getCollection("film").insertMany(Arrays.asList(film1,film2));
					 
					 rec1=new Document();
					 rec1.put("voto",6.0);
					 rec1.put("commento","Commento di Test");
					 rec1.put("id_utente",utente1.getObjectId("_id"));
					 rec1.put("username","utente_test1");
					 rec1.put("id_film",film1.getObjectId("_id"));
					 rec1.put("type","test");
			
					 rec2=new Document();
					 rec2.put("voto",5.0);
					 rec2.put("commento","Commento di Test2");
					 rec2.put("id_utente",utente1.getObjectId("_id"));
					 rec2.put("username","utente_test1");
					 rec2.put("id_film",film2.getObjectId("_id"));
					 rec2.put("type","test");
					 
					 
					 rec3=new Document();
					 rec3.put("voto",4.5);
					 rec3.put("commento","Commento di Test3");
					 rec3.put("id_utente",utente2.getObjectId("_id"));
					 rec3.put("username","utente_test2");
					 rec3.put("id_film",film1.getObjectId("_id"));
					 rec3.put("type","test");
					 
					 rec4=new Document();
					 rec4.put("voto",7);
					 rec4.put("commento","Commento di Test3");
					 rec4.put("id_utente",utente2.getObjectId("_id"));
					 rec4.put("username","utente_test2");
					 rec4.put("id_film",film2.getObjectId("_id"));
					 rec4.put("type","test");
					 
					 database.getCollection("recensioni").insertMany(Arrays.asList(rec1,rec2,rec3,rec4));
					 
					 System.out.println("Documenti di test INSERITI con successo \n ");
					 
				}
		 }
		 catch (Exception e) {
			System.err.println(e);
			System.out.println("TEST ARRESTATI.. \n\n");
		}
	 }
	 
	@Test
	public void testDiEsempio() {
			System.out.println("Test di esempio ");
	        assertEquals(3, 3);
	} 
	 
	

	@Test
	/**
	 * Verifico la correttezza della query controllando media e num_recensioni di un film.
	 */
	public void testAggiornaMediaFilm() {
		
		GestoreRisorse.aggiornaMediaFilm(film1.getObjectId("_id"));
		
		BasicDBObject selectQuery = new BasicDBObject();
		selectQuery.append("_id", film1.getObjectId("_id"));
		FindIterable<Document> ris = database.getCollection("film").find(selectQuery);
		
		assertNotNull(ris);
		assertTrue(ris.cursor().hasNext());
		Document d=ris.cursor().next();
		assertEquals("5.25", d.getDouble("media_voto").toString());
		assertEquals("2", d.getInteger("num_recensioni").toString());
		
	}
	
	@Test
	/**
	 * Verifico la presenza di un utente già inserito.
	 */
	public void testRicercaUtente() {
		
		Utente ris = GestoreRisorse.ricercaUtente(utente1.getString("username"), utente1.getString("password"));
		assertNotNull(ris);
		assertEquals(utente1.getObjectId("_id"), ris.getIdUtente());
		assertEquals("utente_test1", ris.getUsername());
		assertEquals("standard",ris.getRuolo());
	}
	
	@Test
	/**
	 * Controllo che vengano restituiti N film e che siano tutti dello stesso anno.
	 */
	public void testCaricaUltimiNFilm() {
		
		int curr_year = Calendar.getInstance().get(Calendar.YEAR);
		
		List<Film> lista1 = GestoreRisorse.caricaUltimiNFilm(5);
		int i=0;
		
		for (Iterator<Film> iterator = lista1.iterator(); iterator.hasNext();) {
			Film film = (Film) iterator.next();
			if (film.getAnno()==curr_year) {
				i++;
			}
		}
		
		assertEquals(5, lista1.size());
		assertEquals(5, i);
		
	}
	
	@Test
	/**
	 * Testo l'inserimento semplice di un film. 
	 * Controllo che sia presente effettivamente effettuando una find.
	 */
	public void testInserisciFilm() {
		 System.out.println("Test inserimento Film ");
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestI_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 GestoreRisorse.inserisciFilm(film);
		 
		 BasicDBObject selectQuery = new BasicDBObject();
		 selectQuery.append("_id", film.getObjectId("_id"));
		 FindIterable<Document> ris = database.getCollection("film").find(selectQuery);
		 
		 assertNotNull(ris);
		 assertTrue(ris.cursor().hasNext());
	}
	
	@Test
	/**
	 * Controllo l'aggiornamento di un Film.
	 * Si effettua una find e un controllo sui valori modificati.
	 */
	public void testAggiornaFilm() {
		 System.out.println("Test aggiornamento film ");
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestA_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		 
		 film.put("nome", "Film_TestA_2");
		 film.put("durata_min",100);
		 
		 GestoreRisorse.aggiornaFilm(film.getObjectId("_id"), film);
		 
		 BasicDBObject selectQuery = new BasicDBObject();
		 selectQuery.append("_id", film.getObjectId("_id"));
		 FindIterable<Document> ris = database.getCollection("film").find(selectQuery);
		 
		 assertNotNull(ris);
		 assertTrue(ris.cursor().hasNext());
		 Document dbObject=ris.cursor().next();
		 
		 assertNotNull(dbObject);
		 assertEquals("Film_TestA_2", dbObject.getString("nome"));
		 assertEquals(100, dbObject.getInteger("durata_min").intValue());
		
	}
	
	@Test
	/**
	 * Controllo l'eliminazione di un Film.
	 * Inserimento di film fittizio e rimozione dalla collection. Verifica assenza.
	 */
	public void testRimuoviFilm() {
		 System.out.println("Test rimozione Film ");
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestR_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		 
		 GestoreRisorse.rimuoviFilm(film.getObjectId("_id"));
		 
		 BasicDBObject selectQuery = new BasicDBObject();
		 selectQuery.append("_id", film.getObjectId("_id"));
		 FindIterable<Document> ris = database.getCollection("film").find(selectQuery);
		 
		 assertNotNull(ris);
		 assertFalse(ris.cursor().hasNext());
	}
	
	
	@Test
	/**
	 * Ricerca film con nome 'Film Test2' durata inferiore a 110 min e anno 2019.
	 */
	public void testEffettuaQueryRicercaFilm_Test1() {
		
		BasicDBObject selectQuery = new BasicDBObject();
		
		selectQuery.append("$text", new BasicDBObject("$search", "\"" + "Film Test2" + "\""));
		selectQuery.append("anno", new BasicDBObject("$eq", 2019));
		selectQuery.append("durata_min", new BasicDBObject("$lt", 110));
		
		
		List<Film> lista = GestoreRisorse.effettuaQueryRicercaFilm(selectQuery);
		
		assertNotNull(lista);
		assertEquals(1, lista.size());
		assertEquals("Film Test2", lista.get(0).getNomeFilm());
		assertEquals(2019, lista.get(0).getAnno().intValue());
		assertEquals(105, lista.get(0).getDurata().intValue());
		
	}
	
	@Test
	/**
	 * Testo l'inserimento semplice di una Recensione. 
	 * Controllo che sia presente effettivamente effettuando una find.
	 * Controllo che venga aggiornato anche il num_recensioni e media_voto sul documento film.
	 */
	public void testInserisciRecensione() {
		
		 System.out.println("Test inserimento Recensione ");
		 
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestIR_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		 
		 Document rec=new Document();
		 rec.put("_id",new ObjectId());
		 rec.put("voto",6.0);
		 rec.put("commento","Commento di Test_IR_1");
		 rec.put("id_utente",utente1.getObjectId("_id"));
		 rec.put("username","utente_test1");
		 rec.put("id_film",film.getObjectId("_id"));
		 rec.put("type","test");
		 
		 GestoreRisorse.inserisciRecensione(rec);
		 
		 BasicDBObject selectQuery = new BasicDBObject();
		 selectQuery.append("_id", rec.getObjectId("_id"));
		 FindIterable<Document> ris = database.getCollection("recensioni").find(selectQuery);
		 
		 assertNotNull(ris);
		 assertTrue(ris.cursor().hasNext());
		 
		 BasicDBObject selectQuery2 = new BasicDBObject();
		 selectQuery2.append("_id", rec.getObjectId("id_film"));
		 
		 FindIterable<Document> ris2 = database.getCollection("film").find(selectQuery2);
		 
		 assertNotNull(ris2);
		 assertTrue(ris2.cursor().hasNext());
		
		 Document dbObject=ris2.cursor().next();
		
		 assertEquals("1", dbObject.get("num_recensioni").toString());
		 assertEquals("6.0", dbObject.get("media_voto").toString());
 
	}
	
	@Test 
	/**
	 * Controllo l'aggiornamento di una Recensione. 
	 * Controllo effettivo cambiamento valori voto e commento.
	 * Controllo cambio num_recensioni e media_voto nel film associato.
	 */
	public void testAggiornaRecensione() {
		System.out.println("Test aggiornamento Recensione ");
		
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestIR_2");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		
		
		 Document rec=new Document();
		 rec.put("_id",new ObjectId());
		 rec.put("voto",6.0);
		 rec.put("commento","Commento di TestIR_2");
		 rec.put("id_utente",utente1.getObjectId("_id"));
		 rec.put("username","utente_test1");
		 rec.put("id_film",film.getObjectId("_id"));
		 rec.put("type","test");
		 
		 database.getCollection("recensioni").insertOne(rec);
		 
		 Document change= new Document();
		 change.put("num_recensioni",1);
		 change.put("media_voto", 6.0);
		 
		 BasicDBObject updateDocument = new BasicDBObject();
		 updateDocument.append("$set", change);
		 GestoreRisorse.getDatabase().getCollection("film").updateOne(Filters.eq("_id", film.getObjectId("_id")), updateDocument);
		 
		 // applico modifiche e richiamo il metodo
		 rec.put("voto", 4.5);
		 rec.put("commento","CommentoModificato");
		 GestoreRisorse.aggiornaRecensione(rec.getObjectId("_id"), rec, rec.getObjectId("id_film"));
		 
		 BasicDBObject selectQuery1 = new BasicDBObject();
		 selectQuery1.append("_id", rec.getObjectId("_id"));
		 FindIterable<Document> ris1 = database.getCollection("recensioni").find(selectQuery1);
		
		 assertNotNull(ris1);
		 assertTrue(ris1.cursor().hasNext());
		 Document d=ris1.cursor().next();
		 assertEquals("4.5", d.getDouble("voto").toString());
		 assertEquals("CommentoModificato", d.getString("commento"));
		 
		 BasicDBObject selectQuery2 = new BasicDBObject();
		 selectQuery2.append("_id", rec.getObjectId("id_film"));
		 FindIterable<Document> ris2 = database.getCollection("film").find(selectQuery2);
		 
		 Document d2=ris2.cursor().next();
		 assertEquals("4.5", d2.getDouble("media_voto").toString());
		 assertEquals("1", d2.getInteger("num_recensioni").toString());
	}
	
	@Test
	/**
	 * Controllo la rimozione di una recensione.
	 * Controllo l'assenza nella collection Recensioni.
	 * Controlo l'aggiornamento dei valori media_voto e num_recensioni
	 */
	public void testRimuoviRecensione() {
		
		 System.out.println("Test rimuovi Recensione ");
		
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestIR_3");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		
		
		 Document rec=new Document();
		 rec.put("_id",new ObjectId());
		 rec.put("voto",6.0);
		 rec.put("commento","Commento di TestIR_3");
		 rec.put("id_utente",utente1.getObjectId("_id"));
		 rec.put("username","utente_test1");
		 rec.put("id_film",film.getObjectId("_id"));
		 rec.put("type","test");
		 
		 database.getCollection("recensioni").insertOne(rec);
		 
		 Document change= new Document();
		 change.put("num_recensioni",1);
		 change.put("media_voto", 6.0);
		 
		 BasicDBObject updateDocument = new BasicDBObject();
		 updateDocument.append("$set", change);
		 GestoreRisorse.getDatabase().getCollection("film").updateOne(Filters.eq("_id", film.getObjectId("_id")), updateDocument);
		 
		 GestoreRisorse.rimuoviRecensione(rec.getObjectId("_id"), rec.getObjectId("id_film"));
		 
		 BasicDBObject selectQuery1 = new BasicDBObject();
		 selectQuery1.append("_id", rec.getObjectId("_id"));
		 FindIterable<Document> ris1 = database.getCollection("recensioni").find(selectQuery1);
		
		 assertNotNull(ris1);
		 assertFalse(ris1.cursor().hasNext());
		 
		 BasicDBObject selectQuery2 = new BasicDBObject();
		 selectQuery2.append("_id", rec.getObjectId("id_film"));
		 FindIterable<Document> ris2 = database.getCollection("film").find(selectQuery2);
		 
		 Document d2=ris2.cursor().next();
		 assertEquals("0.0", d2.getDouble("media_voto").toString());
		 assertEquals("0", d2.getInteger("num_recensioni").toString());
		
	}
	
	
	@Test
	/**
	 * Ricerco tutte le recensioni associate ad un film.
	 * Controllo che il numero di recensioni sia lo stesso associato a Film1.
	 */
	public void testEffettuaQueryRicercaRecensioni() {
		System.out.println("Test ricerca recensioni ");
		BasicDBObject selectQuery = new BasicDBObject();
		
		selectQuery.append("id_film",film1.getObjectId("_id"));

		
		List<Recensione> lista = GestoreRisorse.effettuaQueryRicercaRecensioni(selectQuery);
		assertNotNull(lista);
		assertEquals(2, lista.size());
		
	}
	
	
	@Test
	/**
	 * Test statistiche nr.1.
	 * Creo ed inserisco un documento fittizio film con numero molto grande di recensioni e media voto piu alta di tutti 
	 * e controllo che venga visualizzato per primo.
	 */
	public void testStatisticheMostReviewed() {
		 System.out.println("Test statistiche MostReviewed ");
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestStat_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 long num=database.getCollection("recensioni").countDocuments()+1;
		 film.put("num_recensioni",num);
		 film.put("media_voto",11.0);
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		 List<Statistica> lista = GestoreRisorse.statisticheMostReviewed();
		 
		 assertNotNull(lista);
		 assertEquals(num, lista.get(0).getNumeroRecensioni().longValue());
		 assertEquals("11.0", lista.get(0).getMediaVoto().toString());
	}
	
	@Test
	/**
	 * Test statistiche nr.2.
	 * Creo ed inserisco un documento fittizio film con numero molto grande di recensioni e media voto piu alta di tutti 
	 * e controllo che venga visualizzato come miglior film del suo anno.
	 */
	public void testStatisticheBestByYear() {
		 System.out.println("Test statistiche BestByYear ");
		 Document film= new Document();
		 film.put("_id",new ObjectId());
		 film.put("nome","Film TestStat_1");
		 film.put("anno",2020);
		 film.put("tramaCompleta","");
		 film.put("genere",Arrays.asList("Commedia","Drammatico"));
		 film.put("paesi_prod",Arrays.asList("Italia"));
		 film.put("durata_min",120);
		 film.put("data_uscita",new Date());
		 long num=database.getCollection("recensioni").countDocuments()+1;
		 film.put("num_recensioni",num);
		 film.put("media_voto",11.0);
		 film.put("type","test");
		 
		 database.getCollection("film").insertOne(film);
		 
		 List<Statistica> lista = GestoreRisorse.statisticheBestByYear();
		 assertNotNull(lista);
		 
		 for (Iterator<Statistica> iterator = lista.iterator(); iterator.hasNext();) {
			Statistica statistica = (Statistica) iterator.next();
			if (statistica.getAnno()==2020) {
				assertEquals(num, statistica.getNumeroRecensioni().longValue());
				assertEquals("11.0", statistica.getMediaVoto().toString());
			}
		}
	}
	
	@Test
	/**
	 * Test Statistiche nr.3
	 * Controllo per l'utente 2 fittizio creato che il num recensioni sia giusto.
	 */
	public void testStatisticheDetailReviews() {
		System.out.println("Test statistiche DetailReviews ");
		List<Statistica> lista = GestoreRisorse.statisticheDetailReviews();
		boolean presente=false;
		
		for (Iterator<Statistica> iterator = lista.iterator(); iterator.hasNext();) {
			Statistica statistica = (Statistica) iterator.next();
			
			if (statistica.getUtente().equals("utente_test2")) {
				presente=true;
				assertEquals(2, statistica.getNumeroRecensioni().longValue());
				assertEquals(1, statistica.getNumRecPositive().longValue());
				assertEquals(1, statistica.getNumRecPositive().longValue());
				assertEquals("5.75", statistica.getMediaVoto().toString());
			}
		}
		assertTrue(presente);
	}
	
	

	@AfterClass
	public  static void testExit() {
		
		database.getCollection("utenti").deleteMany(Filters.eq("type", "test"));
		database.getCollection("film").deleteMany(Filters.eq("type", "test"));
		database.getCollection("recensioni").deleteMany(Filters.eq("type", "test"));
		
		
		System.out.println("Documenti di test ELIMINATI con successo");
		mongoClient.close();
		System.out.println("Chiusura dei TEST \n\n");
	}
	
	
}
