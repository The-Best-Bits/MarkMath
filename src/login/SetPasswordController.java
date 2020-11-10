package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SetPasswordController {

    setPasswordModel setPasswordModel = new setPasswordModel();

    @FXML
    private JFXButton setPasswordButton;

    @FXML
    private JFXPasswordField password;

    @FXML
    private Label invalidPassword;

    @FXML
    void setPassword(ActionEvent event) {
        try {
            if (!this.password.getText().trim().isEmpty()) {
                this.setPasswordModel.setPassword(this.password.getText());
                Stage stage = (Stage)this.setPasswordButton.getScene().getWindow();
                stage.close();

                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                Pane root = loader.load(getClass().getResource("login.fxml").openStream());
                Scene scene = new Scene(root);
                newStage.setScene(scene);
                newStage.show();
            } else {
                this.invalidPassword.setText("You must type in a new password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}