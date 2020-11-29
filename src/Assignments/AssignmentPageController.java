package Assignments;

import Classroom.ClassroomController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import dbUtil.dbConnection;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import markmath.entities.Student;
import markmath.entities.StudentAssignment;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AssignmentPageController<MyType> implements Initializable {

    @FXML
    private TableView<StudentAssignment> AssignmentTable;

    @FXML
    private TableColumn<StudentAssignment, String> idColumn;

    @FXML
    private TableColumn<StudentAssignment, String> nameColumn;

    @FXML
    private TableColumn<StudentAssignment, String> gradeColumn;

    @FXML
    private TableColumn<StudentAssignment, String> markColumn;

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
    private MyType temp;
    private Date lastClickTime;

    public void setClassroomID(String classroomID) {
        this.classroomID = classroomID;
    }

    public String getBundlename() {
        return bundlename;
    }

    public void setBundlename(String bundlename) {
        this.bundlename = bundlename;
    }

    public String getBundleid() {
        return bundleid;
    }

    public void setBundleid(String bundleid) {
        this.bundleid = bundleid;
    }



    @FXML
    void openClassroom(ActionEvent event) throws IOException {
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();}



    public void loadData(){
        try{
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            assert conn != null;
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM '" + this.bundleid + "'");
            rs.next();
            int count = rs.getMetaData().getColumnCount();
            LinkedHashMap<String, Float> out = new LinkedHashMap<>();
            for(int i=4; i<=count; i++){
                out.put(rs.getMetaData().getColumnName(i), rs.getFloat(i));
            }
            this.outline = out.keySet().stream().map(key -> key + ": " + out.get(key)).collect(
                    Collectors.joining(", "));
            while(rs.next()){
                LinkedHashMap<String, Float> map = new LinkedHashMap<>();
                for(int i=4; i<count; i++){
                    map.put(rs.getMetaData().getColumnName(i), rs.getFloat(i));
                }
                this.data.add(new StudentAssignment(
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getFloat("total"), map));
            }

        }catch(SQLException e){
            System.err.println("Error" + e);
        }

        this.idColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentID"));
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentName"));
        this.gradeColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("finalMark"));
        this.markColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("gradeMap"));
        this.AssignmentName.setText(this.bundlename);
        this.AssignmentOutline.setText(this.outline);
        this.AssignmentTable.setItems(this.data);
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
    public void editMark() {
        MyType row = (MyType) this.AssignmentTable.getSelectionModel().getSelectedItem();
        if (row==null) return;
        if (row!=temp) {
            temp = row;
            lastClickTime = new Date();
        }else{
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300){
                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL);

                popup.setTitle("Mark Editing");
                Label label1 = new Label(this.AssignmentTable.getSelectionModel().getSelectedItem().getGradeMap());
                TextField field1 = new TextField();
                field1.setPromptText("Enter Question ID as an integer (eg. 1)");
                TextField field2 = new TextField();
                field2.setPromptText("Enter Custom Mark as a decimal (eg. 7.5)");
                Label label2 = new Label();
                Button button1 = new Button("Submit change");

                button1.setOnAction(e -> {
                    if(isInteger(field1.getText()) && isFloat(field2.getText())){
                    int qid = Integer.parseInt(field1.getText());
                    float mark = Float.parseFloat(field2.getText());
                    int stuid = Integer.parseInt(AssignmentTable.getSelectionModel().getSelectedItem().getStudentID());
                    try{
                        Connection conn = dbConnection.getConnection();
                        assert conn != null;
                        String query = "UPDATE '"+this.bundleid+"' SET question"+qid+" = '"+mark+"' WHERE student_id = '"+stuid+"'";
                        conn.prepareStatement(query).executeUpdate();
                        loadData();
                        popup.close();
                    }catch(SQLException exc){
                        System.err.println("Error" + exc);}
                    }else{
                        label2.setText("Please enter an expected value.");
                    }
                });

                VBox layout= new VBox(10);
                layout.getChildren().addAll(label1, field1, field2, button1);
                layout.setAlignment(Pos.CENTER);
                Scene scene1= new Scene(layout, 300, 250);

                popup.setScene(scene1);
                popup.showAndWait();
            }
        }}


    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}