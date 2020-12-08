package MarkBreakdown;
import dbUtil.dbConnection;

import java.sql.*;
import java.util.ArrayList;


public class MarkBreakdownModel {
    /**
     * Responsible for direct interaction with database for Mark Breakdown pop-up window
     */
    Connection connection;

    public MarkBreakdownModel() {
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    public String getClassroomName(String classID) throws Exception {
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

    public String getAssignmentName(String assignmentID) throws Exception {
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

    public String getStudentName(String studentID) throws Exception {
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

    public ArrayList<String> getGradeBreakdown(String studentID, String assignmentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM '" + assignmentID + "' WHERE student_id = '" + studentID + "'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<String> grades = new ArrayList<String>();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 4; i <= columnCount; i++) {
                    grades.add(rs.getString(i));
                }
            }
            return grades;

        } catch (SQLException ex) {
            ex.printStackTrace();
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

    public ArrayList<String> getTotalPossibleMarks(String assignmentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM '" + assignmentID + "' WHERE student_id = '0'";

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<String> grades = new ArrayList<String>();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 4; i <= columnCount; i++) {
                    grades.add(rs.getString(i));
                }
            }
            return grades;

        } catch (SQLException ex) {
            ex.printStackTrace();
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
     * With given data input, update corresponding total and grade breakdown in database
     * @param qid
     * @param mark
     * @param NewTotal
     * @param stuid
     * @param bundleid
     * @throws SQLException
     */
    public void updateGradeData(int qid, float mark, float NewTotal, String stuid, String bundleid) throws SQLException{
        String query = "UPDATE '"+bundleid+"' SET question"+qid+" = '"+mark+"', total = '"+NewTotal+"' WHERE student_id = '"+stuid+"'";
        this.connection.prepareStatement(query).executeUpdate();
    }
}
