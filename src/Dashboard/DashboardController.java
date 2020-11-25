package Dashboard;


import Classroom.ClassroomController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dbUtil.dbConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;



public class DashboardController<MyType> implements Initializable {
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

    private dbConnection dbc;

    private ObservableList<Classroom> data;
    private MyType temp;
    private Date lastClickTime;
    private Stage stage;
    private JFXButton openClassroom;

    /**
     * Adds a classroom to this users account
     * @param event AddClassroom button is clicked
     */
    @FXML
    private void addClassroom(ActionEvent event) {
        //first check if the user has left the classroomid or classroomname text fields empty
        if(this.classroomid.getText().equals("") || this.classroomname.getText().equals(""))
        {
            this.preExistingID.setText("Error! Text field left blank");
        }
        else {
            String sqlInsert = "INSERT INTO classrooms(class_id, class_name) VALUES (?,?)";
            try {
                Connection conn = dbConnection.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classrooms");
                ArrayList<String> preExistingClassroomIDs = new ArrayList<>();
                while (rs.next()) {
                    preExistingClassroomIDs.add(rs.getString("class_id"));
                }
                //check if there already exists a classroom with the passed id
                if (preExistingClassroomIDs.contains(this.classroomid.getText())) {
                    this.preExistingID.setText("Error! Pre-existing Classroom ID");
                } else {
                    this.preExistingID.setText("Classroom successfully added");
                    PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                    stmt.setString(1, this.classroomid.getText());
                    stmt.setString(2, this.classroomname.getText());
                    stmt.execute();
                    loadClassroom(event);
                }
                conn.close();

            } catch (SQLException throwable) {
                throwable.printStackTrace();
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
        try {                                                                                                 //establish connection
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classrooms");
            while (rs.next()){
                this.data.add(new Classroom(rs.getString("class_name"), rs.getString("class_id")));      //add data to resultset
                System.out.println(rs.getString("class_id"));
                System.out.println(rs.getString("class_name"));
                //if an empty classroom is added we should check for this and omit it
            }
        }catch (SQLException e){
            System.err.println("Error" + e);
        }

        //add data we got to the table
        this.idcolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("id"));
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<Classroom,String>("classname"));
//        this.classroomtable.setItems(null);
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
        if(row != temp){
            temp = row;
            lastClickTime = new Date();
        } else if(row == temp) {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300){ //another click registered in 300 millis
                //Load the assignment bundle page with selected class's assignment bundles presented
                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("/Classroom/Classroom.fxml"));

                try {
                    Loader.load();

                }catch (Exception e){
                    e.printStackTrace();
                }

                ClassroomController classroomController = Loader.getController();
                classroomController.setClassroomID(this.classroomtable.getSelectionModel().getSelectedItem().getId());
                classroomController.loadData();
                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);

                Stage stage = (Stage)classroomtable.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }
    }

    /**
     * Opens the main "Dashboard page" that displays a table with all of the classrooms of this user
     * @param event Classroom button is clicked
     * @throws IOException
     */
    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    //will be changed to an instructions page
    @FXML
    void openPeople(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/People/People.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Opens a settings page...
     * @param event Settings button is clicked
     * @throws IOException
     */
    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
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
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classrooms");
            while (rs.next()){
                this.data.add(new Classroom(rs.getString("class_name"), rs.getString("class_id")));       //add data to resultset

            }
        }catch (SQLException e){
            System.err.println("Error" + e);
        }

        //add data we got to the table
        this.idcolumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("id"));
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<Classroom,String>("classname"));
//        this.classroomtable.setItems(null);
        this.classroomtable.setItems(this.data);
    }
}
