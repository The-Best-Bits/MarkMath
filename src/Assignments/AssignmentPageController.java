package Assignments;

import Classroom.ClassroomController;
import MarkBreakdown.MarkBreakdownController;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import entities.AssignmentOutline;
import entities.StudentAssignment;

import java.io.IOException;
import java.net.URL;
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

    @FXML
    void openClassroom(ActionEvent event) throws IOException {
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();}


    /**
     *
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

    public LinkedHashMap<String, Float> CreateMap(ResultSet rs, int count) throws SQLException{
        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        for(int i=4; i<count; i++){
            map.put(rs.getMetaData().getColumnName(i), rs.getFloat(i));
        }
        return map;
    }

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
                markBreakdownController.setAssignmentPageController(this);
                markBreakdownController.setAssignmentID(this.bundleid);
                markBreakdownController.setStudentID(studentID);
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