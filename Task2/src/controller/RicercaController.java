package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

import application.GestoreRisorse;
import container.RigaFilm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Film;
import model.Recensione;
import model.Statistica;

/**
 * Classe per la ricerca e visualizzazione dei film. Gestione delle statistiche
 * sia per utente Standard che Admin.
 * 
 * @author Davide
 *
 */
public class RicercaController {

	// Campi per le selezioni
	@FXML
	private ComboBox<String> comboGenere;

	@FXML
	private TextField nomeFilmRicercaTF;

	@FXML
	private TextField annoRicercaTF;

	@FXML
	private TextField durataRicercaTF;

	@FXML
	private Label lblUsername;

	@FXML
	private Button btnRicerca;

	@FXML
	private Button btnAggiungi;

	@FXML
	private Button btnLogout;

	// Tabella per la ricerca
	@FXML
	private TableView<RigaFilm> tabellaFilm;

	@FXML
	private TableColumn<?, ?> colOpzioni;

	@FXML
	private TableColumn<?, ?> colNomeFilm;

	@FXML
	private TableColumn<?, ?> colAnno;

	@FXML
	private TableColumn<?, ?> colGenere;

	@FXML
	private TableColumn<?, ?> colDurata;

	@FXML
	private TableColumn<?, ?> colDataUscita;

	@FXML
	private Label lblPag;

	@FXML
	private Label lblStat1;

	@FXML
	private Label lblStat2;

	@FXML
	private Button btnStat1;

	@FXML
	private Button btnStat2;

	private final ObservableList<String> listaGeneri = FXCollections.observableArrayList();

	private final static List<Film> listaFilmCompleta = new ArrayList<Film>();
	private final static ObservableList<RigaFilm> listaFilmAVideo = FXCollections.observableArrayList();

	private int start = 0;
	private int inc = GestoreRisorse.NUM_RIGHE_A_VIDEO;
	private int end = inc;

	private long numDocumentiTrovati;

	public RicercaController() {
		super();
	}

	@FXML
	private void initialize() {

		numDocumentiTrovati = 0;
		setLabelNumeroFilmTrovati();

		initCombo();
		comboGenere.getItems().setAll(listaGeneri);

		colOpzioni.setCellValueFactory(new PropertyValueFactory<>("opzioni"));
		colNomeFilm.setCellValueFactory(new PropertyValueFactory<>("nomeFilm"));
		colAnno.setCellValueFactory(new PropertyValueFactory<>("anno"));
		colGenere.setCellValueFactory(new PropertyValueFactory<>("genere"));
		colDurata.setCellValueFactory(new PropertyValueFactory<>("durata"));
		colDataUscita.setCellValueFactory(new PropertyValueFactory<>("dataUscita"));

		for (TableColumn<RigaFilm, ?> column : tabellaFilm.getColumns()) {
			addTooltipToColumnCells(column);
		}

		tabellaFilm.setItems(listaFilmAVideo);

		btnRicerca.setOnAction((ActionEvent ev) -> {
			ricercaFilm();
		});

		btnLogout.setOnAction((ActionEvent ev) -> {
			gestisciLogout();
		});

		btnAggiungi.setOnAction((ActionEvent ev) -> {
			apriInterfacciaInserisciFilm(null);
		});

	}

	/**
	 * Metodo per l'inizializzazione statica del campo Combobox dei generi film
	 */
	private void initCombo() {
		listaGeneri.add("");
		listaGeneri.add("Commedia");
		listaGeneri.add("Poliziesco");
		listaGeneri.add("Sentimentale");
		listaGeneri.add("Animazione");
		listaGeneri.add("Avventura");
		listaGeneri.add("Drammatico");
		listaGeneri.add("Fantastico");
		listaGeneri.add("Fantasy");
		listaGeneri.add("Thriller");
		listaGeneri.add("Western");
		listaGeneri.add("Fantascienza");
		listaGeneri.add("Giallo");
		listaGeneri.add("Biografico");
		listaGeneri.add("Azione");
		listaGeneri.add("Horror");
		listaGeneri.add("Documentario");
	}

