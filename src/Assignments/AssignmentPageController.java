package Assignments;

import Classroom.ClassroomController;
import MarkBreakdown.MarkBreakdownController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import markmath.entities.AssignmentOutline;
import markmath.entities.StudentAssignment;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AssignmentPageController<MyType> implements Initializable {
    AssignmentPageModel PageModel = new AssignmentPageModel();

    @FXML
    private TableView<StudentAssignment> AssignmentTable;

    @FXML
    private TableColumn<StudentAssignment, String> idColumn;

    @FXML
    private TableColumn<StudentAssignment, String> nameColumn;

    @FXML
    private TableColumn<StudentAssignment, String> gradeColumn;

    @FXML
    private Text AssignmentName;

    @FXML
    private Text AssignmentOutline;

    @FXML
    private JFXButton backButton;

    private dbConnection db;
    private ObservableList<StudentAssignment> data;
    private Stage stage;
    private String bundleid;
    private String bundlename;
    private String classroomID;
    private String outline;
    private float fullMark;
    private MyType temp;
    private Date lastClickTime;

    public void setClassroomID(String classroomID) {
        this.classroomID = classroomID;
    }

    public void setBundlename(String bundlename) {
        this.bundlename = bundlename;
    }

    public void setBundleid(String bundleid) {
        this.bundleid = bundleid;
    }

    /**
     * Process event of clicking onto Classroom button and direct to classroom page
     * @param event
     * @throws IOException
     */
    @FXML
    void openClassroom(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    /**
     * Process event of clicking onto Settings button and direct to Setting page
     * @param event
     * @throws IOException
     */
    @FXML
    void openSetting(ActionEvent event) throws IOException {
        Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Settings/Settings.fxml"));
        Scene mainPage = new Scene(mainPageParent);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainPage);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();}


    /**
     * Helper for UpdateFromDB; create a linked HashMap using data of grade breakdown received from database
     * @param rs
     * @param count
     * @return
     * @throws SQLException
     */
    public LinkedHashMap<String, Float> CreateMap(ResultSet rs, int count) throws SQLException{
        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        for(int i=4; i<count; i++){
            map.put(rs.getMetaData().getColumnName(i), rs.getFloat(i));
        }
        return map;
    }


    /**
     * Helper for loadData; update attributes of this controller with data from database received by model
     */
    public void UpdateFromDB(){
        try{
            ResultSet rs = PageModel.getGradeData(this.bundleid);
            this.data = FXCollections.observableArrayList();
            rs.next();
            int count = rs.getMetaData().getColumnCount();
            LinkedHashMap<String, Float> out = CreateMap(rs, count);
            this.outline = out.keySet().stream().map(key -> key + ": " + out.get(key)).collect(
                    Collectors.joining(", "));
            this.fullMark = rs.getFloat(count);
            while(rs.next()){
                LinkedHashMap<String, Float> map = CreateMap(rs, count);
                StudentAssignment NewAssignment = new StudentAssignment(
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getFloat("total"), map);
                NewAssignment.setOutline(new AssignmentOutline(this.bundlename, out));
                this.data.add(NewAssignment);
            }
        }catch(SQLException e){
            System.err.println("Error:" + e);
        }
    }


    /**
     * Display attributes data onto the Assignment page
     */
    public void loadData(){
        UpdateFromDB();
        this.idColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentID"));
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentName"));
        this.gradeColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("finalMark"));
        this.AssignmentName.setText(this.bundlename);
        this.AssignmentOutline.setText(this.outline+ ", Total: " + this.fullMark);
        this.AssignmentTable.setItems(null);
        this.AssignmentTable.setItems(this.data);
    }


    /**
     * Process event of clicking onto Back button and direct to the corresponding bundle page
     * @param event
     * @throws IOException
     */
    public void backToBundle(ActionEvent event) throws IOException{
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("/Classroom/Classroom.fxml"));
        try {
            Loader.load();
        }catch (Exception e){
            e.printStackTrace();
        }
        ClassroomController classroomController = Loader.getController();
        classroomController.setClassroomID(this.classroomID);
        classroomController.loadData();
        Parent mainPageParent = Loader.getRoot();
        Scene mainPage = new Scene(mainPageParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

    /**
     * Process row selection in the table; load the detailed grade breakdown pop-up window
     * @throws Exception
     */
    @FXML
    public void handleRowSelect() throws Exception {
        MyType row = (MyType) this.AssignmentTable.getSelectionModel().getSelectedItem();
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

                String studentID = this.AssignmentTable.getSelectionModel().getSelectedItem().getStudentID();

                MarkBreakdownController markBreakdownController = Loader.getController();
                markBreakdownController.setAssignmentID(this.bundleid);
                markBreakdownController.setStudentID(studentID);
                markBreakdownController.setClassID(this.classroomID);
                markBreakdownController.loadPage();

                Parent p = Loader.getRoot();
                Scene scene = new Scene(p);

                Stage stage = new Stage();
                stage.setOnCloseRequest(e -> {
                    loadData();
                    stage.close();});
                stage.setScene(scene);
                stage.show();

            } else {
                lastClickTime = new Date();
            }
        }
    }

}