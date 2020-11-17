package Classroom;

import Server.CheckMathParser;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import markmath.controllers.ParsedDataPerAssignment;
import markmath.controllers.ParsedDataPerAssignmentManager;
import markmath.entities.AssignmentOutline;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

    @FXML
    private JFXButton markButton;

    @FXML
    private Label errorMarkingStudentAssignment;

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

    @FXML
    void markAssignment(ActionEvent event){
        ParsedDataPerAssignmentManager manager = CheckMathParser.getParsedDataManager();
        ArrayList<ParsedDataPerAssignment> parsedDataAssignmnents = manager.getParsedDataAssignments();
        for (ParsedDataPerAssignment assignment: parsedDataAssignmnents){
            //check that the student exists in this classroom
            //check that their is a corresponding assignment bundle in this classroom
            if(!studentInClassroom(assignment.getStudentNum()) || !assignmentBundleInClassroom(assignment.getAssignmentType())){
                this.errorMarkingStudentAssignment.setText("Error. Student or assignment bundle associated with this student document is not in this classroom");
            }
            else{
                //add student info to the assignment bundle for this classroom

            }




        }

    }

    private Boolean studentInClassroom(String studentNum){

        try{
            Connection conn = dbConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT student_id FROM students WHERE CHARINDEX(':" + this.classroomID + ":', class_id)>0)");
            while (rs.next()){
                if(rs.getString("student_id").equals(studentNum)){
                    conn.close();
                    return true;
                }
            }
            conn.close();
            return false;

        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return false;

    }

    private Boolean assignmentBundleInClassroom(String assignmentType){
        try{
            Connection conn = dbConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT assignment_name FROM AssignmentBundles WHERE classroom_id =" + this.classroomID);
            while (rs.next()){
                if(rs.getString("assignment_name").equals(assignmentType)){
                    conn.close();
                    return true;
                }
            }
            conn.close();
            return false;

        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return false;
    }

    private AssignmentOutline getAssignmentOutline(String assignmentType){
        try{
            Connection conn = dbConnection.getConnection();
            String sqlQuery = "SELECT * FROM " + assignmentType + " ORDER BY ROWID ASC LIMIT 1";
            ResultSet rs = conn.createStatement().executeQuery(sqlQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            HashMap<String, Float> questionToMarks = new HashMap<>();
            System.out.println(numColumns);
            for (int i =1; i <= numColumns-4; i++){
                String question = "question" + i;
                questionToMarks.put(question, rs.getFloat(question));
            }
        }catch(SQLException e){
            System.out.println("Error" + e);
        }
    }

}