	/**
	 * Metodo per la gestione dello scroll di tutte le righe nella tabella Film.
	 * Questo metodo è stato introdotto per ridurre i tempi di attesa e soprattutto
	 * l'utilizzo di memoria, per il popolamento e visualizzazione di un numero
	 * elevato di righe a video. Ad ogni scroll viene visualizzata una sottolista di
	 * record di listaFilmCompleta.
	 */
	public void initScrollTable() {

		ScrollBar tableViewScrollBar = getTableViewScrollBar(tabellaFilm);
		tableViewScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double position = newValue.doubleValue();
				ScrollBar scrollbar = getTableViewScrollBar(tabellaFilm);

				if (position == scrollbar.getMax()) {
					if (end <= listaFilmCompleta.size()) {

						List<Film> sottoLista = listaFilmCompleta.subList(start, end);
						listaFilmAVideo.addAll(RigaFilm.ottieniListaRighe(sottoLista));
						start = end;
						end += inc;
						tabellaFilm.scrollTo(start);
					}
				}
			}
		});
	}

	/**
	 * Metodo per l'impostazione di un filtro di ricerca da applicare alla query.
	 * Controlla la presenza e la validità dei campi inseriti per la ricerca.
	 * 
	 * @return Filtro contenente le informazioni per effettuare la query
	 *         dettagliata.
	 */
	private BasicDBObject filtraRisultati() {

		BasicDBObject selectQuery = new BasicDBObject();
		if (!nomeFilmRicercaTF.getText().trim().equals("")) {
			// selectQuery.append("nome", new
			// BasicDBObject("$regex",".*"+nomeFilmRicercaTF.getText()+".*"));
			selectQuery.append("$text", new BasicDBObject("$search", "\"" + nomeFilmRicercaTF.getText() + "\""));
		}

		if (comboGenere.getSelectionModel().getSelectedItem() != null
				&& !comboGenere.getSelectionModel().getSelectedItem().equals("")) {
			selectQuery.append("genere", comboGenere.getSelectionModel().getSelectedItem());
		}

		if (!annoRicercaTF.getText().trim().equals("")) {
			try {
				int anno = Integer.parseInt(annoRicercaTF.getText().trim());
				selectQuery.append("anno", new BasicDBObject("$eq", anno));
			} catch (NumberFormatException ex) {
				System.err.println("Formato anno errato");
			}
		}

		if (!durataRicercaTF.getText().trim().equals("")) {
			try {
				int durata = Integer.parseInt(durataRicercaTF.getText().trim());
				selectQuery.append("durata_min", new BasicDBObject("$lt", durata));
			} catch (NumberFormatException ex) {
				System.err.println("Formato durata errato");
			}
		}
		return selectQuery;
	}

	/**
	 * Metodo per la ricerca effettiva dei film. Chiama una funzione per la
	 * preparazione del filtro di ricerca, una query per il conteggio dei film, e
	 * una query per la ricerca dei film. Al termine incapsula i risultati in
	 * container RigaFilm li mostra a video.
	 */
	private void ricercaFilm() {

		BasicDBObject selectQuery = filtraRisultati();

		listaFilmCompleta.clear();
		listaFilmAVideo.clear();
		
		List<Film> lista = GestoreRisorse.effettuaQueryRicercaFilm(selectQuery);
		listaFilmCompleta.addAll(lista);
		
		if (end<lista.size()) listaFilmAVideo.addAll(RigaFilm.ottieniListaRighe(lista.subList(0, end)));
		else listaFilmAVideo.addAll(RigaFilm.ottieniListaRighe(lista.subList(0, lista.size())));
		
		long numDoc = lista.size();
		setNumDocumentiTrovati(numDoc);
		setLabelNumeroFilmTrovati();
	}

	/**
	 * Effettua il logout rimandando all'interfaccia di autenticazione.
	 */
	private void gestisciLogout() {
		Stage stage = (Stage) btnLogout.getScene().getWindow();
		stage.close();
		apriInterfacciaLogin(stage);
	}

	/**
	 * Effttua l'eliminazione di un film, sia sull'unità persistente che nella
	 * tabella a video.
	 * 
	 * @param idFilm L'id del film che deve essere cancellato.
	 */
	public static void rimuoviFilm(ObjectId idFilm) {

		GestoreRisorse.rimuoviFilm(idFilm);
		RicercaController.rimuoviRigaVideo(idFilm);
	}

	// Metodi per l'apertura di nuove interfacce

	/**
	 * Implementa l'apertura di una nuova interfaccia per l'inserimento o la
	 * modifica di un film.
	 * 
	 * @param idFilm Indica l'id del film che si vuole modificare. Se null
	 *               predispone l'interfaccia per inserimento.
	 */
	public static void apriInterfacciaInserisciFilm(ObjectId idFilm) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(RicercaController.class.getResource("/views/ModificaFilmView.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			ModificaFilmController controller = fxmlLoader.<ModificaFilmController>getController();
			if (idFilm == null) {
				controller.setLblTitoloOp("Inserimento Nuovo Film");
				controller.setBtnInserisciModifica("Inserisci");
			} else {
				BasicDBObject selectQuery = new BasicDBObject();
				selectQuery.append("_id", idFilm);

				List<Film> risultati = GestoreRisorse.effettuaQueryRicercaFilm(selectQuery);

				if (!risultati.isEmpty()) {
					Film film = risultati.get(0);

					controller.setIdFilm(idFilm);
					if (film.getNomeFilm() != null) {
						controller.getTitoloTF().setText(film.getNomeFilm());
					}
					if (film.getAnno() != null) {
						controller.getAnnoTF().setText(film.getAnno() + "");
					}

					if (film.getDurata() != null) {
						controller.getDurataTF().setText(film.getDurata() + "");
					}
					if (controller.getDataUscitaDP() != null) {
						controller.getDataUscitaDP().setValue(film.getDataUscita());
					}
					if (controller.getTramaCompletaTA() != null) {
						controller.getTramaCompletaTA().setText(film.getTramaCompleta());
					}

					if (film.getGenere() != null && film.getGenere().size() > 0) {
						controller.getComboGenere().setValue(film.getGenere().get(0));
					}

					if (film.getPaesiProd() != null && film.getPaesiProd().size() > 0) {
						controller.getListaPaesi().addAll(film.getPaesiProd());
						controller.setPaesiProdTF();
					}
				} else
					return;

				controller.setBtnInserisciModifica("Modifica");
				controller.setLblTitoloOp("Modifica Film");
			}

			Scene scene = new Scene(root, 600, 700);
			Stage nuovo = new Stage();
			nuovo.setScene(scene);
			nuovo.setTitle("Let's Movie - Inserisci/Modifica Film");
			nuovo.setMaximized(false);
			nuovo.setResizable(false);
			nuovo.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementa l'apertura dell' interfaccia di autenticazione, chiudendo quella
	 * corrente.
	 * 
	 * @param primaryStage Lo stage che viene modificato.
	 */
	private void apriInterfacciaLogin(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/AutenticazioneView.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root, 850, 600);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Let's Movie - Autenticazione");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementa l'apertura dell'interfaccia di dettaglio film.
	 * 
	 * @param idFilm L'id del film che viene dettagliato.
	 */
	public static void apriInterfacciaDettaglioFilm(ObjectId idFilm) {

		try {

			FXMLLoader fxmlLoader = new FXMLLoader(
					RicercaController.class.getResource("/views/MostraDettaglioFilmView.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			MostraFilmDettaglioController controller = fxmlLoader.<MostraFilmDettaglioController>getController();

			BasicDBObject selectQuery = new BasicDBObject();
			selectQuery.append("_id", idFilm);

			List<Film> risultato = GestoreRisorse.effettuaQueryRicercaFilm(selectQuery);
			Film f = null;
			if (!risultato.isEmpty()) {

				f = risultato.get(0);

				controller.setLblTitolo(f.getNomeFilm());
				controller.setLblGenere(f.getTuttiGeneriStringa());
				if (f.getDataUscita() != null)
					controller.setLblDataDiUscita(f.getDataUscita().toString());
				if (f.getDurata() != null)
					controller.setLblDurata(f.getDurata() + " Minuti");
				else
					controller.setLblDurata(" -- Minuti");
				controller.setLblPaesiProduzione(f.getTuttiPaesiProduzioneStringa());
				controller.setTextTramaCompleta(f.getTramaCompleta());
				if (f.getNumRecensioni() != null)
					controller.setLblNumRec(f.getNumRecensioni() + "");
				if (f.getMediaVoto() != null)
					controller.setLblMediaVoto(Math.round(f.getMediaVoto() * 100.0) / 100.0 + "");
				controller.getListaCast().addAll(f.getListaCast());

				Scene scene = new Scene(root, 600, 632);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setMaximized(false);
				stage.setResizable(false);
				stage.setTitle("Dettaglio - " + f.getNomeFilm());
				stage.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementa l'apertura dell'interfaccia di dettaglio recensione, relativa ad
	 * un film. Se utilizzata in modalità MODIFICA_RECENSIONE viene caricata
	 * l'intera recesione. Se utilizzata in modalita AGGIUNGI_RECENSIONE, predispone
	 * l'interfaccia per un nuovo inserimento.
	 * 
	 * 
	 * @param idRecensione L'id della recensione che si va a modificare. Mettere
	 *                     null quando si entra in inserimento.
	 * @param idFilm       L'id del film associato alla recensione che si va a
	 *                     dettagliare.
	 * @param nomeFilm     Il nome del film associato alla recensione.
	 * @param modalita     DettaglioRecensioneController.AGGIUNGI_RECENSIONE per
	 *                     l'aggiunta di una nuova recensione oppure
	 *                     DettaglioRecensioneController.MODIFICA_RECENSIONE per la
	 *                     modifica di una recensione esistente
	 * 
	 */
	public static void apriInterfacciaDettaglioRecensione(ObjectId idRecensione, ObjectId idFilm, String nomeFilm,
			int modalita) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Film.class.getResource("/views/ModificaRecensioneView.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			ModificaRecensioneController controller = fxmlLoader.<ModificaRecensioneController>getController();
			Scene scene = new Scene(root, 600, 632);

			controller.setModalita(modalita);
			controller.setIdFilm(idFilm);

			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setMaximized(false);
			stage.setResizable(false);

			if (modalita == GestoreRisorse.AGGIUNGI_RECENSIONE) {
				stage.setTitle(nomeFilm + " - Aggiungi Recensione");
			} else {
				stage.setTitle(nomeFilm + " - Modifica Recensione");

				BasicDBObject selectQuery = new BasicDBObject();
				selectQuery.append("_id", idRecensione);
				List<Recensione> listRec = GestoreRisorse.effettuaQueryRicercaRecensioni(selectQuery);
				if (!listRec.isEmpty()) {
					controller.impostaRecensione(listRec.get(0));
				} else
					return;
			}
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementa l'apertura dell'interfaccia per la mostra di tutte le recensioni
	 * relative ad un film.
	 * 
	 * @param f Il model del film di cui si vogliono vedere tutte le recensioni
	 *          associate.
	 */
	public static void apriInterfacciaMostraTutteLeRecensioniDiFilm(Film f) {

		try {
			BasicDBObject selectQuery = new BasicDBObject();
			selectQuery.append("id_film", f.getId());

			List<Recensione> listaRecensioni=GestoreRisorse.effettuaQueryRicercaRecensioni(selectQuery);
			
			FXMLLoader fxmlLoader = new FXMLLoader(
					RicercaController.class.getResource("/views/MostraRecensioniView.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			MostraRecensioniController controller = fxmlLoader.<MostraRecensioniController>getController();

			MostraRecensioniController.getListarecensioniavideo().clear();
			MostraRecensioniController.getListarecensionicompleta().clear();
			
			MostraRecensioniController.getListarecensionicompleta().addAll(listaRecensioni);
			Scene scene = new Scene(root, 1450, 600);

			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Mostra tutte recensioni Film -" + f.getNomeFilm());
			stage.show();
			
			controller.initScrollTable(listaRecensioni, f.getNomeFilm());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Implementa l'apertura dell'interfaccia per la mostra delle statistiche.
	 * 
	 * @param lista               La lista dei record che viene mostrata a video.
	 * @param titoloFinestra      Il testo che viene mostrato come titolo della
	 *                            nuova finestra.
	 * @param labelDaVisualizzare Il testo descrittivo della query che viene
	 *                            mostrato nella testata della nuova finestra.
	 * @param tipologia           Distingue i 2 casi Standard e Admin, richiamabili
	 *                            da GestoreRisorse.RUOLO_STANDARD,
	 *                            GestoreRisorse.RUOLO_ADMIN.
	 */
	public static void apriInterfacciaStatistiche(List<Statistica> lista, String titoloFinestra,
			String labelDaVisualizzare, String tipologia) {
		try {

			FXMLLoader fxmlLoader = null;
			if (tipologia.equals(GestoreRisorse.RUOLO_STANDARD)) {
				fxmlLoader = new FXMLLoader(
						RicercaController.class.getResource("/views/StatisticheUtenteStandardView.fxml"));
			} else {
				fxmlLoader = new FXMLLoader(
						RicercaController.class.getResource("/views/StatisticheUtenteAdminView.fxml"));
			}

			Parent root = (Parent) fxmlLoader.load();

			MostraStatisticheController controller = fxmlLoader.<MostraStatisticheController>getController();
			controller.setLblStringaDesQuery(labelDaVisualizzare);
			
			controller.getListaStatisticheCompleta().clear();
			controller.getListaStatistiche().clear();
			
			controller.getListaStatisticheCompleta().addAll(lista);
			
			
			Scene scene = new Scene(root, 850, 600);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle(titoloFinestra);
			stage.show();
			
			controller.initScrollTable(lista);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Richiama la funzione per il calcolo delle statistiche dei film più recensiti
	 * e la funzione per l'apertura dell'interfaccia delle statistiche.
	 */
	public static void apriInterfacciaStatisticheFilmPiuRecensiti() {

		List<Statistica> listaStat = GestoreRisorse.statisticheMostReviewed();

		if (!listaStat.isEmpty()) {

			String titoloFinestra = "Let's Movie - Statistiche - Most Reviewed";
			String labelDaVis = "Lista dei film che hanno ricevuto un numero maggiore di recensioni con durata superiore ai 60 min, con genere diverso da 'Documentario' "
					+ "ordinati per numero di recensioni e media voto";
			apriInterfacciaStatistiche(listaStat, titoloFinestra, labelDaVis, GestoreRisorse.RUOLO_STANDARD);
		}
	}

	/**
	 * Richiama la funzione per il calcolo delle statistiche dei migliori film per
	 * ciascun anno e la funzione per l'apertura dell'interfaccia delle statistiche.
	 */
	public static void apriInterfacciaStatisticheMiglioriPerAnno() {

		List<Statistica> listaStat = GestoreRisorse.statisticheBestByYear();

		if (!listaStat.isEmpty()) {

			String titoloFinestra = "Let's Movie - Statistiche - Best By Year";
			String labelDaVis = "Lista dei film che hanno ricevuto più di 5 recensioni e che hanno ottenuto un valore di media voto  maggiore degli altri "
					+ "per ciascun anno in ordine di visualizzazione per anno crescente";
			apriInterfacciaStatistiche(listaStat, titoloFinestra, labelDaVis, GestoreRisorse.RUOLO_STANDARD);
		}
	}

	/**
	 * Richiama la funzione per il calcolo delle statistiche delle recensioni di
	 * ciascun utente e la funzione per l'apertura dell'interfaccia delle
	 * statistiche.
	 */
	public static void apriInterfacciaStatisticheDettaglioRecensioniPerUtente() {

		List<Statistica> listaStat = GestoreRisorse.statisticheDetailReviews();
		if (!listaStat.isEmpty()) {

			String titoloFinestra = "Let's Movie - Statistiche - Detail Reviews";
			String labelDaVis = "Lista degli utenti che hanno effettuato recensioni, in ordine decrescente per numero di recensioni totali effettuate e media, "
					+ "dettagliando il numero di recensioni positive (voto>=6), numero di quelle negative (voto<6) e media complessiva per quell'utente";
			apriInterfacciaStatistiche(listaStat, titoloFinestra, labelDaVis, GestoreRisorse.RUOLO_ADMIN);
		}
	}

	// Metodi per la gestione della TableView

	/**
	 * Metodo per l'aggiornamento di una riga all'interno della lista dei film. Se
	 * il film non si trova all'interno della lista viene aggiunto in fondo alla
	 * lista. Viene fatto un controllo prima sulla lista dei film "nascosta", nel
	 * caso non sia presente viene aggiunto in fondo. Poi viene controllato se in
	 * quel momento è presente nella lista dei film visualizzati a video, nel caso
	 * lo aggiorna.
	 * 
	 * @param riga La riga che viene aggiornata o aggiunta.
	 */
	public static void aggiornaRigaVideo(RigaFilm riga) {

		List<Film> allFilms = new ArrayList<Film>();
		List<RigaFilm> listaVideo = new ArrayList<RigaFilm>();
		boolean trovato = false;

		for (Iterator<Film> iterator = listaFilmCompleta.iterator(); iterator.hasNext();) {
			Film film = (Film) iterator.next();
			if (film.getId().equals(riga.getModel().getId())) {
				allFilms.add(film);
				trovato = true;
			} else {
				allFilms.add(riga.getModel());
			}
		}
		if (!trovato)
			allFilms.add(riga.getModel());
		
		listaFilmCompleta.clear();
		listaFilmCompleta.addAll(allFilms);
		
		for (Iterator<RigaFilm> iterator = listaFilmAVideo.iterator(); iterator.hasNext();) {
			RigaFilm rigaFilm = (RigaFilm) iterator.next();
			if (rigaFilm.getModel().getId().equals(riga.getModel().getId())) {
				listaVideo.add(riga);

			} else {
				listaVideo.add(rigaFilm);
			}
		}
		if (!trovato) listaVideo.add(new RigaFilm(riga.getModel()));

		listaFilmAVideo.clear();
		listaFilmAVideo.addAll(listaVideo);
	}

	/**
	 * Metodo per la rimozione di una riga all'interno della lista dei film.
	 * Prima controllo ed elimino nella lista dei film totali.
	 * Poi controllo ed elimino se presente nella lista dei film visualizzati in questo momento a video.
	 * 
	 * @param idFilm L'id del film nella riga che deve essere rimosso.
	 */
	public static void rimuoviRigaVideo(ObjectId idFilm) {
		
		for (Iterator<Film> iterator = listaFilmCompleta.listIterator(); iterator.hasNext();) {
			Film film = (Film) iterator.next();
			if (film.getId().equals(idFilm)) iterator.remove();
		}
		
		for (ListIterator<RigaFilm> iterator = listaFilmAVideo.listIterator(); iterator.hasNext();) {
			RigaFilm rigaFilm = (RigaFilm) iterator.next();
			if (rigaFilm.getModel().getId().equals(idFilm)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Metodo per l'aggiunta del tooltip al passaggio del mouse sopra la cella della
	 * TableView. Utile per visualizzare il contenuto di celle con dimensione
	 * inferiore del proprio contenuto.
	 * 
	 * @param column La colonna a cui viene applicato l'effetto del tooltip
	 */
	private <T> void addTooltipToColumnCells(TableColumn<RigaFilm, T> column) {
		Callback<TableColumn<RigaFilm, T>, TableCell<RigaFilm, T>> existingCellFactory = column.getCellFactory();

		column.setCellFactory(c -> {
			TableCell<RigaFilm, T> cell = existingCellFactory.call(c);
			if (c.getId().equals("colOpzioni"))
				return cell;
			Tooltip tooltip = new Tooltip();

			tooltip.textProperty().bind(cell.itemProperty().asString());
			cell.setTooltip(tooltip);
			return cell;
		});
	}

	/**
	 * Metodo per il reperimento della Scrollbar associata alla tabella, per
	 * permettere di abilitare lo scrolling dinamico in caso di gestione molte righe
	 * a video. Variabile start memorizza l'indice di inizio visualizzazione,
	 * variabile inc di quanto incrementare, variabile end l'indice massimo di
	 * visualizzazione.
	 * 
	 * @param listView La tabella di cui si vuole ottenere la scrollbar
	 * @return ScrollBar della tabella inserita come parametro
	 */
	private ScrollBar getTableViewScrollBar(TableView<?> listView) {
		ScrollBar scrollbar = null;
		for (Node node : listView.lookupAll(".scroll-bar")) {
			if (node instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) node;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					scrollbar = bar;
				}
			}
		}
		return scrollbar;
	}

	// Getter e Setter per impostare contenuti componenti grafici
	public void setLblUsername(String text) {
		this.lblUsername.setText(text);
	}

	public long getNumDocumentiTrovati() {
		return numDocumentiTrovati;
	}

	public void setNumDocumentiTrovati(long numDocumentiTrovati) {
		this.numDocumentiTrovati = numDocumentiTrovati;
	}

	public void setLabelNumeroFilmTrovati() {
		lblPag.setText(numDocumentiTrovati + " Film ");
	}

	public Button getBtnAggiungi() {
		return btnAggiungi;
	}

	public void setLblStat1(String lblStat1) {
		this.lblStat1.setText(lblStat1);
	}

	public void setLblStat2(String lblStat2) {
		this.lblStat2.setText(lblStat2);
	}

	public void setBtnStat1(String btnStat1) {
		this.btnStat1.setText(btnStat1);
	}

	public void setBtnStat2(String btnStat2) {
		this.btnStat2.setText(btnStat2);
	}

	public Label getLblStat1() {
		return lblStat1;
	}

	public Label getLblStat2() {
		return lblStat2;
	}

	public Button getBtnStat1() {
		return btnStat1;
	}

	public Button getBtnStat2() {
		return btnStat2;
	}

	public static List<Film> getListafilmcompleta() {
		return listaFilmCompleta;
	}

	public static ObservableList<RigaFilm> getListafilmavideo() {
		return listaFilmAVideo;
	}

}
