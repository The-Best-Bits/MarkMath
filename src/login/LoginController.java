package login;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class LoginController {

    @FXML
    private JFXTextField username;

    @FXML
    private Text signin;

    @FXML
    private JFXTextField password;

    @FXML
    private JFXButton loginButton;

    public LoginController() {
    }

    @FXML
    private void userLogin(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();
        if (user.equals("Sarah Xu")&&pass.equals("123456")){
            System.out.println("Welcome!");
        }
        else
            System.out.println("Wrong Username/Password!");

    }

}

