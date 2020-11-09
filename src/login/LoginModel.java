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

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public boolean isLogin(String user, String pass) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT username, password FROM login";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String userName = rs.getString(1);
                String passWord = rs.getString(2);

                if (userName.equals(user) && passWord.equals(pass)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            return false;
        }

        finally {
            {
                stmt.close();
                rs.close();
            }
        }
    }
}
