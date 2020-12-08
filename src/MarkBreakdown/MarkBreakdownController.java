package MarkBreakdown;

import Assignments.AssignmentPageController;
import StudentMarks.StudentMarksController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MarkBreakdownController {
    MarkBreakdownModel markBreakdownModel = new MarkBreakdownModel();

    private String studentID;
    private String classroomID;
    private String assignmentID;
    private ArrayList<String> gradeBreakdown;
    private ArrayList<String> possibleGradeBreakdown;

    private StudentMarksController studentMarksController;

    private AssignmentPageController assignmentPageController;


    @FXML
    private Text assignment_name_display;
    @FXML
    private Text student_name_display;
    @FXML
    private Text class_name_display;

    @FXML
    private Button editMarksButton;

    @FXML
    private TableView<BreakdownData> breakdown_table;

    @FXML
    private ObservableList<BreakdownData> breakdownData;

    @FXML
    private TableColumn<BreakdownData, String> question_column;

    @FXML
    private TableColumn<BreakdownData, String> mark_column;

    @FXML
    private TableColumn<BreakdownData, String> possible_mark_column;

    public void setStudentMarksController(StudentMarksController controller) {
        this.studentMarksController = controller;
    }

    public void setAssignmentPageController(AssignmentPageController controller) {
        this.assignmentPageController = controller;
    }


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
    public void setPossibleGradeBreakdown(ArrayList<String> possibleGradeBreakdown) {
        this.possibleGradeBreakdown = possibleGradeBreakdown;
    }

    @FXML
    public void loadPage() throws Exception {
        this.assignment_name_display.setText(this.markBreakdownModel.getAssignmentName(this.assignmentID));
        this.class_name_display.setText(this.markBreakdownModel.getClassroomName(this.classroomID));
        this.student_name_display.setText(this.markBreakdownModel.getStudentName(this.studentID));
        this.setGradeBreakdown(markBreakdownModel.getGradeBreakdown(studentID, assignmentID));
        this.setPossibleGradeBreakdown(markBreakdownModel.getTotalPossibleMarks(assignmentID));

        try {
            this.breakdownData = FXCollections.observableArrayList();

            for (int i = 0; i < this.gradeBreakdown.size() - 1; i++) {
                this.breakdownData.add(new BreakdownData(Integer.toString(i + 1), gradeBreakdown.get(i), this.possibleGradeBreakdown.get(i)));
            }

            this.breakdownData.add(new BreakdownData("Total", gradeBreakdown.get(this.gradeBreakdown.size() - 1), this.possibleGradeBreakdown.get(this.gradeBreakdown.size() - 1)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.question_column.setCellValueFactory(new PropertyValueFactory<BreakdownData, String>("questionNumber"));
        this.mark_column.setCellValueFactory(new PropertyValueFactory<BreakdownData, String>("mark"));
        this.possible_mark_column.setCellValueFactory(new PropertyValueFactory<BreakdownData, String>("possibleMark"));


        this.breakdown_table.setItems(null);
        this.breakdown_table.setItems(this.breakdownData);
    }

    @FXML
    public void editMarks(ActionEvent event) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Mark Editing");
        TextField field1 = new TextField();
        field1.setPromptText("Enter Question ID as an integer (eg. 1)");
        field1.setMaxWidth(250);
        TextField field2 = new TextField();
        field2.setPromptText("Enter Mark as a decimal (eg. 7.5)");
        field2.setMaxWidth(250);
        Label label2 = new Label();
        Button button1 = new Button("Submit change");

        button1.setOnAction(e -> {
            if (isPositiveInteger(field1.getText()) & isPositiveFloat(field2.getText())) {
                int questionNumber = Integer.parseInt(field1.getText());
                float newMark = Float.parseFloat(field2.getText());
                if (questionNumber <= possibleGradeBreakdown.size() - 1 && newMark <= Float.parseFloat(possibleGradeBreakdown.get(questionNumber - 1))) {
                    float oldMark = Float.parseFloat(gradeBreakdown.get(questionNumber - 1));
                    float oldTotal = Float.parseFloat(gradeBreakdown.get(gradeBreakdown.size() -1));
                    float NewTotal = oldTotal - oldMark + newMark;

                    try{
                        markBreakdownModel.updateGradeData(questionNumber, newMark, NewTotal, this.studentID, this.assignmentID);
                        loadPage();

                        if (this.studentMarksController != null) {
                            this.studentMarksController.loadData();
                        } else if (this.assignmentPageController != null) {
                            this.assignmentPageController.loadData();
                        }
                        popup.close();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                } else {
                    label2.setText("Please enter valid values");
                }
            } else{
                label2.setText("Please enter valid values");
            }
        });

        VBox layout= new VBox(15);
        layout.getChildren().addAll(field1, field2, label2, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);
        popup.setScene(scene1);
        popup.showAndWait();

    }

    public static boolean isPositiveInteger(String str) {
        try {
            int num = Integer.parseInt(str);
            return num > 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isPositiveFloat(String str) {
        try {
            float decimal = Float.parseFloat(str);
            return decimal > 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}