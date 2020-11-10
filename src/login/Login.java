package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Application {

    LoginModel loginModel = new LoginModel();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = openLogin();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public Parent openLogin() throws Exception{
        if (this.loginModel.isPasswordAlreadySet()) {
            return FXMLLoader.load(getClass().getResource("login.fxml"));
        } else {
            return FXMLLoader.load(getClass().getResource("SetPassword.fxml"));
        }
    }
}
