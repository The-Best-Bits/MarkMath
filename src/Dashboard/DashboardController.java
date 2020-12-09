package Dashboard;


import Classroom.ClassroomController;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import markmath.entities.Classroom;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;



public class DashboardController<MyType> implements Initializable {
    /**
     * Following Clean Architecture, this is a controller class that directly interacts with the UI for the Dashboard
     * fxml page. It is responsible for initializing the Dashboard page and taking input from users on the Dashboard
     * page and performing the necessary actions.
     */

    @FXML
    private TableView<Classroom> classroomtable;

    @FXML
    private TableColumn<Classroom, String> idcolumn;

    @FXML
    private TableColumn<Classroom, String> namecolumn;

    @FXML
    private JFXTextField classroomid;

    @FXML
    private JFXTextField classroomname;

    @FXML
    private Label preExistingID;

    private ObservableList<Classroom> data;
    private MyType temp;
    private Date lastClickTime;
    private final DashboardModel model = new DashboardModel();


    /**
     * Opens the main "Dashboard page" that displays a table with all of the classrooms of this user
     * @param event Classroom button is clicked
     * @throws IOException
     */
    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Opens the settings page
     * @param event Settings button is clicked
     * @throws IOException
     */
    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Opens a how-to-use page for Dashboard
     * @param event user clicks on help button
     * @throws IOException
     */

    @FXML
    void openHelp(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/ClassroomGuide.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = new Stage();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Called when Dashboard.fxml is initially initialized after the user logs in
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {                                                                                                 //establish connection
            this.data = FXCollections.observableArrayList();
            ResultSet rs = this.model.getClassroomData();
            while (rs.next()) {
                this.data.add(new Classroom(rs.getString("class_name"), rs.getString("class_id")));       //add data to resultset

            }
        } catch (SQLException e) {
            System.err.println("Error" + e);
        }

        //add data we got to the table
        this.idcolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("id"));
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("classname"));
        this.classroomtable.setItems(this.data);
    }

    /**
     * Adds a classroom to this users account
     * @param event AddClassroom button is clicked
     */
    @FXML
    private void addClassroom(ActionEvent event) {
        //first check if the user has left the classroomid or classroomname text fields empty
        if (this.classroomid.getText().trim().equals("") || this.classroomname.getText().trim().equals("")) {
            this.preExistingID.setText("Error! Text field left blank");
        } else {
            String classID = this.classroomid.getText().trim();
            String className = this.classroomname.getText().trim();
            if (this.model.addClassroomToDatabase(classID, className)){
                this.preExistingID.setText("Classroom successfully added");
                try {
                    loadClassroom(event);
                }catch (SQLException e){
                    System.out.println("Error loading classrooms table" + e);
                }
            }
            else{
                this.preExistingID.setText("Error! Pre-existing Classroom ID");
            }
        }
    }

    /**
     * Updates the classrooms table to show all of the classrooms for this user
     * @param event AddClassroom button is clicked
     * @throws SQLException
     */
    @FXML
    private void loadClassroom(ActionEvent event) throws SQLException {
        this.preExistingID.setText("");
        try {
            this.data = FXCollections.observableArrayList();
            ResultSet rs = this.model.getClassroomData();
            while (rs.next()) {
                this.data.add(new Classroom(rs.getString("class_name"), rs.getString("class_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error" + e);
        }

        //add data we got to the table
        this.idcolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("id"));
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("classname"));
        this.classroomtable.setItems(this.data);

    }


    /**
     * When a user clicks on a specific row (corresponding to a classroom), this method will open up a page for that
     * specific classroom that displays all of the assignmentbundles and students within the classroom
     */
    @FXML
    private void handleRowSelect() {
        //Detect double click
        MyType row = (MyType) this.classroomtable.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (row != temp) {
            temp = row;
            lastClickTime = new Date();
        } else if (row == temp) {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300) { //another click registered in 300 millis
                //Load the assignment bundle page with selected class's assignment bundles presented
                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("/Classroom/Classroom.fxml"));

                try {
                    Loader.load();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ClassroomController classroomController = Loader.getController();
                classroomController.setClassroomID(this.classroomtable.getSelectionModel().getSelectedItem().getId());
                classroomController.loadData();
                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);

                Stage stage = (Stage) classroomtable.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }
    }

}