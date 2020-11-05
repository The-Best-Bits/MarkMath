package MarkmathApp;

import java.sql.*;

import dbUtil.dbConnection;

public class Model {
    Connection connection;

    public Model(){
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

    /*public void showUser(){
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM login;");
            System.out.println(results.getString(1));
        } catch (SQLException ex) {
            System.out.println("Could not retrieve data");
        }
    }*/
}
