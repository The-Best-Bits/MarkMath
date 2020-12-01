package Assignments;

import Classroom.ClassroomController;
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


    /**
     *
     */
    public void loadData(){
        UpdateFromDB();
        this.idColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentID"));
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("studentName"));
        this.gradeColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("finalMark"));
        this.markColumn.setCellValueFactory(new PropertyValueFactory<StudentAssignment, String>("BreakdownString"));
        this.AssignmentName.setText(this.bundlename);
        this.AssignmentOutline.setText(this.outline+ ", Total: " + this.fullMark);
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
            } }catch(SQLException e){
            System.err.println("Error" + e);
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
    public void editMark() {
        StudentAssignment curr = this.AssignmentTable.getSelectionModel().getSelectedItem();
        MyType row = (MyType) curr;
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
                Label label1 = new Label("CURRENT MARK: " +curr.getBreakdownString());
                TextField field1 = new TextField();
                field1.setPromptText("Enter Question ID as an integer (eg. 1)");
                field1.setPrefColumnCount(40);
                TextField field2 = new TextField();
                field2.setPromptText("Enter Custom Mark as a decimal (eg. 7.5)");
                field2.setPrefColumnCount(40);
                Label label2 = new Label();
                Button button1 = new Button("Submit change");

                button1.setOnAction(e -> {
                    if(isPositiveInteger(field1.getText()) & isPositiveFloat(field2.getText())){
                        int qid = Integer.parseInt(field1.getText());
                        float mark = Float.parseFloat(field2.getText());
                        if(qid <= curr.getOutline().getQuestionToMarks().size() && mark <= curr.getOutline(
                        ).getQuestionToMarks().get("question"+qid)){
                            float NewTotal = curr.getFinalMark() - curr.getFinalMarkBreakdown().get("question"+qid) + mark;
                            int stuid = Integer.parseInt(curr.getStudentID());
                            try{
                                PageModel.updateGradeData(qid, mark, NewTotal, stuid, this.bundleid);
                                loadData();
                                popup.close();
                            }catch(SQLException exc){
                                System.err.println("Error" + exc);}
                        }else{
                            label2.setText("Please enter valid values");}
                    }else{
                        label2.setText("Please enter valid values");}
                });

                VBox layout= new VBox(15);
                layout.getChildren().addAll(label1, field1, field2, label2, button1);
                layout.setAlignment(Pos.CENTER);
                Scene scene1= new Scene(layout, 400, 300);
                popup.setScene(scene1);
                popup.showAndWait();
            }
        }}


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