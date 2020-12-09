package Settings;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SettingsModel {
    /**
     * This class works with the SettingsController class and acts as a gateway class to read and update
     * information in the data base. The user will be able to change and store their new password.
     */

    Connection connection;

    public SettingsModel(){
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    /**
     * Updates the current password in the database to the given password 'newpassword'
     * @param newPassword the new password
     */
    public void updatePassword(String newPassword) throws SQLException{

        Statement stat = null;

        try {
            stat = this.connection.createStatement();
            stat.execute("UPDATE login SET password = '"+newPassword+"' ");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        finally{
            if(stat != null){
                stat.close();
            }
        }
    }
}
