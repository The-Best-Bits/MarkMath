package StudentMarks;

import dbUtil.dbConnection;

import java.sql.*;
import java.util.ArrayList;

public class StudentMarksModel {
    Connection connection;

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

    public String getClassroomName(String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT class_name FROM classrooms WHERE class_id = " + classID;

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

        String sql = "SELECT assignment_name FROM AssignmentBundles WHERE assignmentbundle_id = " + assignmentID;

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

        String sql = "SELECT student_name FROM students WHERE student_id = " + studentID;

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

    public ArrayList<String> getAssignmentsInClass(String classID) throws Exception {
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
        } catch (Exception e) {
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

    public boolean tableExists(String assignment_id) throws Exception {
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = this.connection.getMetaData();
            rs = dbm.getTables(null, null, assignment_id, null);
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (rs != null){
                rs.close();
            }
        }
    }
}