/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 ITopia IS102-5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package luggage.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import static jdk.nashorn.internal.runtime.Context.printStackTrace;
import luggage.Debug;
import luggage.MainActivity;
import luggage.database.models.CustomerModel;
import luggage.database.models.LocationModel;
import luggage.database.models.LuggageModel;
import luggage.database.models.Model;
import luggage.helpers.StageHelper;

/**
 * MissingLuggageController
 *
 * Controller for luggage/missing.fxml and luggage/missingview.fxml
 *
 * Package: luggage.controllers
 *
 * @author ITopia IS102-5
 */
public class MissingLuggageController extends BaseController implements Initializable {

    // LIST ELEMENTS
    /**
     *
     */
    @FXML
    private TableView luggageTableView;

    /**
     *
     */
    @FXML
    private TableColumn tableViewId;

    /**
     *
     */
    @FXML
    private TableColumn tableViewStatus;

    /**
     *
     */
    @FXML
    private TableColumn tableViewTags;

    /**
     *
     */
    @FXML
    private TableColumn tableViewDate;

    /**
     *
     */
    @FXML
    private TableColumn tableViewNotes;

    /**
     *
     */
    @FXML
    private Button listView;

    /**
     *
     */
    @FXML
    private Label printNotif;

    /**
     *
     */
    @FXML
    private TextField listSearchField;

    // VIEW ELEMENTS
    /**
     *
     */
    @FXML
    private Button viewClose;

    /**
     *
     */
    @FXML
    private TextField viewTags;

    /**
     *
     */
    @FXML
    private ChoiceBox<String> viewStatus;

    /**
     *
     */
    @FXML
    private ChoiceBox<LocationModel> viewLocationId;

    /**
     *
     */
    @FXML
    private ChoiceBox<CustomerModel> viewCustomerId;

    /**
     *
     */
    @FXML
    private TextField viewStatusAsText;

    /**
     *
     */
    @FXML
    private TextField viewLocationAsText;

    /**
     *
     */
    @FXML
    private TextField viewCustomerAsText;

    /**
     *
     */
    @FXML
    private TextField viewNotes;

    /**
     *
     */
    @FXML
    private DatePicker viewDate;

    /**
     *
     */
    private ObservableList<LuggageModel> data = FXCollections.observableArrayList();

    /**
     *
     */
    private final ObservableList<LocationModel> locationData = FXCollections.observableArrayList();

    /**
     *
     */
    private final ObservableList<CustomerModel> customerData = FXCollections.observableArrayList();

    /**
     * Called on controller start
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                Debug.print("MISSING LUGGAGE CONTROLLER-----------------------------------------------------------------");

                if (luggageTableView != null) {
                    resetTableView("status = ?", "missing");
                    listView.disableProperty().bind(luggageTableView.getSelectionModel().selectedItemProperty().isNull());
                    luggageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    listKeyActions();
                }

                if (viewLocationId != null) {
                    setViewChoiceBoxes();
                    setViewFields();
                    viewKeyActions();
                }
            }
        });
    }

    /**
     * Calls LocationModel to enable mapping a Location ID to the Location's
     * name.
     */
    private LocationModel selectedLocation;

    /**
     * Calls CustomernModel to enable mapping a Customer ID to the Customer's
     * name.
     */
    private CustomerModel selectedCustomer;

    /**
     * Populates the view Location &amp; Customer ChoiceBoxes.
     */
    public void setViewChoiceBoxes() {
        // Locations
        LocationModel oLocationModel = new LocationModel();
        List<Model> allLocations = oLocationModel.findAll();

        int selectedLocationId = new LuggageModel(MainActivity.viewId).getLocationId();

        for (Model allLocation : allLocations) {
            LocationModel location = (LocationModel) allLocation;
            if (location.getId() == selectedLocationId) {
                selectedLocation = location;
            }

            locationData.add(location);
        }

        viewLocationId.setItems(locationData);

        long startTime = System.nanoTime();

        // Customers
        CustomerModel oCustomerModel = new CustomerModel();
        List<Model> allCustomers = oCustomerModel.findAll();

        int selectedCustomerId = new LuggageModel(MainActivity.viewId).getCustomerId();

        for (Model allCustomer : allCustomers) {
            CustomerModel customer = (CustomerModel) allCustomer;
            if (customer.getId() == selectedCustomerId) {
                selectedCustomer = customer;
            }

            customerData.add(customer);
        }

        viewCustomerId.setItems(customerData);

        ObservableList<String> statuses = FXCollections.observableArrayList();
        statuses.add("Missing");
        statuses.add("Found");
        statuses.add("Resolved");
        viewStatus.setItems(statuses);

        long endTime = System.nanoTime();
        long microseconds = ((endTime - startTime) / 1000);
        Debug.print("setViewChoiceBoxes() " + " took " + microseconds + " microseconds.");
    }

