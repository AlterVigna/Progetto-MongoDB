package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bson.types.ObjectId;

import application.GestoreRisorse;
import container.RigaRecensioni;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Recensione;

/**
 * Classe per la mostra delle recensioni effettuate da un Utente.
 * 
 * @author Davide Vigna
 *
 */
public class MostraRecensioniController {

	@FXML
	private TableColumn<?, ?> colOpzioni;

	@FXML
	private TableColumn<?, ?> colUtente;

	@FXML
	private TableColumn<?, ?> colVoto;

	@FXML
	private TableColumn<?, ?> colCommento;

	@FXML
	public TableView<RigaRecensioni> tabellaRecensioni;

	private static final List<Recensione> listaRecensioniCompleta = new ArrayList<Recensione>();
	private static final ObservableList<RigaRecensioni> listaRecensioniAVideo = FXCollections.observableArrayList();

	private int start = 0;
	private int inc = GestoreRisorse.NUM_RIGHE_A_VIDEO;
	private int end = inc;

	public MostraRecensioniController() {
	}

	@FXML
	public void initialize() {

		listaRecensioniAVideo.clear();
		colOpzioni.setCellValueFactory(new PropertyValueFactory<>("opzioni"));
		colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
		colVoto.setCellValueFactory(new PropertyValueFactory<>("voto"));
		colCommento.setCellValueFactory(new PropertyValueFactory<>("recensione"));

		for (TableColumn<RigaRecensioni, ?> column : tabellaRecensioni.getColumns()) {
			addTooltipToColumnCells(column);
		}
		tabellaRecensioni.setItems(listaRecensioniAVideo);
	}

	/**
	 * Metodo per l'aggiornamento di una riga all'interno della lista delle
	 * recensioni. Se la recensione non si trova all'interno della lista viene
	 * aggiunta in fondo agli altri elementi. Viene fatto un controllo prima sulla
	 * lista delle recensioni "nascosta", nel caso non sia presente viene aggiunta
	 * in fondo. Poi viene controllato se in quel momento è presente nella lista delle
	 * film visualizzati a video, nel caso lo aggiorna.
	 * 
	 * @param riga La riga che viene aggiornata o aggiunta.
	 */
	public static void aggiornaRigaAVideo(RigaRecensioni riga) {
		ObjectId idRecensione = riga.getModel().getIdRecensione();

		List<Recensione> listaCompleta = new ArrayList<Recensione>();
		List<RigaRecensioni> listaVideo = new ArrayList<RigaRecensioni>();
		boolean trovato = false;

		for (Iterator<Recensione> iterator = listaRecensioniCompleta.iterator(); iterator.hasNext();) {
			Recensione rec = (Recensione) iterator.next();

			if (rec.getIdRecensione().equals(idRecensione)) {
				Recensione r = new Recensione();

				r.setIdFilm(rec.getIdFilm());
				r.setIdRecensione(rec.getIdRecensione());
				r.setIdUtente(r.getIdUtente());
				r.setNomeFilm(r.getNomeFilm());
				r.setRecensione(riga.getRecensione());
				r.setVoto(riga.getVoto());
				listaCompleta.add(r);
				trovato = true;
			} else {
				listaCompleta.add(rec);
			}
		}
		if (!trovato)
			listaCompleta.add(riga.getModel());

		listaRecensioniCompleta.clear();
		listaRecensioniCompleta.addAll(listaCompleta);

		for (Iterator<RigaRecensioni> iterator = listaRecensioniAVideo.iterator(); iterator.hasNext();) {
			RigaRecensioni rigaVideo = (RigaRecensioni) iterator.next();

			if (rigaVideo.getModel().getIdRecensione().equals(idRecensione)) {

				rigaVideo.setRecensione(riga.getRecensione());
				rigaVideo.setVoto(riga.getVoto());

				listaVideo.add(rigaVideo);
			} else {
				listaVideo.add(rigaVideo);
			}
		}

		listaRecensioniAVideo.clear();
		listaRecensioniAVideo.addAll(listaVideo);
	}

	/**
	 * Metodo per la rimozione di una riga all'interno della lista dei film.
	 * 
	 * @param idRecensione L'id della recensioni nella riga che deve essere rimossa.
	 */
	public static void rimuoviRigaAVideo(ObjectId idRecensione) {
		for (ListIterator<RigaRecensioni> iterator = listaRecensioniAVideo.listIterator(); iterator.hasNext();) {
			RigaRecensioni rigaVideo = (RigaRecensioni) iterator.next();
			if (rigaVideo.getModel().getIdRecensione().equals(idRecensione)) {
				iterator.remove();
				break;
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
	private <T> void addTooltipToColumnCells(TableColumn<RigaRecensioni, T> column) {

		Callback<TableColumn<RigaRecensioni, T>, TableCell<RigaRecensioni, T>> existingCellFactory = column
				.getCellFactory();

		column.setCellFactory(c -> {

			TableCell<RigaRecensioni, T> cell = existingCellFactory.call(c);
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

	/**
	 * Metodo per la gestione dello scroll di tutte le righe nella tabella delle
	 * recensioni. Questo metodo è stato introdotto per ridurre i tempi di attesa e
	 * soprattutto l'utilizzo di memoria, per il popolamento e visualizzazione di un
	 * numero elevato di righe a video. Ad ogni scroll viene visualizzata una
	 * sottolista di record di listaFilmCompleta.
	 */
	public void initScrollTable(List<Recensione> lista, String nomeFilm) {

		if (end < lista.size()) {
			listaRecensioniAVideo.addAll(RigaRecensioni.ottieniListaRighe(lista.subList(0, end), nomeFilm));
		} else
			listaRecensioniAVideo.addAll(RigaRecensioni.ottieniListaRighe(lista.subList(0, lista.size()), nomeFilm));

		start = end;
		end += inc;

		ScrollBar tableViewScrollBar = getTableViewScrollBar(tabellaRecensioni);
		tableViewScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double position = newValue.doubleValue();
				ScrollBar scrollbar = getTableViewScrollBar(tabellaRecensioni);

				if (position == scrollbar.getMax()) {
					if (end < listaRecensioniCompleta.size()) {

						List<Recensione> sottoLista = listaRecensioniCompleta.subList(start, end);
						listaRecensioniAVideo.addAll(RigaRecensioni.ottieniListaRighe(sottoLista, nomeFilm));
						start = end;
						end += inc;
						tabellaRecensioni.scrollTo(start);
					} else {
						if (start < end) {
							List<Recensione> sottoLista = listaRecensioniCompleta.subList(start,
									listaRecensioniCompleta.size());
							listaRecensioniAVideo.addAll(RigaRecensioni.ottieniListaRighe(sottoLista, nomeFilm));
							start = end;
							end = listaRecensioniCompleta.size();
							tabellaRecensioni.scrollTo(start);
						}
					}
				}
			}
		});
	}

	public static ObservableList<RigaRecensioni> getListarecensioniavideo() {
		return listaRecensioniAVideo;
	}

	public static List<Recensione> getListarecensionicompleta() {
		return listaRecensioniCompleta;
	}

}
