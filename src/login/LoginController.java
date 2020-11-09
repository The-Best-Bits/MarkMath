package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import MarkmathApp.Controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {

    login.LoginModel loginModel = new login.LoginModel();

    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private JFXButton loginButton;

    public LoginController() {
    }

    @FXML
    private void userLogin(ActionEvent event) {
        try {
            if (this.loginModel.isLogin(this.username.getText(), this.password.getText())) {
                System.out.println("Right credentials");
                Stage stage = (Stage)this.loginButton.getScene().getWindow();
                stage.close();
                Login();
            }
            else
                System.out.println("Wrong Username/Password!");
        } catch (Exception localException) {
            System.out.println("Error!");
        }
    }

    public void Login() {
        try {
            Stage userStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(getClass().getResource("/MarkmathApp/MarkMath.fxml").openStream());
            Scene scene = new Scene(root);
            userStage.setScene(scene);
            userStage.setTitle("Dashboard");
            userStage.setResizable(false);
            userStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

