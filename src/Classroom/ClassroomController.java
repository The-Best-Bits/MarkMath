package Classroom;

import java.lang.Character;

import Assignments.AssignmentPageController;
import Server.CheckMathParser;
import Server.SocketIOServer;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import login.Login;
import markmath.controllers.ParsedDataPerAssignment;
import markmath.controllers.ParsedDataPerAssignmentManager;
import markmath.entities.AssignmentBundle;
import markmath.entities.AssignmentOutline;
import markmath.entities.StudentAssignment;
import markmath.usecases.StudentAssignmentManager;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Date;

public class ClassroomController<MyType> implements Initializable {
    ClassroomModel classroomModel = new ClassroomModel();

    @FXML
    private Text classroom_name;

    @FXML
    private JFXTextField student_id;

    @FXML
    private JFXTextField student_name;

    @FXML
    private Label addStudentError;

    @FXML
    private Label studentNameError;

    @FXML
    private Label studentIDError;

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
    private JFXButton btnQuestion;

    @FXML
    private JFXButton addAssignment;

    @FXML
    private GridPane pane;

    @FXML
    private TextField textField[] = new TextField[20];

    @FXML
    private int numOfQuestions = 1;

    @FXML
    private JFXTextField assignment_id;

    @FXML
    private JFXTextField assignment_name;


    @FXML
    private Label errorCreatingAssignment;

    @FXML
    private Label errorMarkingStudentAssignment;

    public int counterAddQuestions = 1;
    public HashMap<String, Float> assignment_outline = new HashMap<String, Float>();

    private String classroomID;

