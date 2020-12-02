package Classroom;

import dbUtil.dbConnection;
import markmath.entities.AssignmentBundle;
import markmath.entities.AssignmentOutline;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;

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

    public void closeConnection(){
        try {
            this.connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
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


    public void createAssignmentTable(String received_id, AssignmentBundle assignment) throws Exception {
        Statement stmt = null;

        StringBuilder outline = new StringBuilder();
        int numOfQuestions = assignment.getOutline().getNumberOfQuestions();
        int i = 0;
        while (i != numOfQuestions) {
            outline.append("question").append(i+1).append(" REAL, ");
            i = i+1;
        }

        String outlineInDatabase = outline.toString();
        String sql = "CREATE TABLE IF NOT EXISTS "+assignment.getName()+"(student_id STRING, student_name STRING, " +
                "document_name STRING, "+ outlineInDatabase + "total STRING);";

        /*String sql = "CREATE TABLE IF NOT EXISTS" + assignment.getName() +" (\n"
                + "     bundle_id string PRIMARY KEY,\n"
                + " 	student_id string,\n"
                + " 	student_name string,\n"
                + " 	document_name string,\n"
                + " 	capacity real\n"
                + ");"; */
        try {
            stmt = this.connection.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (stmt != null ){
                stmt.close();

            }
        }
        //Adds outline as student 0 to indicate the full marks

    }

    public void addOutlineToAssignmentTable(AssignmentOutline outline, String assignmentName){

        StringBuilder outlineInDatabase = new StringBuilder();
        int numOfQuestions = outline.getNumberOfQuestions();
        HashMap<String, Float> outlineMap = outline.getQuestionToMarks();
        int i = 0;
        while (i != numOfQuestions-1) {
            outlineInDatabase.append("question").append(i+1).append(", ");
            i = i+1;
        }
        outlineInDatabase.append("question").append(i+1);
        System.out.println("you tried adding the outline");
        System.out.println(outlineInDatabase);
        //System.out.println(String.join("", Collections.nCopies(numOfQuestions, "?")));

        String sqlInsert = "INSERT INTO " + assignmentName +" (student_id, student_name, document_name, " + outlineInDatabase.toString() +
                ", total) VALUES (?, ?, ?, " + String.join("", Collections.nCopies(numOfQuestions, "?, ")) + "?)";
        //System.out.println(sql);
        try {
            System.out.println("you are in the try loop");
            PreparedStatement stmt = connection.prepareStatement(sqlInsert);
            stmt.setString(1, "0");
            stmt.setString(2,"outline");
            stmt.setString(3, outline.getName());
            i = 0;
            while (i != numOfQuestions) {
                stmt.setFloat(4+i, outlineMap.get("question" +(i+1)));
                i = i+1;
            }
            stmt.setFloat(4+i, outline.returnFullMark());
            boolean success = stmt.execute();
            System.out.println(success);
            stmt.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        }

    }

