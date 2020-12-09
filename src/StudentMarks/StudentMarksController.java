package StudentMarks;

import Classroom.ClassroomController;
import MarkBreakdown.MarkBreakdownController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class StudentMarksController<MyType> {
    /**
     * This is a controller class that directly interacts with the UI for the StudentMarks.fxml page. It is responsible
     * for populating the table in the page as well as initializing a MarkBreakdownController and opening the
     * MarkBreakdown.fxml page with that controller.
     */
    StudentMarksModel studentMarksModel = new StudentMarksModel();

    private String studentID;
    private String classroomID;

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setClassID(String classID) {
        this.classroomID = classID;
    }

    @FXML
    private Text student_name_display;
    @FXML
    private Text class_name_display;

    @FXML
    private Button backButton;

    @FXML
    private TableView<MarksData> marks_table;

    @FXML
    private ObservableList<MarksData> marksData;

    @FXML
    private TableColumn<MarksData, String> id_column;

    @FXML
    private TableColumn<MarksData, String> name_column;

    @FXML
    private TableColumn<MarksData, String> total_column;

    private MyType temp;
    private Date lastClickTime;

    /**
     * Helper function for opening pages.
     * @throws IOException
     */
    private void openPage(Parent p, Stage stage) throws IOException {
        Scene mainPage = new Scene(p);
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Opens the main "Dashboard page" that displays a table with all of the classrooms of this user
     * @param event Classroom button is clicked
     * @throws IOException
     */
    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent p = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openPage(p, stage);
    }

    /**
     * Opens a settings page...
     * @param event Settings button is clicked
     * @throws IOException
     */
    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent p = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openPage(p, stage);
    }

    /**
     * loads the classroom name and student name fields, as well as the data in the table.
     * @throws Exception
     */
    @FXML
    public void loadPage() throws Exception {
        this.class_name_display.setText(this.studentMarksModel.getClassroomName(classroomID));
        this.student_name_display.setText(this.studentMarksModel.getStudentName(studentID));
        this.loadTableData();
    }

    /**
     * loads the data in the table in this page.
     * @throws Exception
     */
    @FXML
    public void loadTableData() throws Exception {
        try {
            this.marksData = FXCollections.observableArrayList();

            ArrayList<String> assignmentsInClass = studentMarksModel.getAssignmentsInClass(this.classroomID);

            for (String assignmentID: assignmentsInClass) {
                String assignment_name = studentMarksModel.getAssignmentName(assignmentID);
                if (studentMarksModel.tableExists(assignmentID) && studentMarksModel.studentDidAssignment(studentID, assignmentID)) {
                    String totalMark = studentMarksModel.getTotalMark(studentID, assignmentID) + "/" +
                            studentMarksModel.getTotalMark("0", assignmentID);
                    this.marksData.add(new MarksData(assignmentID, assignment_name, totalMark));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.id_column.setCellValueFactory(new PropertyValueFactory<MarksData, String>("assignmentID"));
        this.name_column.setCellValueFactory(new PropertyValueFactory<MarksData, String>("assignmentName"));
        this.total_column.setCellValueFactory(new PropertyValueFactory<MarksData, String>("grade"));


        this.marks_table.setItems(null);
        this.marks_table.setItems(this.marksData);
    }

    /**
     * When a user clicks on a specific row (corresponding to an assignment that a student completed), this method
     * will open up a page that displays the grade breakdown for that assignment.
     */
    @FXML
    private void handleRowSelect() throws Exception {
        //Detect double click
        MyType row = (MyType) this.marks_table.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (row != temp) {
            temp = row;
            lastClickTime = new java.util.Date();
        } else {
            java.util.Date now = new java.util.Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300) { //another click registered in 300 millis
                //Load a page for the student
                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("/MarkBreakdown/MarkBreakdown.fxml"));

                try {
                    Loader.load();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*gets the controller from the loader of the mark breakdown page, and then sets attributes */
                MarkBreakdownController markBreakdownController = Loader.getController();
                String assignmentID = this.marks_table.getSelectionModel().getSelectedItem().getAssignmentID();
                markBreakdownController.setStudentMarksController(this);
                markBreakdownController.setAssignmentID(assignmentID);
                markBreakdownController.setStudentID(this.studentID);
                markBreakdownController.setClassID(this.classroomID);
                markBreakdownController.loadPage();

                /*opens mark breakdown page for the assignment */
                Parent p = Loader.getRoot();
                Stage stage = new Stage();
                openPage(p, stage);
            } else {
                lastClickTime = new Date();
            }
        }
    }

    /**
     * goes to the classroom page that this assignment was in, open to the students tab of that page.=,
     * and populates the tables in that page.
     * @param event
     * @throws Exception
     */
    @FXML
    private void back(ActionEvent event) throws Exception {
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("/Classroom/Classroom.fxml"));

        try {
            Loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*gets classroomController from loader, sets classroomID and populates table*/
        ClassroomController classroomController = Loader.getController();
        classroomController.setClassroomID(this.classroomID);
        classroomController.loadData();

        /* Sets the top tab to the Student tab*/
        SingleSelectionModel<Tab> selectionModel = classroomController.tab_pane.getSelectionModel();
        selectionModel.select(1);

        /*loads page*/
        Parent p = Loader.getRoot();
        Stage stage = (Stage) backButton.getScene().getWindow();
        openPage(p, stage);
    }
}