package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bson.types.ObjectId;

import container.RigaRecensioni;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Classe per la mostra delle recensioni effettuate da un Utente.
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

	private static final ObservableList<RigaRecensioni> listaRecensioni = FXCollections.observableArrayList();

	public MostraRecensioniController() {
	}

	@FXML
	public void initialize() {

		listaRecensioni.clear();
		colOpzioni.setCellValueFactory(new PropertyValueFactory<>("opzioni"));
		colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
		colVoto.setCellValueFactory(new PropertyValueFactory<>("voto"));
		colCommento.setCellValueFactory(new PropertyValueFactory<>("recensione"));

		for (TableColumn<RigaRecensioni, ?> column : tabellaRecensioni.getColumns()) {
			addTooltipToColumnCells(column);
		}
		tabellaRecensioni.setItems(listaRecensioni);
	}

	/**
	 * Metodo per l'aggiornamento di una riga all'interno della lista delle
	 * recensioni. Se la recensione non si trova all'interno della lista viene
	 * aggiunta in fondo agli altri elementi.
	 * 
	 * @param riga La riga che viene aggiornata o aggiunta.
	 */
	public static void aggiornaRigaAVideo(RigaRecensioni riga) {
		ObjectId idRecensione = riga.getModel().getIdRecensione();
		List<RigaRecensioni> listaVideo = new ArrayList<RigaRecensioni>();

		for (Iterator<RigaRecensioni> iterator = listaRecensioni.iterator(); iterator.hasNext();) {
			RigaRecensioni rigaVideo = (RigaRecensioni) iterator.next();

			if (rigaVideo.getModel().getIdRecensione().equals(idRecensione)) {

				rigaVideo.setRecensione(riga.getRecensione());
				rigaVideo.setVoto(riga.getVoto());

				listaVideo.add(rigaVideo);
			} else {
				listaVideo.add(rigaVideo);
			}
		}

		listaRecensioni.clear();
		listaRecensioni.addAll(listaVideo);
	}
	
	/**
	 * Metodo per la rimozione di una riga all'interno della lista dei film.
	 * 
	 * @param idRecensione L'id della recensioni nella riga che deve essere rimossa.
	 */
	public static void rimuoviRigaAVideo(ObjectId idRecensione) {
		for (ListIterator<RigaRecensioni> iterator = listaRecensioni.listIterator(); iterator.hasNext();) {
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

	public ObservableList<RigaRecensioni> getListaRecensioni() {
		return listaRecensioni;
	}

}
