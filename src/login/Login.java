package login;

import Server.SocketIOServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**The main class of the app; starts the app by showing the login page. To run the app, this class needs to be run. 
**/
public class Login extends Application {

    LoginModel loginModel = new LoginModel();
    private static SocketIOServer server;

    public static void main(String[] args) {
        launch(args);

    }

    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = openLogin();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/art/icon.png")));
        stage.show();
        //start the server
        server = new SocketIOServer();
        server.start();
        //configure app to screen size of current device
        

    }
    /**Method that runs the first page of the application, the login page. If the user is opening the page for the first time, 
    it lets them choose a password.
    **/
    public Parent openLogin() throws Exception{
        if (this.loginModel.isPasswordAlreadySet()) {
            return FXMLLoader.load(getClass().getResource("login.fxml"));
        } else {
            return FXMLLoader.load(getClass().getResource("SetPassword.fxml"));
        }
    }

    public static SocketIOServer getServer(){return server;}
}
