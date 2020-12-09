package login;

import java.sql.*;

import dbUtil.dbConnection;

/**
 * This class interact with the database for the LoginController. It checks if a password already exists
 * and check if the user inputs the correct password when login.
 */

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

    /**
     * Checks if there is a password in the database.
     * @return
     * @throws Exception database exception
     */
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

    /**
     * Check if the password that user inputs is the matching password with the password stored in the
     * database. Return true iff the passwords match.
     * @param pass the password user inputs when login.
     * @return true iff the password matches.
     * @throws Exception database exception
     */
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
