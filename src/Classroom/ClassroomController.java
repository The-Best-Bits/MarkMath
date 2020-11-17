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
import markmath.entities.StudentAssignment;
import markmath.usecases.StudentAssignmentManager;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
                //get assignment outline
                AssignmentOutline outline = getAssignmentOutline(assignment.getAssignmentType());
                //get student name
                String studentName = getStudentNameFromDatabase(assignment.getStudentNum());
                StudentAssignmentManager saManager = new StudentAssignmentManager(assignment.getStudentNum(),
                        studentName, assignment.getAssignmentName(), assignment.getAssignmentType(),
                        assignment.getFinalParsedData(), outline);
                saManager.markAllQuestions();
                StudentAssignment studentAssignment = saManager.getCarbonCopy();
                //add StudentAssignment to Database
                addStudentAssignmentToDatabase(studentAssignment);

            }

        }

    }

    private Boolean studentInClassroom(String studentNum){

        try{
            Connection conn = dbConnection.getConnection();
//            ResultSet rs = conn.createStatement().executeQuery("SELECT student_id FROM students WHERE class_id LIKE concat('%', " + this.classroomID + ", '%'");
            ResultSet rs = conn.createStatement().executeQuery("SELECT class_id FROM students WHERE student_id =" + studentNum);
            String class_ids = rs.getString("class_id");
            System.out.println(class_ids);
            if (class_ids.contains(this.classroomID)){
                conn.close();
                return true;
            }
//            while (rs.next()){
//                if(rs.getString("student_id").equals(studentNum)){
//                    conn.close();
//                    return true;
//                }
//            }
            System.out.println("test");
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
            conn.close();
            AssignmentOutline outline = new AssignmentOutline(assignmentType, questionToMarks);
            return outline;
        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return null;
    }

    private String getStudentNameFromDatabase(String studentNum){
        try{
            Connection conn = dbConnection.getConnection();
            String sqlQuery = "SELECT student_name FROM students WHERE student_id = " + studentNum;
            ResultSet rs = conn.createStatement().executeQuery(sqlQuery);
            String studentName = rs.getString("student_name");
            conn.close();
            return studentName;
        }
        catch(SQLException e){
            System.out.println("Error" + e);
        }
        return null;
    }

    private void addStudentAssignmentToDatabase(StudentAssignment assignment){

        int numQuestions = assignment.getQuestions().size();
        StringBuilder questions = new StringBuilder();
        StringBuilder questionMarks = new StringBuilder("(?,?,?,?");
        for (int i =1; i<=numQuestions; i++){
            questions.append(", question").append(i);
            questionMarks.append(",?");
        }
        questions.append(", total)");
        questionMarks.append(")");
        String sqlInsert = "";
        Boolean studentAssignmentInDatabase = false;
        //check if student assignment is already in database
        if (studentAssignmentInDatabase(assignment.getStudentID(), assignment.getAssignmentType())){
            studentAssignmentInDatabase = true;
            StringBuilder sqlUpdate = new StringBuilder("UPDATE " + assignment.getAssignmentType() + " SET ");
            int q =1;
            while(q< numQuestions){
                sqlUpdate.append("question" + q + "=" + assignment.getQuestion(q).getFinalMark() + ", ");
                q+=1;
            }
            sqlUpdate.append("question").append(q).append("=").append(assignment.getQuestion(q).getFinalMark());
            sqlUpdate.append(" WHERE student_id=" + assignment.getStudentID());
            sqlInsert = sqlUpdate.toString();
        }
        else {
             sqlInsert = "INSERT INTO " + assignment.getAssignmentType() + "(student_id, student_name, document_name"
                    + questions + " VALUES " + questionMarks;
        }
        System.out.println(sqlInsert);
        try{
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);
            if(!studentAssignmentInDatabase) {
                stmt.setString(1, assignment.getStudentID());
                stmt.setString(2, assignment.getStudentName());
                stmt.setString(3, assignment.getAssignmentName());
                int q = 1;
                while (q <= numQuestions) {
                    stmt.setFloat(q + 3, assignment.getQuestion(q).getFinalMark());
                    q += 1;
                }
                stmt.setFloat(q + 3, assignment.getFinalMark());
            }
            stmt.execute();
            conn.close();

        }catch(SQLException e){
            System.out.println("Error" + e);
        }
    }

    private Boolean studentAssignmentInDatabase(String studentNum, String assignmentType){
        try{
            Connection conn = dbConnection.getConnection();
            String sqlQuery = "SELECT student_name FROM " + assignmentType + " WHERE student_id =" + studentNum;
            ResultSet rs = conn.createStatement().executeQuery(sqlQuery);
            if (rs.next()){
                conn.close();
                return true;
            }
            conn.close();
            return false;
        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return false;
    }
}