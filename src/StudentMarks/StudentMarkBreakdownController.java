package StudentMarks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class StudentMarkBreakdownController {
    private String studentID;
    private String classroomID;
    private String assignmentID;
    private ArrayList<String> gradeBreakdown;

    private StudentMarksModel studentMarksModel = new StudentMarksModel();


    @FXML
    private Text assignment_name_display;
    @FXML
    private Text student_name_display;
    @FXML
    private Text class_name_display;

    @FXML
    private TableView<BreakdownData> breakdown_table;

    @FXML
    private ObservableList<BreakdownData> breakdownData;

    @FXML
    private TableColumn<BreakdownData, String> question_column;

    @FXML
    private TableColumn<BreakdownData, String> mark_column;

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setClassID(String classID) {
        this.classroomID = classID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }

    public void setGradeBreakdown(ArrayList<String> gradeBreakdown) {
        this.gradeBreakdown = gradeBreakdown;
    }

    @FXML
    public void loadPage() throws Exception {
        this.assignment_name_display.setText(this.studentMarksModel.getAssignmentName(assignmentID));
        this.class_name_display.setText(this.studentMarksModel.getClassroomName(classroomID));
        this.student_name_display.setText(this.studentMarksModel.getStudentName(studentID));

        try {
            this.breakdownData = FXCollections.observableArrayList();

            for (int i = 0; i < this.gradeBreakdown.size() - 1; i++) {
                this.breakdownData.add(new BreakdownData(Integer.toString(i + 1), gradeBreakdown.get(i)));
            }

            this.breakdownData.add(new BreakdownData("Total", gradeBreakdown.get(this.gradeBreakdown.size() - 1)));

            ArrayList<String> assignmentsInClass = studentMarksModel.getAssignmentsInClass(this.classroomID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.question_column.setCellValueFactory(new PropertyValueFactory<BreakdownData, String>("questionNumber"));
        this.mark_column.setCellValueFactory(new PropertyValueFactory<BreakdownData, String>("mark"));


        this.breakdown_table.setItems(null);
        this.breakdown_table.setItems(this.breakdownData);
    }
}