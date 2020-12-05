package Settings;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import markmath.entities.Classroom;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SettingsController {

    @FXML
    private JFXTextField newPass;

    @FXML
    private JFXButton savePassword;

    @FXML
    private JFXTextField cfrmPass;

    private dbConnection dbc;

    @FXML
    private Label passRemind;

    private SettingsModel model = new SettingsModel();

    /**
     *Changes the password if and only if newPass and cfrmPass matches and none of the text fields are left blank.
     * @param event when user clicks the save button
     */

    @FXML
    void savePassword(ActionEvent event) {

        String newpassword = newPass.getText();
        String confirmpass = cfrmPass.getText();
        if (newpassword.isEmpty()|| confirmpass.isEmpty()){
            passRemind.setText("Error! Please fill all fields.");
        }
        else{
            if (!newpassword.equals(confirmpass)){
                passRemind.setText("Error! Please make sure the passwords match.");
            }
            else{
                try{
                    model.updatePassword(newpassword);
                    passRemind.setText("Success! Your new password has been saved.");
                }
                catch(SQLException e){
                    System.out.println("Error, could not update password");
                    e.printStackTrace();
                }
            }

        }

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

}
