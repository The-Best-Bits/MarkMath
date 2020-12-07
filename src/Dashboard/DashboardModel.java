package Dashboard;

import dbUtil.dbConnection;

import java.sql.*;

public class DashboardModel {
    /**
     * Following Clean Architecture, this is a gateway class that interacts with the database. It is specifically
     * responsible for sending and retrieving data to and from the database in accordance with the actions users can
     * perform on the Dashboard fxml page.
     */
    Connection connection;

    public DashboardModel(){
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
     * Adds a classroom with the given classID and className to the database. If there already exists a classroom
     * with the given classID in the database this classroom will not be added and this method will return false.
     * @param classID the class ID
     * @param className the class name
     * @return a boolean representing whether this classroom was added to the database
     */
    public boolean addClassroomToDatabase(String classID, String className) {
        PreparedStatement stmt;
        ResultSet rs;
        String sqlClassExists = "SELECT class_id FROM classrooms WHERE class_id =" + classID;
        String sqlInsert = "INSERT INTO classrooms(class_id, class_name) VALUES (?,?)";

        try {
            //first check if there already exists a classroom with this classID in the database
            rs = this.connection.createStatement().executeQuery(sqlClassExists);
            if (rs.next()) {
                return false;
            } else {
                stmt = this.connection.prepareStatement(sqlInsert);
                stmt.setString(1, classID);
                stmt.setString(2, className);
                stmt.execute();
                stmt.close();
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error" + e);
        }

        return false;
    }

    /**
     * Returns all of the data in the classrooms table in the database
     * @return ResultSet containing all of the data from the classrooms table in the databse
     */
    public ResultSet getClassroomData(){
        try{
            return this.connection.createStatement().executeQuery("SELECT * FROM classrooms");

        }catch(SQLException e){
            System.out.println("Error retrieving classrooms data" + e);
        }

        return null;
    }


}
