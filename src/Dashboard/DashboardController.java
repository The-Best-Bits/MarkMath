package Dashboard;


import AssignmentBundle.AssignmentBundlePageController;
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

    @FXML
    private void addClassroom(ActionEvent event) {
        String sqlInsert = "INSERT INTO classrooms(class_id, class_name) VALUES (?,?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classrooms");
            ArrayList<String> preExistingClassroomIDs = new ArrayList<>();
            while(rs.next()){
                preExistingClassroomIDs.add(rs.getString("class_id"));
            }
            if (preExistingClassroomIDs.contains(this.classroomid.getText())){
                 this.preExistingID.setText("Error! Pre-existing Classroom ID");
            }
            else {
                stmt.setString(1, this.classroomid.getText());
                stmt.setString(2, this.classroomname.getText());
            }
            stmt.execute();
            conn.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

    }

    @FXML
    private void loadClassroom(ActionEvent event) throws SQLException {
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

    private JFXButton openClassroom;
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

                Loader.setLocation(getClass().getResource("/AssignmentBundle/AssignmentBundlePage.fxml"));

                try {
                    Loader.load();

                }catch (Exception e){
                    e.printStackTrace();
                }

                AssignmentBundlePageController aBundlePage = Loader.getController();
                aBundlePage.setClassroomID(this.classroomtable.getSelectionModel().getSelectedItem().getId());

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


    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

    @FXML
    void openPeople(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/People/People.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

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