    /**
     * Opens the Missing Luggage list view.
     */
    @FXML
    public void listView() {
        LuggageModel luggage = (LuggageModel) luggageTableView.getSelectionModel().getSelectedItem();

        if (luggage == null) {
            return;
        }

        MainActivity.viewId = luggage.getId();

        StageHelper.addPopup("luggage/missingview", this, false, true);
    }

    /**
     * Populates the view fields with the selected Missing Luggage item's data.
     */
    public void setViewFields() {
        LuggageModel luggage = new LuggageModel(MainActivity.viewId);

        viewLocationId.getSelectionModel().select(selectedLocation);
        viewCustomerId.getSelectionModel().select(selectedCustomer);
        viewStatus.setValue(luggage.getStatus());
        viewLocationAsText.setText(selectedLocation.toString());
        try {
            viewCustomerAsText.setText(selectedCustomer.toString());
        } catch (NullPointerException n) {
            printStackTrace(n);
        }
        viewStatusAsText.setText(luggage.getStatus());
        viewTags.setText(luggage.getTags());
        viewNotes.setText(luggage.getNotes());

        LocalDate date = LocalDate.parse(luggage.getDatetime());
        viewDate.setValue(date);
    }

    /**
     *
     * @param where
     * @param params
     */
    public void resetTableView(String where, String... params) {
        LuggageModel luggage = new LuggageModel();
        List<Model> allLuggage = luggage.findAll(where, params);

        data = FXCollections.observableArrayList();
        for (Model singleModel : allLuggage) {
            LuggageModel singleLuggage = (LuggageModel) singleModel;
            data.add(singleLuggage);
        }

        tableViewId.setCellValueFactory(new PropertyValueFactory("id"));
        tableViewStatus.setCellValueFactory(new PropertyValueFactory("status"));
        tableViewTags.setCellValueFactory(new PropertyValueFactory("tags"));
        tableViewDate.setCellValueFactory(new PropertyValueFactory("datetime"));
        tableViewNotes.setCellValueFactory(new PropertyValueFactory("notes"));

        luggageTableView.setItems(data);
    }

    /**
     * Handles the search field functionality.
     */
    @FXML
    protected void listOnSearch() {

        String[] keywords = listSearchField.getText().split("\\s+");

        String[] params = new String[4 * keywords.length];
        boolean firstColumn = true;
        String query = "";

        for (int i = 0; i < keywords.length; i++) {
            if (firstColumn) {
                params[0 + i] = "%" + keywords[i] + "%";
                query += "id LIKE ?";
            } else {
                params[0 + i] = "%" + keywords[i] + "%";
                query += " OR id LIKE ?";
            }

            params[1 + i] = "%" + keywords[i] + "%";
            query += " OR tags LIKE ?";

            params[2 + i] = "%" + keywords[i] + "%";
            query += " OR status LIKE ?";

            params[3 + i] = "%" + keywords[i] + "%";
            query += " OR datetime LIKE ?";

            firstColumn = false;
        }

        resetTableView(query, params);
        clearNotif();
    }

    /**
     * Clears the notification label.
     */
    @FXML
    private void clearNotif() {
        printNotif.setText("");
    }

    /**
     * Clears the notification label.
     */
    @FXML
    private void clearSearch() {
        listOnSearch();
        clearNotif();
    }

    /**
     * Creates the (mouse, keyboard, etc.) event filters for the list view.
     */
    public void listKeyActions() {
        luggageTableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent a) -> {
            if (a.getCode().equals(KeyCode.ESCAPE)) {
                resetTableView("status = ?", "missing");
                Debug.print("Refreshed Found Luggage list view.");
            } else if (a.getCode().equals(KeyCode.V) || (a.getCode().equals(KeyCode.ENTER))) {
                listView();
            }
        });
        listView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent a) -> {
            if (a.getCode().equals(KeyCode.ESCAPE)) {
                resetTableView("status = ?", "missing");
                Debug.print("Refreshed Found Luggage list view.");
            } else if (a.getCode().equals(KeyCode.V) || a.getCode().equals(KeyCode.ENTER)) {
                listView();
            }
        });
        listSearchField.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE)) {
                resetTableView("", new String[0]);
                listSearchField.setText("");
                clearNotif();
            }
        });
    }

    /**
     * Creates the (mouse, keyboard, etc.) event filters for the view page.
     */
    public void viewKeyActions() {
        viewLocationAsText.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewCustomerAsText.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewStatusAsText.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewDate.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewTags.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewNotes.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
        viewClose.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent b) -> {
            if (b.getCode().equals(KeyCode.ESCAPE) || b.getCode().equals(KeyCode.ENTER)) {
                viewClose();
            }
        });
    }

    /**
     * Closes current view.
     */
    public void viewClose() {
        Stage addStage = (Stage) viewClose.getScene().getWindow();
        StageHelper.closeStage(addStage);
    }

}
