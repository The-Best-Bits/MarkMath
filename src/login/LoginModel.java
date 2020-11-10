package login;

import java.sql.*;

import dbUtil.dbConnection;

public class LoginModel {
    Connection connection;

    public LoginModel(){
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    public boolean isPasswordAlreadySet() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT password FROM login";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();

        } catch (SQLException ex) {
            return false;
        }
        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean isPassword(String pass) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT password FROM login";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String passWord = rs.getString(1);


                if (passWord.equals(pass)) {
                    return true;
                }
            }

            return false;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null && rs != null) {
                stmt.close();
                rs.close();
            }
        }
    }
}
