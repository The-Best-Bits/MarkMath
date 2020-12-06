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
    StudentMarksModel studentMarksModel = new StudentMarksModel();
    private String studentID;
    private String classroomID;

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

    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
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

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setClassID(String classID) {
        this.classroomID = classID;
    }


    @FXML
    public void loadPage() throws Exception {
        this.class_name_display.setText(this.studentMarksModel.getClassroomName(classroomID));
        this.student_name_display.setText(this.studentMarksModel.getStudentName(studentID));

        try {
            this.marksData = FXCollections.observableArrayList();

            ArrayList<String> assignmentsInClass = studentMarksModel.getAssignmentsInClass(this.classroomID);

            for (String assignment: assignmentsInClass) {
                String assignment_id = assignment;
                String assignment_name = studentMarksModel.getAssignmentName(assignment);
                if (studentMarksModel.tableExists(assignment_id)) {
                    String totalMark = studentMarksModel.getTotalMark(studentID, assignment_id);
                    this.marksData.add(new MarksData(assignment_id, assignment_name, totalMark));
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
        } else if (row == temp) {
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

                String assignment_id = this.marks_table.getSelectionModel().getSelectedItem().getAssignmentID();
                MarkBreakdownController markBreakdownController = Loader.getController();
                markBreakdownController.setAssignmentID(assignment_id);
                markBreakdownController.setStudentID(this.studentID);
                markBreakdownController.setClassID(this.classroomID);
                markBreakdownController.loadPage();

                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }
    }

    @FXML
    private void back(ActionEvent event) throws Exception {
        FXMLLoader Loader = new FXMLLoader();

        Loader.setLocation(getClass().getResource("/Classroom/Classroom.fxml"));

        try {
            Loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ClassroomController classroomController = Loader.getController();
        classroomController.setClassroomID(this.classroomID);
        classroomController.loadData();
        SingleSelectionModel<Tab> selectionModel = classroomController.tab_pane.getSelectionModel();
        selectionModel.select(1);
        Parent p = Loader.getRoot();
        Scene scene = new Scene(p);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}