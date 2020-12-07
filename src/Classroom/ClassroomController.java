package Classroom;

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
import Classroom.ClassroomModel;
import StudentMarks.StudentMarksController;


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
    public TabPane tab_pane;

    @FXML
    private Pane student_id_pane;

    @FXML
    private Pane student_name_pane;

    @FXML
    private JFXTextField student_id;

    @FXML
    private JFXTextField student_name;

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
    private int numOfQuestions = 0;

    @FXML
    private JFXTextField assignment_id;

    @FXML
    private JFXTextField assignment_name;

    @FXML
    private JFXButton openHelp;

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
    private void loadStudentData() throws Exception {
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
        this.student_id_pane.toFront();
    }

    /**
     * Adds student to classroom, and clears fields.
     * @param event user clicks add student button on the addStudentIDPane.
     * @throws Exception
     */
    @FXML
    void addStudentWithID(ActionEvent event) throws Exception {
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        String studentID = this.student_id.getText().trim();
        String classID = this.classroomID.trim();

        if (studentID.isEmpty()) {
            System.out.println("hi");
            this.studentIDError.setText("This field cannot be empty.");
            return;
        }

        if (classroomModel.studentIsInDatabase(studentID)) {
            if (classroomModel.studentIsInClass(studentID, classID)) {
                this.studentNameError.setText("This student is already in the class.");
            } else {
                this.classroomModel.addStudentToClass(studentID, classID);
                this.student_id.setText("");
                this.loadData();
            }
        } else {
            this.student_name_pane.toFront();
        }
    }

    /**
     * Adds student to classroom and to database, and clears fields and goes to the addStudentIDPane.
     * @param event user clicks add student button on the addStudentNamePane.
     * @throws Exception
     */
    @FXML
    void addStudentWithName(ActionEvent event) throws Exception{
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        String studentID = this.student_id.getText().trim();
        String studentName = this.student_name.getText().trim();
        String classID = this.classroomID.trim();

        if (studentName.isEmpty()) {
            System.out.println("hi");
            this.studentNameError.setText("This field cannot be empty.");
            return;
        }

        this.classroomModel.addStudentToDatabase(studentID, studentName);
        this.classroomModel.addStudentToClass(studentID, classID);
        this.loadData();
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        this.student_id.setText("");
        this.student_name.setText("");
        this.student_id_pane.toFront();
    }

    /**
     * Goes back to the studentIDPane and clears fields
     * @param event user clicks on back button
     * @throws Exception
     */
    @FXML
    void addStudentBack(ActionEvent event) throws Exception {
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        this.student_id.setText("");
        this.student_name.setText("");
        this.student_id_pane.toFront();
    }

    /**
     * Removes student from classroom
     * @param event user clicks on remove Student event
     * @throws Exception
     */
    @FXML
    void removeStudent(ActionEvent event) throws Exception {
        this.studentIDError.setText("");
        this.studentNameError.setText("");
        String studentID = this.student_id.getText().trim();
        String classID = this.classroomID.trim();

        if (studentID.isEmpty()) {
            this.studentIDError.setText("This field cannot be empty.");
            return;
        }

        if (this.classroomModel.studentIsInClass(studentID, classID)) {
            this.classroomModel.removeStudent(studentID, classID);
            this.student_id.setText("");
            this.loadData();
        }
    }

    /**
     * When a user clicks on a specific row (corresponding to a student), this method will open up a page for that
     * specific student in that classroom that displays the grade breakdown for each assignment that the student completed.
     */
    @FXML
    private void studentRowSelect() throws Exception {
        //Detect double click
        MyType row = (MyType) this.student_table.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (row != temp) {
            temp = row;
            lastClickTime = new Date();
        } else if (row == temp) {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300) { //another click registered in 300 millis
                //Load a page for the student
                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("/StudentMarks/StudentMarks.fxml"));

                try {
                    Loader.load();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                StudentMarksController studentMarksController= Loader.getController();
                studentMarksController.setStudentID(this.student_table.getSelectionModel().getSelectedItem().getID());
                studentMarksController.setClassID(this.classroomID);
                studentMarksController.loadPage();
                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);

                Stage stage = (Stage) student_table.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }
    }

    @FXML
    void openDashboard(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Opens a how-to-use page for assignments
     * @param event user clicks on help button
     * @throws IOException
     */

    @FXML
    void openHelp(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Classroom/howto.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = new Stage();
        stage.setScene(mainPage);
        stage.show();
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
        //This clears the previous attempt of creating an assignment so that we are not making the outline larger or
        // communicating an error from a previous attempt
        errorCreatingAssignment.setText("");
        assignment_outline.clear();
        //Checks if the outline has at least one question
        if (numOfQuestions == 0) {
            errorCreatingAssignment.setText("Please add a Question");
        }
        else {
            createOutline();
        }


        //Checks if a valid name and id were inserted. If not, an error is displayed and this error
        //will stop the program from creating the assignment
        ArrayList<String> fieldData = checkFields();
        String received_id = fieldData.get(0);
        String received_name = fieldData.get(1);
        System.out.println(received_id);
        //Add an assignment to the Class and creates its page only if the error label is empty, which means no
        //error was found anywhere in the process
        if (errorCreatingAssignment.getText().trim().equals("")){
            AssignmentOutline newOutline = new AssignmentOutline(received_name, assignment_outline);
            AssignmentBundle newAssignment = new AssignmentBundle(newOutline);
            //System.out.println("Success");
            addAssignmentBundleToClassroomDatabase(received_id, newAssignment);
            ClassroomModel model = new ClassroomModel();
            if (errorCreatingAssignment.getText().trim().equals("")){
                try {
                    model.createAssignmentTable(received_id, newAssignment);
                    model.addOutlineToAssignmentTable(newOutline, received_id);

                }
                catch (Exception e){
                    e.printStackTrace();
                    errorCreatingAssignment.setText("There was a connection problem");
                }
            }

            loadBundleData();
        }
    }

    /**
     * Creates the Outline as the user inserted it in the interface through the various AddQuestion buttons and prepares
     * it to be used to create an assignment.
     * It checks if the input is a valid number (Positive float) and if no one of the fields is empty,
     * and if any of these two conditions fail, the user gets a prompt to input the outline correctly.
     * Precondition: At least one question must exists and be valid.
     */
    private void createOutline(){
        int i = 0;
        // Gets the numbers for the outline from each field
        while (i != numOfQuestions) {
            String poss_number = textField[i+1].getText().trim();
            //checks if the number is valid for the outline
            try {
              float poss_number_float = Float.parseFloat(poss_number);
              if (poss_number_float > 0) {
                  assignment_outline.put("question"+ (i+1), poss_number_float);
              } else {
                  errorCreatingAssignment.setText("Please complete the Outline with valid full numbers.");
              }

            }
            catch (Exception NumberFormatException){

                errorCreatingAssignment.setText("Please complete the Outline with valid full numbers.");
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
        try {
            if (assignmentBundleInClassroom(received_id)) {
                errorCreatingAssignment.setText("This Assignment's id already exists.");
            } else if (classroomModel.assignmentBundleNameInClassroom(assignment.getName(), this.classroomID)) {
                errorCreatingAssignment.setText("This Assignment's name already exists.");
            } else {
                Connection conn = dbConnection.getConnection();
                int received_id_int = Integer.parseInt(received_id);
                // ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM assignmentBundles");
                PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                stmt.setInt(1, received_id_int);
                stmt.setString(2, assignment.getName());
                stmt.setString(3, curr_class);
                stmt.execute();
                conn.close();
            }
        }catch (SQLException e){
            errorCreatingAssignment.setText("Unexpected error. The assignment may \n exist in other classrooms");
        }
        }

    private ArrayList<String> checkFields(){
        String received_name;
        String received_id;
        ArrayList<String> fieldData = new ArrayList<>();

        if (assignment_id != null  && !assignment_id.getText().equals("")){
            received_id = assignment_id.getText().trim();
            fieldData.add(received_id);
        }
        else{
            errorCreatingAssignment.setText("Please insert an Assignment id");
            received_id = "99";
            fieldData.add(received_id);
        }

        if ( assignment_name != null  && !assignment_name.getText().equals("")){
            received_name = assignment_name.getText().trim();
            fieldData.add(received_name);
        }
        else{
            errorCreatingAssignment.setText("Please insert an Assignment name");
            received_name = "Test";
            fieldData.add(received_name);
        }

        return fieldData;

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

        Login.getServer().setParseResultsEvents(true);
        Login.getServer().getAllResultsEvents();

        try {
            //freezes entire program until we receive "result" events from Hypatia
            Thread.sleep(7000);
            Login.getServer().setParseResultsEvents(false);
            CheckMathParser parser = Login.getServer().getparser();
            System.out.println(parser.getFinalParsedData());
            //check that the student exists in this classroom
            //check that there is a corresponding assignment bundle in this classroom
            if (!classroomModel.studentIsInClass(parser.getStudentNum(), this.classroomID)
                    || !classroomModel.assignmentBundleNameInClassroom(parser.getAssignmentType(), this.classroomID)) {
                this.errorMarkingStudentAssignment.setText("Error. Student or assignment bundle associated with\nthis student document is not in this classroom");
            } else {
                this.errorMarkingStudentAssignment.setText("");
                //get assignment outline
                AssignmentOutline outline = classroomModel.getAssignmentOutline(parser.getAssignmentType(),
                        this.classroomID);
                //get student name
                String studentName = classroomModel.getStudentNameFromDatabase(parser.getStudentNum());
                StudentAssignmentManager saManager = new StudentAssignmentManager(parser.getStudentNum(), studentName,
                        parser.getAssignmentName(), parser.getAssignmentType(), parser.getFinalParsedData(), outline);
                saManager.markAllQuestions();
                StudentAssignment studentAssignment = saManager.getCarbonCopy();
                //add StudentAssignment to Database
                addStudentAssignmentToDatabase(studentAssignment);
            }

            System.out.println("test" + parser.getFinalParsedData().isEmpty());
            parser.clearCheckMathParser();
            System.out.println("test" + parser.getFinalParsedData().isEmpty());

        }catch(Exception e){
            System.out.println("Error" + e);
        }

    }


    /**
     * Adds a student's assignment to the database in the corresponding assignmentbundle table. This method does not
     * connect with the database but rather calls the addStudentAssignmentToDatabaseModel method in ClassroomModel
     * @param assignment StudentAssignment that has been marked
     */
    private void addStudentAssignmentToDatabase(StudentAssignment assignment){
        try{
        String sqlInsert = createQuery(assignment);
        boolean inDatabase = classroomModel.studentAssignmentInDatabase(assignment.getStudentID(),
                assignment.getAssignmentType(), this.classroomID);
        System.out.println(sqlInsert);
        this.classroomModel.addStudentAssignmentToDatabaseModel(sqlInsert, assignment, inDatabase);
        }catch (SQLException e){
            System.out.println("Error" + e);
        }

    }

    /**
     * Helper function to addStudentAssignmentToDatabase. Creates the SQL Query to be executed to add the assignment to the database.
     * If the assignment is already in the table of the assignment bundle it belongs to (i.e it has already been marked)
     * , then we will update the information in the database. Otherwise we will insert the data in a new row
     * (as this assignment has not been marked yet)
     * @param assignment StudentAssignment
     * @return SQL Query to be executed
     */
    private String createQuery(StudentAssignment assignment){

        String sqlInsert = "";
        try {
            String assignmentBundleID = classroomModel.getIDOfAssignmentBundle(assignment.getAssignmentType(),
                    this.classroomID);
            int numQuestions = assignment.getQuestions().size();
            StringBuilder questions = new StringBuilder();
            StringBuilder questionMarks = new StringBuilder("(?,?,?,?");
            for (int i = 1; i <= numQuestions; i++) {
                questions.append(", question").append(i);
                questionMarks.append(",?");
            }
            questions.append(", total)");
            questionMarks.append(")");
            //check if student assignment is already in database
            if (classroomModel.studentAssignmentInDatabase(assignment.getStudentID(), assignment.getAssignmentType(),
                    this.classroomID)) {
                StringBuilder sqlUpdate = new StringBuilder("UPDATE '" + assignmentBundleID + "' SET ");
                int q = 1;
                while (q <= numQuestions) {
                    sqlUpdate.append("question" + q + "=" + assignment.getQuestion(q).getFinalMark() + ", ");
                    q += 1;
                }
                sqlUpdate.append("total =").append(assignment.getFinalMark());
                sqlUpdate.append(" WHERE student_id=" + assignment.getStudentID());
                sqlInsert = sqlUpdate.toString();
            } else {
                sqlInsert = "INSERT INTO '" + assignmentBundleID + "'(student_id, student_name, document_name"
                        + questions + " VALUES " + questionMarks;
            }

        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return sqlInsert;
    }


}