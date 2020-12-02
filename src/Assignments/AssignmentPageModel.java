package Assignments;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AssignmentPageModel {
    Connection connection;

    public AssignmentPageModel(){
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    public ResultSet getGradeData(String bundleid) throws SQLException{
        return this.connection.createStatement().executeQuery("SELECT * FROM '" + bundleid + "'");
    }


    public void updateGradeData(int qid, float mark, float NewTotal, int stuid, String bundleid) throws SQLException{
        String query = "UPDATE '"+bundleid+"' SET question"+qid+" = '"+mark+"', total = '"+NewTotal+"' WHERE student_id = '"+stuid+"'";
        this.connection.prepareStatement(query).executeUpdate();
    }
}
