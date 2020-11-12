package login;

import java.sql.*;

import dbUtil.dbConnection;

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
