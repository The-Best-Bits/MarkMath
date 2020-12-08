package StudentMarks;

import dbUtil.dbConnection;

import java.sql.*;
import java.util.ArrayList;

/**
 * Following Clean Architecture, this is a gateway class that interacts with the database. It is specifically
 * responsible for sending and retrieving data to and from the database in accordance with the actions users can
 * perform on the Dashboard fxml page.
 */
public class StudentMarksModel {

    Connection connection;

    /**
     *  Initializes model and connects it to database
     **/
    public StudentMarksModel() {
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
     * returns the name of a classroom with a certain ID
     * @param classID
     * @return return class_name for row in clasrooms in database with column class_id = classID
     * @throws SQLException
     */
    public String getClassroomName(String classID) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT class_name FROM classrooms WHERE class_id = '" + classID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.getString(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return ("");
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null){
                rs.close();
            }
        }
    }

    /**
     * gets assignment name from ID
     * @param assignmentID
     * @return return assignment_name for row in clasrooms in database with column
     * assignmentbundle_id = assignmentID
     * @throws SQLException
     */
    public String getAssignmentName(String assignmentID) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT assignment_name FROM AssignmentBundles WHERE assignmentbundle_id = '" + assignmentID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.getString(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return ("");
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null){
                rs.close();
            }
        }
    }

    /**
     * gets student name from ID
     * @param studentID
     * @return student_name for row in students table in database with column student_id = studentID
     * @throws SQLException
     */
    public String getStudentName(String studentID) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT student_name FROM students WHERE student_id = '" + studentID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.getString(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return ("");
        }

        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    /**
     * gets a list of assignments associated with the class
     * @param classID
     * @return an ArrayList containing all of the assignmentbundle_ids in table 'AssignmentBundles'
     * in rows with classroom_id = classroomID.
     * @throws Exception
     */
    public ArrayList<String> getAssignmentsInClass(String classID) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT assignmentbundle_id FROM 'AssignmentBundles' WHERE classroom_id = '" + classID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<String> assignments = new ArrayList<String>();

            while (rs.next()) {
                assignments.add(rs.getString(1));
            }

            return assignments;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null){
                rs.close();
            }
        }
    }

    /**
     * creates and returns a string representation of the students grade, in the form
     * student's grade / possible grade
     * @param studentID
     * @param assignmentID
     * @return A string containing the value for the 'total' column in the row with studentID in the
     * 'student_id' column in the table with title assignmentID, "/", and the value for the 'total' column
     * in the row with 0 in the 'student_id' column in the table.
     * @throws Exception
     */
    public String getTotalMark(String studentID, String assignmentID) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        String sql = "SELECT (total) FROM '" + assignmentID + "' WHERE student_id = '" + studentID + "'";
        String grade = "0.0";
        String possibleGrade = "0.0";
        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                /*stores student's total mark in grade*/
                grade = rs.getString(1);
                String sql2 = "SELECT (total) FROM '" + assignmentID + "' WHERE student_id = '" + 0 + "'";

                /* stores total possible grade in possibleGrade*/
                rs2 = stmt.executeQuery(sql2);
                possibleGrade = rs2.getString(1);
            }

            return grade + "/" + possibleGrade;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return grade + "/" + possibleGrade;
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }

            if (rs2 != null) {
                rs2.close();
            }
        }
    }

    /**
     * Finds out whether the table for a specific assignment exists in the database.
     * @param assignmentID
     * @return true if a table with the title assignmentID exists in database, false if not.
     * @throws Exception
     */
    public boolean tableExists(String assignmentID) throws SQLException {
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = this.connection.getMetaData();
            rs = dbm.getTables(null, null, assignmentID, null);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (rs != null){
                rs.close();
            }
        }
    }

    /**
     * finds out whether the grades for a specific student are in the page for an assignment.
     * @param studentID
     * @param assignmentID
     * @return true if there exists a row with colum student_id = studentID in the table with name
     *  assignmentID, false if there doesn't exist one or if the assignmentID table doesn't exist.
     * @throws Exception
     */
    public boolean studentDidAssignment(String studentID, String assignmentID) throws SQLException {
        if (!tableExists(assignmentID)) {
            return false;
        }
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM '" + assignmentID + "' WHERE student_id = '" + studentID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }
}