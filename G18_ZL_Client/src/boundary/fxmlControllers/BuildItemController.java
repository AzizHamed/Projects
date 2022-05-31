package boundary.fxmlControllers;

import java.net.URL;
import java.util.ResourceBundle;

import entity.BuildItem;
import entity.Item;
import entity.Item.ItemInBuild;
import entity.Item.OrderItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BuildItemController implements Initializable {

	@FXML
	private CheckBox chooceItemChecBox;

	@FXML
	private Label priceLabel;

	@FXML
	private TextField quantityTextField;

	private int column, row;

	BuildItemsPageController buildItemsPageController;
	Item presseditem;

	OrderItem orderItem;

	public void setData(Item item, int column, int row, BuildItemsPageController buildItemsPageController) {
		presseditem = item;
		this.column = column;
		this.row = row;
		//chooceItemChecBox.setText(item.getName());
		chooceItemChecBox.setText(item.getPrice() + "");
	 
		this.buildItemsPageController = buildItemsPageController;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		quantityTextField.setText(0 + "");
		quantityTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					// Textfield on focus" ;
					 
				} else {
					
					// Textfield not focus";

					// Textfield is null
					if (quantityTextField.getText().trim().isEmpty()) {
						chooceItemChecBox.setSelected(false);
						quantityTextField.setDisable(true);
						quantityTextField.setText(0 + "");
						BuildItemsPageController.deleteItemIsNotselected(presseditem);
					} 
					
					else {
						// Textfield is not number
						if (!isNumeric(quantityTextField.getText())) {
							quantityTextField.setText("0");
							BuildItemsPageController.deleteItemIsNotselected(presseditem);
							chooceItemChecBox.setSelected(false);
							quantityTextField.setDisable(true);
						} else {
							// Textfield ==0
							if (Integer.parseInt(quantityTextField.getText()) == 0) {
								BuildItemsPageController.deleteItemIsNotselected(presseditem);
								chooceItemChecBox.setSelected(false);
								quantityTextField.setDisable(true);
								BuildItemsPageController.deleteItemIsNotselected(presseditem);
								
								
							}else {

							// add item
							quantityTextField.setStyle("-fx-text-fill: black; ");
							Integer.parseInt(quantityTextField.getText());
							BuildItemsPageController.SelectedItem(presseditem,
									Integer.parseInt(quantityTextField.getText()));
							}
						}
					}

				}
			}
		});

	}

	public void change(ActionEvent event) {

		if (chooceItemChecBox.isSelected()) {
			// checkBox selected
			quantityTextField.setDisable(false);

			if (quantityTextField.getText().equals("0")) {
				quantityTextField.setText("1");
				BuildItemsPageController.SelectedItem(presseditem, 1);
			}
			

		} else {
			// checkBox not selected
			BuildItemsPageController.deleteItemIsNotselected(presseditem);
			quantityTextField.setDisable(true);
				quantityTextField.setText(0+"");

		}
	}
	// check sting is number
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			int d = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}