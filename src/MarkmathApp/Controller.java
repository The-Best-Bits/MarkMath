package MarkmathApp;

import javafx.fxml.Initializable;


import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    Model Model = new Model();
    public void initialize(URL url, ResourceBundle rb) {
        if (this.Model.isDatabaseConnected()) {
            System.out.print("Connected");
        } else {
            System.out.print("Not Connected");
        }

        //this.Model.showUser();//
    }
}
