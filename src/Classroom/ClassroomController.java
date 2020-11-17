package Classroom;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClassroomController implements Initializable {
    ClassroomModel classroomModel = new ClassroomModel();

    @FXML
    private Text classroom_name;

    @FXML
    private JFXTextField student_id;

    @FXML
    private JFXTextField student_name;

    @FXML
    private TableView<StudentData> student_table;

    @FXML
    private TableColumn<StudentData, String> student_id_column;

    @FXML
    private TableColumn<StudentData, String> student_name_column;

    @FXML
    private TableView<AssignmentData> assignment_table;

    @FXML
    private TableColumn<AssignmentData, String> assignment_id_column;

    @FXML
    private TableColumn<AssignmentData, String> assignment_name_column;

    private dbConnection db;

    private ObservableList<StudentData> student_data;

    private ObservableList<AssignmentData> assignment_data;


    private String classroomID;

    public String getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(String classroomID) {
        this.classroomID = classroomID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();
    }

    @FXML
    public void loadData() {
        try {
            this.classroom_name.setText(classroomModel.getClassroomName(this.classroomID));
            loadBundleData();
            loadStudentData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void loadBundleData() {
        try {
            Connection conn = dbConnection.getConnection();
            this.assignment_data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery("SELECT assignmentbundle_id, assignment_name, classroom_id FROM AssignmentBundles WHERE classroom_id =" + this.classroomID);
            while (rs.next()) {
                this.assignment_data.add(new AssignmentData(rs.getString(1), rs.getString(2), rs.getString(3)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.assignment_id_column.setCellValueFactory(new PropertyValueFactory<AssignmentData, String>("ID"));
        this.assignment_name_column.setCellValueFactory(new PropertyValueFactory<AssignmentData, String>("name"));

        this.assignment_table.setItems(null);
        this.assignment_table.setItems(this.assignment_data);
    }

    @FXML
    private void loadStudentData() throws SQLException {
        try {
            Connection conn = dbConnection.getConnection();
            this.student_data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery("SELECT student_id, student_name, class_id FROM students WHERE CHARINDEX(':" + this.classroomID + ":', class_id) > 0");
            while (rs.next()) {
                this.student_data.add(new StudentData(rs.getString(1), rs.getString(2), rs.getString(3)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.student_id_column.setCellValueFactory(new PropertyValueFactory<StudentData, String>("ID"));
        this.student_name_column.setCellValueFactory(new PropertyValueFactory<StudentData, String>("name"));

        this.student_table.setItems(null);
        this.student_table.setItems(this.student_data);
    }

    @FXML
    void addStudent(ActionEvent event) {

    }

    @FXML
    void openDashboard(ActionEvent event) throws IOException {
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



}