    private MyType temp;
    private Date lastClickTime;


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
    private void RowSelect() {
        MyType row = (MyType) this.assignment_table.getSelectionModel().getSelectedItem();
        if (row==null) return;
        if (row!=temp) {
            temp = row;
            lastClickTime = new Date();
        }else{
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300){
                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("/Assignments/AssignmentPage.fxml"));

                try {
                    Loader.load();

                }catch (Exception e){
                    e.printStackTrace();
                }
                AssignmentPageController controller = Loader.getController();
                controller.setBundleid(this.assignment_table.getSelectionModel().getSelectedItem().getID());
                controller.setBundlename(this.assignment_table.getSelectionModel().getSelectedItem().getName());
                controller.setClassroomID(this.classroomID);
                controller.loadData();
                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);
                Stage stage = (Stage)assignment_table.getScene().getWindow();stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }

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
    void addStudent(ActionEvent event) throws Exception {
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        this.addStudentError.setText("");
        String studentID = this.student_id.getText().trim();
        String studentName = this.student_name.getText().trim();
        String classID = this.classroomID.trim();

        if (studentID.isEmpty()) {
            System.out.println("hi");
            this.studentIDError.setText("This field cannot be empty.");
            return;
        }

        if (studentName.isEmpty()) {
            this.studentNameError.setText("This field cannot be empty.");
            return;
        }

        boolean a = classroomModel.addStudentToClass(studentID, studentName, classID);

        if (!a) {
            this.addStudentError.setText("The student is already in the classroom.");
        }

        this.loadData();
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


    /**
     * Allows the user to add one extra question to the outline of a new assignment they wish to create.
     * It creates a label indicating the appropriate question number and a text field that allows the user to insert
     * a mark.
     * NOTE: MAX CAPACITY 20 QUESTIONS
     * @param event This method takes place when the add question button in the interface is clicked.
     */

    @FXML
    void addQuestion(ActionEvent event) {

       Label labels[] = new Label[20];

       textField[counterAddQuestions] = new TextField();
       labels[counterAddQuestions] = new Label("Question "+ counterAddQuestions);
       pane.add(labels[counterAddQuestions], 0, (counterAddQuestions));
       pane.add(textField[counterAddQuestions], 1, (counterAddQuestions));
       counterAddQuestions = counterAddQuestions + 1;
       numOfQuestions = numOfQuestions + 1;


    }

    /**
     * Allows the user to add a new Assignment to a classroom with specified id, name and outline.
     * It creates a new AssignmentBundle object and adds it to the AssignmentBundles database of this classroom by
     * getting the needed data from the text fields.
     * The method fails and communicates the user such failure if one of the text fields is empty or if the Assignment
     * already exists.
     * @param event This method takes place when the Add Assignment button in the interface is clicked.
     */
    @FXML
    void createAssignment(ActionEvent event)
    {

        int i = 1;

        //This clears the previous attempt of creating an assignment so that we are not making the outline larger
        assignment_outline.clear();
        createOutline();

        String received_name;
        String received_id;

        if ( assignment_name != null  && !assignment_name.getText().equals("")){
            received_name = assignment_name.getText().trim();
        }
        else{
            errorCreatingAssignment.setText("Please insert an Assignment name");
            received_name = "Test";
        }

        if (assignment_id != null  && !assignment_id.getText().equals("")){
           received_id = assignment_id.getText().trim();
        }
        else{
            errorCreatingAssignment.setText("Please insert an Assignment id");
            received_id = "99";
        }
        if (errorCreatingAssignment.getText().trim().equals("")){
            AssignmentOutline newOutline = new AssignmentOutline(received_name,assignment_outline );
            AssignmentBundle newAssignment = new AssignmentBundle(newOutline);
            System.out.println("Success");
            addAssignmentBundleToClassroomDatabase(received_id, newAssignment);
            loadBundleData();
            //createNewBundlePage(received_id, newAssignment);
        }

        //System.out.println(assignment_outline.get("Question 1"));
    }

    /**
     * Creates the Outline as the user inserted it in the interface through the various AddQuestion buttons and prepares
     * it to be used to create an assignment.
     * It checks if the input is a valid number and if no one of the fields is empty, and if any of these two conditions
     * fail, the user gets a prompt to input the outline correctly.
     */
    private void createOutline(){
        int i = 1;
        while (i != numOfQuestions) {
            String poss_number = textField[i].getText().trim();
            int j = 0;
            boolean isValidMark;
            isValidMark = poss_number.length() != 0;
            //System.out.println(poss_number);
            //System.out.println(j);
            //System.out.println(poss_number.length());
            while (j != poss_number.length()){
                if (!Character.isDigit(poss_number.charAt(j))){
                    isValidMark = false;}
                j = j+1;
                errorCreatingAssignment.setText("Please use only numbers");
            }

            if (isValidMark) {
                assignment_outline.put("question"+ (i), Float.valueOf(textField[i].getText()));
            }
            else
            {
                errorCreatingAssignment.setText("Please complete the Outline.");
            }
            i = i+1;
        }
    }


    /**
     * Adds assignment to the AssignmentBundles table of the database for this classroom with the specified id.
     * If the assignment already exists, nothing is added and the user is communicated so.
     * @param received_id The id the user wishes the new Assignment to have.
     * @param assignment The assignment the user has created with a specified name and outline.
     */
    private void addAssignmentBundleToClassroomDatabase(String received_id, AssignmentBundle assignment){

        String curr_class = this.classroomID;
        String sqlInsert = "INSERT INTO AssignmentBundles(assignmentbundle_id, assignment_name, classroom_id) VALUES (?,?,?)";
        if (assignmentBundleInClassroom(assignment.getName())){
            errorCreatingAssignment.setText("The Assignment already exists.");
        }
        else{
            try{

                Connection conn = dbConnection.getConnection();
                int received_id_int = Integer.parseInt(received_id);
               // ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM assignmentBundles");
                PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                stmt.setInt(1, received_id_int);
                stmt.setString(2, assignment.getName());
                stmt.setString(3, curr_class);
                int q = 1;
                stmt.execute();
                conn.close();
            }catch(SQLException e){
                System.out.println("Error" + e);
            }
        }
        }

    private void createNewBundlePage(String received_id, AssignmentBundle assignment) {

    }

    //Luca's
    private Boolean assignmentBundleInClassroom(String assignmentID){
        try{
            Connection conn = dbConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT assignmentbundle_id FROM AssignmentBundles WHERE classroom_id =" + this.classroomID);
            while (rs.next()){
                if(rs.getString("assignmentbundle_id").equals(assignmentID)){
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

    /**
     *  When a user clicks the MarkAssignment button within a classroom this method goes through all of the parsed data
     *  received by the server, creates a mock student assignment corresponding to the parsed data of each specific
     *  document that the teacher has opened and clicked through, and adds the required data to the database
     * @param event User clicks the MarkAssignment Button
     */
    @FXML
    void markAssignment(ActionEvent event){

        //when user clicks on Mark Assignment Button we get all of the data from the Hypatia document currently open
        Login.getServer().getResultsEvents();

        try {
            Thread.sleep(1000);
            ParsedDataPerAssignmentManager manager = CheckMathParser.getParsedDataManager();
            ArrayList<ParsedDataPerAssignment> parsedDataAssignmnents = manager.getParsedDataAssignments();
            for (ParsedDataPerAssignment assignment : parsedDataAssignmnents) {
                System.out.println(assignment.getFinalParsedData());
                //check that the student exists in this classroom
                //check that their is a corresponding assignment bundle in this classroom
                if (!studentInClassroom(assignment.getStudentNum()) || !assignmentBundleNameInClassroom(assignment.getAssignmentType())) {
                    this.errorMarkingStudentAssignment.setText("Error. Student or assignment bundle associated with this student document is not in this classroom");
                } else {
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
            manager.clearParsedDataAssignments();
        }catch(InterruptedException e){
            System.out.println("Error" + e);
        }

    }

    /**
     * Helper function to markStudentAssignment. Differs from assignmentBundleInClassroom as this method checks if
     * there exists an assignmentbundle with the given name in this classroom.
     * @param assignmentType Name of the assignmentbundle
     * @return True iff this classroom contains an assignmentbundle with this name
     */
    private Boolean assignmentBundleNameInClassroom(String assignmentType){
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
                System.out.println("Assignment bundle name in classroom");
            System.out.println("Error" + e);
        }

        return false;
    }

    /**
     * Helper function to markStudentAssignment
     * @param studentNum StudentID of a student
     * @return True iff there exists a student in this classroom with the given studentID
     */
    private Boolean studentInClassroom(String studentNum){

        try{
            Connection conn = dbConnection.getConnection();
//            ResultSet rs = conn.createStatement().executeQuery("SELECT student_id FROM students WHERE class_id LIKE concat('%', " + this.classroomID + ", '%'");
            String sql = "SELECT * FROM students WHERE student_id = " + studentNum + " AND CHARINDEX(':" + this.classroomID + ":', class_id) > 0";
//            ResultSet rs = conn.createStatement().executeQuery("SELECT class_id FROM students WHERE student_id =" + studentNum);
            ResultSet rs = conn.createStatement().executeQuery(sql);
            Boolean result = rs.next();
            rs.close();
            conn.close();
            return result;

//            String class_ids = rs.getString("class_id");
            //this wont work as if we have :11: as a students classroom_ids, when we check if they are in class with
            //id 1 it will return true
//            if (class_ids.contains(this.classroomID)){
//                conn.close();
//                return true;
//            }
//            System.out.println("test");
//            conn.close();
//            return false;

        }catch(SQLException e){
            System.out.println("Student in classroom");
            System.out.println("Error" + e);
            return false;
        }
    }


    /**
     * Helper function to markStudentAssignment
     * @param assignmentType Name of the assignmentbundle
     * @return the assignmentoutline of the assignmentbundle with the give name
     */
    private AssignmentOutline getAssignmentOutline(String assignmentType){

        String assignmentBundleID = getIDOfAssignmentBundle(assignmentType);

        try{
            Connection conn = dbConnection.getConnection();
            String sqlQuery = "SELECT * FROM '" + assignmentBundleID + "' ORDER BY ROWID ASC LIMIT 1";
            System.out.println(sqlQuery);
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
            System.out.println("Get Assignment Outline");
            System.out.println("Error" + e);
        }

        return null;
    }

    /**
     * Helper function to markStudentAssignment
     * @param assignmentBundleName
     * @return ID of the assignmentbundle with the given name in this classroom
     * Note: There is only ever one assignmentbundle with a given name in a classroom, so there can only be one ID
     * returned
     */
    private String getIDOfAssignmentBundle(String assignmentBundleName){
        try{
            Connection conn = dbConnection.getConnection();
            String sql = "SELECT assignmentbundle_id FROM AssignmentBundles WHERE assignment_name = '"
                    + assignmentBundleName + "'"+ " AND classroom_id ='" + this.classroomID + "'";
            System.out.println(sql);
            ResultSet rs = conn.createStatement().executeQuery(sql);
            System.out.println(rs.next());
            System.out.println(this.classroomID);
            String result = rs.getString("assignmentbundle_id");
            System.out.println(result);
            rs.close();
            conn.close();
            return result;

        }catch(SQLException e){
            System.out.println("Get Assignment bundle ID");
            System.out.println("Error" + e);
        }
        return null;
    }


    /**
     * Helper function to markStudentAssignment
     * @param studentNum StudentID of a student in this classroom
     * @return Name of the student with this StudentID
     */
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

    /**
     * Helper function to markStudentAssignment
     * Adds a student's assignment to the database in the corresponding assignmentbundle table
     * @param assignment StudentAssignment that has been marked
     */
    private void addStudentAssignmentToDatabase(StudentAssignment assignment){
        String assignmentBundleID = getIDOfAssignmentBundle(assignment.getAssignmentType());

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
        boolean studentAssignmentInDatabase = false;
        //check if student assignment is already in database
        if (studentAssignmentInDatabase(assignment.getStudentID(), assignment.getAssignmentType())){
            studentAssignmentInDatabase = true;
            StringBuilder sqlUpdate = new StringBuilder("UPDATE '" + assignmentBundleID + "' SET ");
            int q =1;
            while(q<= numQuestions){
                sqlUpdate.append("question" + q + "=" + assignment.getQuestion(q).getFinalMark() + ", ");
                q+=1;
            }
            sqlUpdate.append("total =").append(assignment.getFinalMark());
            sqlUpdate.append(" WHERE student_id=" + assignment.getStudentID());
            sqlInsert = sqlUpdate.toString();
        }
        else {
             sqlInsert = "INSERT INTO '" + assignmentBundleID + "'(student_id, student_name, document_name"
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
            System.out.println("Add Student assignment to database");
            System.out.println("Error" + e);
        }
    }

    /**
     * Helper function to markStudentAssignment
     * @param studentNum StudentID of a student
     * @param assignmentType Name of an assignmentbundle in this classroom
     * @return True iff there already exists an assignment in the given assignment bundle in this classroom for a
     * student with the given studentID
     */
    private Boolean studentAssignmentInDatabase(String studentNum, String assignmentType){
        String assignmentbundleID = getIDOfAssignmentBundle(assignmentType);
        try{
            Connection conn = dbConnection.getConnection();
            String sqlQuery = "SELECT student_name FROM '" + assignmentbundleID + "' WHERE student_id =" + studentNum;
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