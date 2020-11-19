package Assignments;

import com.jfoenix.controls.JFXTextField;
import dbUtil.dbConnection;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import markmath.entities.StudentAssignment;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;

public class AssignmentPageController implements Initializable {

    @FXML
    private TableView<GradeData> AssignmentTable;

    @FXML
    private TableColumn<GradeData, String> idColumn;

    @FXML
    private TableColumn<GradeData, String> nameColumn;

    @FXML
    private TableColumn<GradeData, String> gradeColumn;

    @FXML
    private TableColumn<GradeData, ObservableMapValue<String, Float>> markColumn;

    @FXML
    private Text AssignmentName;

    private dbConnection db;
    private ObservableList<GradeData> data;
    private Stage stage;
    private String bundleid;
    private String bundlename;

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

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + this.bundlename);
            rs.next();
            while(rs.next()){
                ObservableMap<String, Float> map = FXCollections.observableMap(new HashMap<>());
                for (int i=4; i<rs.getMetaData().getColumnCount(); i++){
                    map.put(rs.getMetaData().getColumnName(i), rs.getFloat(i));
                }
                this.data.add(new GradeData(
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getFloat("total"), map));
            }

        }catch(SQLException e){
            System.err.println("Error" + e);
        }

        this.idColumn.setCellValueFactory(new PropertyValueFactory<GradeData, String>("ID"));
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<GradeData, String>("name"));
        this.gradeColumn.setCellValueFactory(new PropertyValueFactory<GradeData, String>("grade"));
//        this.markColumn.setCellValueFactory(new PropertyValueFactory<GradeData, ObservableMapValue<String, Float>>("breakdown"));
        this.AssignmentName.setText(this.bundlename);
        this.AssignmentTable.setItems(null);
        this.AssignmentTable.setItems(this.data);
    }


}