package boundary.fxmlControllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import control.MainController;
import entity.Store;
import entity.MyMessage.MessageType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * @author hamza
 *
 */
public class CEOOrderReportsController implements Initializable {

	@FXML
    private TableView<?> ReportTableView;

    @FXML
    private TableColumn<?, ?> amountSoldTableCol;

    @FXML
    private TableColumn<?, ?> itemNameTableCol;

    @FXML
    private ChoiceBox<String> branchsChoiceBox;

    @FXML
    private ListView<String> monthsListView;

    @FXML
    private BarChart<?, ?> reportBarChart;

    @FXML
    private Text reportMonthText;

    @FXML
    private TextField totalItemsSoldTextField;

    @FXML
    private Button viewReportButton;

    /*-------------------------------------------------*/
    
    /* array of the names of the branches */
    private static ArrayList<String> branchsNames;
    
    /* an ArrayList to fill the list view of the months (monthsListView) */
    private  static ArrayList<String> monthsYears = null;
    
    /* the selected branch's ID */
    private static int branchID;
    
    /* ----------------------------------------------- */

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setBranchNamesInArrayList();
		this.branchsChoiceBox.getItems().addAll(branchsNames);
		this.branchsChoiceBox.setOnAction(this::afterBranchSelected);
		monthsListView.getItems().addAll(monthsYears);
		
		monthsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				monthSelectedFromListView();
			}
		});
		
	}
	
	
	/* ----------------------------------------------------------------- */
	
	
	/**
	 * Function to set the branch names in an ArrayList,
	 * so we can show them in the ChoiceBox.
	 */
	private void setBranchNamesInArrayList() {
		for(Store s : Store.values()) {
			branchsNames.add(s.toString());
		}
	}
	
	/**
	 * @param branchId		the ID we want to set
	 * 
	 *  Function to set the branchID according to the selected branch.
	 */
	public void setBranchID(int branchId) {
		branchID = branchId;
	}
	
	/**
	 * @param event
	 * 
	 * Function to do after we select a branch from branchsChoiceBox.
	 */
	@SuppressWarnings("unchecked")
	public void afterBranchSelected(ActionEvent event) {
		setBranchID(Store.valueOf(branchsChoiceBox.getValue()).ordinal());
		monthsYears = (ArrayList<String>)MainController.getMyClient().send(MessageType.GET, "order/report/sale/months/"+branchID, null);
		monthsListView.getItems().addAll(monthsYears);
	}
	
	/**
	 * Action when a line is selected in the monthsListView. 
	 */
	public void monthSelectedFromListView() {
		this.viewReportButton.setDisable(false);
	}
	
	/**
	 * Function to set the monthsYearsArrayList into monthsYears,
	 * So we can show them in ListView.
	 */
	public static void setMonthsYears(ArrayList<String> monthsYearsArrayList) {
		monthsYears = monthsYearsArrayList;
	}

	
}
