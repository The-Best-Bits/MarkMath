package Classroom;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassroomModel {
    Connection connection;

    public ClassroomModel(){
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
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean studentIsInDatabase(String studentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = " + studentID;

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean studentIsInClass(String studentID, String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = " + studentID + " AND CHARINDEX(':" + classID + ":', class_id) > 0";

        try {
            if (studentIsInDatabase(studentID)) {
                stmt = this.connection.createStatement();
                rs = stmt.executeQuery(sql);
                return rs.next();
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean addStudentToClass(String studentID, String studentName, String classID) throws Exception {
        /*Adds a student to the database, including the classID. If the student is already in the database and
        the class, the method returns false and does nothing else. Otherwise, the method returns True, adds the
        student to the database if they are not in it already, and adds the classID to the student in the
        database.
         */
        Statement stmt = null;
        ResultSet rs = null;

        try {
            if (this.studentIsInDatabase(studentID)) {
                if (this.studentIsInClass(studentID, classID)) {
                    return false;
                } else {
                    String sql1 = "SELECT class_id FROM students where student_id = " + studentID;
                    stmt = this.connection.createStatement();
                    rs = stmt.executeQuery(sql1);
                    String originalClasses = rs.getString(1);
                    String newClasses = originalClasses + classID + ":";

                    String sql2 = "UPDATE students SET class_id = '" + newClasses + "' WHERE student_id = " + studentID;
                    stmt = this.connection.createStatement();
                    stmt.executeUpdate(sql2);
                    return true;
                }
            } else {
                String sql = "INSERT INTO students (student_id, student_name, class_id) VALUES ('" + studentID + "', '" + studentName + "', ':" + classID + ":')";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
