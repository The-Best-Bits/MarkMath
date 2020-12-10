package Assignments;

import dbUtil.dbConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AssignmentPageModel {
    /**
     * Responsible for direct interaction with database for Assignment page
     */
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

    /**
     * Get result set for grade data from the database
     * @param bundleid
     * @return
     * @throws SQLException
     */
    public ResultSet getGradeData(String bundleid) throws SQLException{
        return this.connection.createStatement().executeQuery("SELECT * FROM '" + bundleid + "'");
    }

}
