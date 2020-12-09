package login;

import java.sql.*;

import dbUtil.dbConnection;

/**
 * This class interacts with the database for the SetPasswordController. It takes user input and
 * stores it in the database.
 */
public class SetPasswordModel {
    Connection connection;

    public SetPasswordModel(){
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
     * Set the password to the user-input. Stores the password in the database.
     * @param pass the password that user inputs
     */
    public void setPassword(String pass) {
        Statement stmt = null;

        String sql = "INSERT INTO login (password) VALUES ('" + pass + "')";

        try {
            stmt = this.connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
