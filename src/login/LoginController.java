package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {

    LoginModel loginModel = new LoginModel();

    @FXML
    private JFXTextField password;

    @FXML
    private JFXButton loginButton;

    @FXML
    private Label wrongPassword;

    @FXML
    private void userLogin(ActionEvent event) {
        try {
            if (this.loginModel.isPassword(this.password.getText())) {
                Stage stage = (Stage)this.loginButton.getScene().getWindow();
                stage.close();

                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                Pane root = loader.load(getClass().getResource("/MarkmathApp/MarkMath.fxml").openStream());
                Scene scene = new Scene(root);
                newStage.setScene(scene);
                newStage.show();
            }
            else {
                this.wrongPassword.setText("Wrong password! Please try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

