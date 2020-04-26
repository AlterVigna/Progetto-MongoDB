package test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import application.GestoreRisorse;

public class TestConsistenza {

	private static MongoClient mongoClient;
	private static MongoDatabase database;
	
	@BeforeClass
	public static void setup() {
		 System.out.println("Inizio TEST di Gestore Risorse ... \n");
		 try {
			 	mongoClient=MongoClients.create(GestoreRisorse.INDIRIZZO_DATABASE);
				database=GestoreRisorse.getMongoClient().getDatabase(GestoreRisorse.NOME_DATABASE); 
				
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
	public void testInserimentoMilioneRecensioni() {
			System.out.println("Test inserimento film test con un milione di recensioni ");
			
			 Document utente1 = new Document();
			 utente1.put("_id",new ObjectId());
			 utente1.put("username","utente_test1");
			 utente1.put("password","test");
			 utente1.put("ruolo","standard");
			 utente1.put("type","test");
			
			 database.getCollection("utenti").insertOne(utente1);
			
			 Document film1 = new Document();
			 film1.put("_id",new ObjectId());
			 film1.put("nome","Film Test1");
			 film1.put("anno",2020);
			 film1.put("tramaCompleta","");
			 film1.put("genere",Arrays.asList("Commedia","Drammatico"));
			 film1.put("paesi_prod",Arrays.asList("Italia"));
			 film1.put("durata_min",120);
			 film1.put("data_uscita",new Date());
			 film1.put("type","test");
			 
			GestoreRisorse.getDatabase().getCollection("film").insertOne(film1);
			System.out.println("Film inserito con successo");
			
			long num=1000000;
			for (long i = 0; i <num; i++) {
				
				 Document rec=new Document();
				 rec.put("_id",new ObjectId());
				 rec.put("voto",6.0);
				 rec.put("commento","Testo recensione"+i);
				 rec.put("username","utente_test1");
				 rec.put("id_utente",utente1.getObjectId("_id"));
				 rec.put("id_film",film1.getObjectId("_id"));
				 rec.put("type","test");
				
				 database.getCollection("recensioni").insertOne(rec);
				 System.out.println("Recensione inserita con successo");
				 
			}
			 
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
