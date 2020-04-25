package controller;

import java.util.ArrayList;
import java.util.List;

import application.GestoreRisorse;
import container.RigaStatistiche;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Statistica;


/**
 * Classe per la visualizzazione delle statistiche, sia per utente Standard che Admin.
 * Attributi utilizzati per la tipologia Standard: colAnno,colNomeFilm,colVoto,colNumRec.
 * Attributi utilizzati per la tipologia Admin: colUtente,colTotNumRec,colNumRecPos,colNumRecNeg,colMediaRec.
 * 
 * @author Davide
 *
 */
public class MostraStatisticheController {

	 @FXML
	 private Label lblStringaDesQuery;
	 
	 // Colonne interfaccia utente standard
	 @FXML
	 private TableColumn<?, ?> colAnno;
	 
	 @FXML
	 private TableColumn<?, ?> colNomeFilm;
	 
	 @FXML
	 private TableColumn<?, ?> colVoto;
	
	 @FXML
	 private TableColumn<?, ?> colNumRec;
	 
	 
	 // Colonne interfaccia utente admin

	 @FXML
	 private TableColumn<?, ?> colUtente;
	 
	 @FXML
	 private TableColumn<?, ?> colTotNumRec;
	 
	 @FXML
	 private TableColumn<?, ?> colNumRecPos;
	 
	 @FXML
	 private TableColumn<?, ?> colNumRecNeg;
	 
	 @FXML
	 private TableColumn<?, ?> colMediaRec;
	 
	 
	 @FXML
	 private TableView<RigaStatistiche> tabellaStat;
	 
	 private final List<Statistica> listaStatisticheCompleta= new ArrayList<Statistica>();
	 private final ObservableList<RigaStatistiche> listaStatisticheAVideo=FXCollections.observableArrayList();
	 
	 private int start = 0;
	 private int inc = GestoreRisorse.NUM_RIGHE_A_VIDEO;
	 private int end = inc;
	 
	 
	 public MostraStatisticheController() {
	 }
	 
	 @FXML
	 public void initialize() {
		 
		 // Colonne interfaccia utente standard
		 if(colAnno!=null) colAnno.setCellValueFactory(new PropertyValueFactory<>("anno"));
		 if(colNomeFilm!=null) colNomeFilm.setCellValueFactory(new PropertyValueFactory<>("nomeFilm"));
		 if(colVoto!=null)colVoto.setCellValueFactory(new PropertyValueFactory<>("mediaVoto"));
		 if(colNumRec!=null) colNumRec.setCellValueFactory(new PropertyValueFactory<>("numeroRecensioni"));
		 
		 // Colonne interfaccia utente admin
		 if(colUtente!=null) colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
		 if(colTotNumRec!=null) colTotNumRec.setCellValueFactory(new PropertyValueFactory<>("numeroRecensioni"));
		 if(colNumRecPos!=null) colNumRecPos.setCellValueFactory(new PropertyValueFactory<>("numRecPositive"));
		 if(colNumRecNeg!=null) colNumRecNeg.setCellValueFactory(new PropertyValueFactory<>("numRecNegative"));
		 if(colMediaRec!=null) colMediaRec.setCellValueFactory(new PropertyValueFactory<>("mediaVoto"));
		 
		 tabellaStat.setItems(listaStatisticheAVideo);
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
		 * Metodo per la gestione dello scroll di tutte le righe nella tabella delle statistiche.
		 * Questo metodo è stato introdotto per ridurre i tempi di attesa e soprattutto
		 * l'utilizzo di memoria, per il popolamento e visualizzazione di un numero
		 * elevato di righe a video. Ad ogni scroll viene visualizzata una sottolista di
		 * record di listaFilmCompleta.
		 */
		public void initScrollTable(List<Statistica> lista) {

			if (end<lista.size()) {
				listaStatisticheAVideo.addAll(RigaStatistiche.ottieniListaRighe(lista.subList(0, end)));
			}
			else listaStatisticheAVideo.addAll(RigaStatistiche.ottieniListaRighe(lista.subList(0, lista.size())));
			
			start = end;
			end += inc;
			
			ScrollBar tableViewScrollBar = getTableViewScrollBar(tabellaStat);
			tableViewScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double position = newValue.doubleValue();
					ScrollBar scrollbar = getTableViewScrollBar(tabellaStat);

					if (position == scrollbar.getMax()) {
						if (end < listaStatisticheCompleta.size()) {

							List<Statistica> sottoLista = listaStatisticheCompleta.subList(start, end);
							listaStatisticheAVideo.addAll(RigaStatistiche.ottieniListaRighe(sottoLista));
							start = end;
							end += inc;
							tabellaStat.scrollTo(start);
						}
						else {
							if (start<end) {
								List<Statistica> sottoLista = listaStatisticheCompleta.subList(start, listaStatisticheCompleta.size());
								listaStatisticheAVideo.addAll(RigaStatistiche.ottieniListaRighe(sottoLista));
								start = end;
								end=listaStatisticheCompleta.size();
								tabellaStat.scrollTo(start);
							}
						}
					}
				}
			});
		}
	 
	 

	public ObservableList<RigaStatistiche> getListaStatistiche() {
		return listaStatisticheAVideo;
	}

	public Label getLblStringaDesQuery() {
		return lblStringaDesQuery;
	}

	public void setLblStringaDesQuery(String testo) {
		this.lblStringaDesQuery.setText(testo);
	}	 
	 
	public List<Statistica> getListaStatisticheCompleta() {
		return listaStatisticheCompleta;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getInc() {
		return inc;
	}

	public void setInc(int inc) {
		this.inc = inc;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	
	
	
}
