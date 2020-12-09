package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;

public class LoginController {
    /**
     * This class interacts directly with the UI of the login page. The user will be able to login by entering
     * the correct password and access the dashboard page iff the password is correct.
     */

    LoginModel loginModel = new LoginModel();

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton loginButton;

    @FXML
    private Label wrongPassword;

    /**
     * The onAction method for the login button. When pressed, takes the user input and calls isPassword in the
     * loginModel. If the password is correct, open the dashboard page.
     * @param event user clicks login button.
     */
    @FXML
    private void userLogin(ActionEvent event) {
        try {
            if (this.loginModel.isPassword(this.password.getText())) {
                Stage stage = (Stage)this.loginButton.getScene().getWindow();
                stage.close();

                Parent mainPageParent = FXMLLoader.load(getClass().getResource("/Dashboard/Dashboard.fxml"));
                Scene mainPage = new Scene(mainPageParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(mainPage);
                window.show();
            }
            else {
                this.wrongPassword.setText("Wrong password! Please try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

