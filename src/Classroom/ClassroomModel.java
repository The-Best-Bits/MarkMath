package Classroom;

import dbUtil.dbConnection;
import markmath.entities.AssignmentBundle;
import markmath.entities.AssignmentOutline;
import markmath.entities.StudentAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ClassroomModel {
    /**
     * Following Clean Architecture, this is a gateway class that interacts with the database. It is specifically
     * responsible for sending and retrieving data to and from the database in accordance with the actions users can
     * perform on the Classroom.fxml page.
     */
    Connection connection;

    /**
     * Initializes model and connects it to database
     **/
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

    /**
     * gets the classroom name for the classroom with ID with classID from the database
     * @param classID
     * @return class_name from the table classrooms where column class_id is classID.
     * @throws Exception
     */
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

    /**
     * finds whether the student is stored in the database or not.
     * @param studentID
     * @return true if there is a row with student_id equal to studentID in the students table, false otherwise.
     * @throws Exception
     */
    public boolean studentIsInDatabase(String studentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = '" + studentID + "'";

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

    /**
     * Finds whether the student is in the class with the ID classID or not
     * @param studentID
     * @param classID
     * @return true if the student with id studentID is in the students table and classID is stored in the class_id
     * column for that student. Returns false otherwise.
     * @throws Exception
     */
    public boolean studentIsInClass(String studentID, String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = '" + studentID + "' AND CHARINDEX(':" + classID + ":', class_id) > 0";

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

    /**
     * If the student is in the database, this method modifies the value of class_id for the student in the database
     * by appending classID + ":"
     * @param studentID
     * @param classID
     * @throws Exception
     * Note: When this method is called, the student should already be in the database.
     * Note: The class_id data is stored in the form ":id1:id2:id3:" where each of id1, id2, and id3 are classroom IDs.
     */
    public void addStudentToClass(String studentID, String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (studentIsInDatabase(studentID)) {
                String sql1 = "SELECT class_id FROM students where student_id = '" + studentID + "'";
                stmt = this.connection.createStatement();
                rs = stmt.executeQuery(sql1);
                String originalClasses = rs.getString(1);
                String newClasses = originalClasses + classID + ":";

                String sql2 = "UPDATE students SET class_id = '" + newClasses + "' WHERE student_id = '" + studentID + "'";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
     * if the student with ID studentID is not stored in the students table of the database, the student is
     * added, with parameters student_name = studentName, student_id = studentID, and a classroom string ":" that
     * means that the student is currently not in any classes.
     * @param studentID
     * @param studentName
     * @throws Exception
     */
    public void addStudentToDatabase(String studentID, String studentName) throws Exception {
        Statement stmt = null;

        try {
            if (!this.studentIsInDatabase(studentID)) {
                String sql = "INSERT INTO students (student_id, student_name, class_id) VALUES ('" + studentID + "', '"
                        + studentName + "', ':')";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * removes the class ID associated with the class under the student in the student table in the database.
     * If the student is enrolled in no classes after this, the student is removed from the Database.
     * @param studentID
     * @param classID
     * @throws Exception
     */
    private void removeStudentFromClass(String studentID, String classID) throws Exception {
        if (!studentIsInClass(studentID, classID)) {
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;

        try {
            String sql1 = "SELECT class_id FROM students where student_id = '" + studentID + "'";
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql1);

            String originalClasses = rs.getString(1);
            String newClasses = originalClasses.replace(classID + ":", "");

            String sql2;

            if (newClasses.trim().equals(":")) {
                sql2 = "DELETE FROM students WHERE student_id = '" + studentID + "'";
            } else {
                sql2 = "UPDATE students SET class_id = '" + newClasses + "' WHERE student_id = '" + studentID + "'";
            }

            stmt.executeUpdate(sql2);

        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * removes the entry for the student with ID studentID in the table with title assignmentID.
     * @param studentID
     * @param assignmentID
     * @throws Exception
     */
    private void removeStudentFromAssignment(String studentID, String assignmentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getTables(null, null, assignmentID, null);
            if (rs.next()) {
                String sql = "DELETE FROM '" + assignmentID + "' WHERE student_id = '" + studentID + "'";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Returns a list of Assignments associated with a class.
     * @param classID
     * @return an ArrayList containing Strings representing assignment IDs from database table AssignmentBundles where
     * classroom_id = classID
     * @throws Exception
     */
    private ArrayList<String> getAssignmentsInClass(String classID) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<String> assignmentsInClass = new ArrayList<>();

        try {
            String sql = "SELECT assignmentbundle_id FROM AssignmentBundles WHERE classroom_id = '" + classID + "'";
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                assignmentsInClass.add(rs.getString(1));
            }

            return assignmentsInClass;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    /** removes the student from the classroom. The method removes the student from the class and from all
     * assignment bundle tables.
     * If the student is enrolled in no classes after this, the student is removed from the Database.
     * @param studentID
     * @param classID
     * @throws Exception
     */
    public void removeStudent(String studentID, String classID) throws Exception {
        /*
         */
        if (!studentIsInClass(studentID, classID)) {
            return;
        }

        try {
            this.removeStudentFromClass(studentID, classID);

            ArrayList<String> assignmentsInClass = this.getAssignmentsInClass(classID);

            for (String assignment: assignmentsInClass) {
                this.removeStudentFromAssignment(studentID, assignment);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a table in the database with the name received_id, that contains columns student_id, student_name,
     * document_name, as well as a column for each question that the teacher created in the outline, and a column for
     * the total grade.
     * @param received_id
     * @param assignment
     * @throws Exception
     */
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
        String sql = "CREATE TABLE IF NOT EXISTS " + "[" + received_id + "]" + " (student_id STRING, student_name STRING, " +
                "document_name STRING, "+ outlineInDatabase + "total STRING);";

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

    /**
     * Creates a row in the table assignment ID that is an outline for the assignment with student_id 0 and which
     * contains the maximum possible marks for each question and the total.
     * @param outline
     * @param assignmentID
     */
    public void addOutlineToAssignmentTable(AssignmentOutline outline, String assignmentID){

        StringBuilder outlineInDatabase = new StringBuilder();
        int numOfQuestions = outline.getNumberOfQuestions();
        HashMap<String, Float> outlineMap = outline.getQuestionToMarks();
        int i = 0;
        while (i != numOfQuestions-1) {
            outlineInDatabase.append("question").append(i+1).append(", ");
            i = i+1;
        }
        outlineInDatabase.append("question").append(i+1);

        String sqlInsert = "INSERT INTO " + "[" + assignmentID + "]"+" (student_id, student_name, document_name, " + outlineInDatabase.toString() +
                ", total) VALUES (?, ?, ?, " + String.join("", Collections.nCopies(numOfQuestions, "?, ")) + "?)";

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

    /**
     * Helper function to markStudentAssignment. Differs from assignmentBundleInClassroom as this method checks if
     * there exists an assignmentbundle with the given name in this classroom. Because assignmentbundle names are unique
     * in a classroom this is a valid approach to search if an assignmentBundle is in a classroom. When marking a Hypatia
     * document we do not have the assignment bundle ID, we only have the assignment bundle name.
     * @param assignmentType Name of the assignmentbundle
     * @param classroomID  ID of the classroom
     * @return True iff this classroom contains an assignmentbundle with this name
     */
    public boolean assignmentBundleNameInClassroom(String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String sqlQuery = "SELECT assignment_name FROM AssignmentBundles WHERE classroom_id =" + classroomID;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            while (rs.next()){
                if(rs.getString("assignment_name").equals(assignmentType)){
                    return true;
                }
            }
            return false;

        }catch(SQLException e){
            System.out.println("Assignment bundle name in classroom");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null) {
                rs.close();
            }
        }

        return false;
    }

    /**
     * Helper function to addAssignment. Checks if there exists an assignment with the same id in the database,
     * since ids must be ideal for any classroom, so that it can be added in the classroom specified by classroomID.
     * Return True if the assignment with assignmentID exists.
     * @param assignmentID The id of the potential assignment that needs to be checked.
     * @param classroomID The classroom id of the classroom where we went to put the assignment.
     * @return
     */
    public Boolean assignmentBundleIDInClassrooms(String assignmentID, String classroomID){
        try{
            
            ResultSet rs = connection.createStatement().executeQuery("SELECT assignmentbundle_id FROM AssignmentBundles WHERE classroom_id =" + classroomID);
            while (rs.next()){
                if(rs.getString("assignmentbundle_id").equals(assignmentID)){
                    connection.close();
                    return true;
                }
            }
            return false;

        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        return false;
    }

    /**
     * Helper method of addAssignmentBundleToClassroomDatabase in ClassroomController. It attempts to add the assignment
     * to the data stored in the assignmentBundles table and assign it to the correct classroom indicated by currClass.
     * Throws a SQL exception if the assignment cannot be added or there are other problems.
     * Precondition:A valid sql statement must be created.
     * @param sqlInsert The SQL statement needed to add an assignmentBundle to the table assignmentBundles with the
     *                  currClass id.
     * @param received_id The id of the assignment we want to add.
     * @param assignmentName The name of the assignment we want to add.
     * @param currClass The id of the class in which we want to add the assignment
     * @throws SQLException Error in the database
     */
    public void addAssignmentBundleToClassroomDatabase(String sqlInsert, String received_id, String assignmentName,
                                                       String currClass) throws SQLException{

        int received_id_int = Integer.parseInt(received_id);
        // ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM assignmentBundles");
        PreparedStatement stmt = connection.prepareStatement(sqlInsert);
        stmt.setInt(1, received_id_int);
        stmt.setString(2, assignmentName );
        stmt.setString(3, currClass);
        stmt.execute();

    }

    /**
     * @param assignmentType Name of the assignmentbundle
     * @param classroomID ID of the classroom this assignmentbundle is in
     * @return the assignmentoutline of the assignmentbundle with the give name
     */
    public AssignmentOutline getAssignmentOutline(String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String assignmentBundleID = getIDOfAssignmentBundle(assignmentType, classroomID);
            String sqlQuery = "SELECT * FROM '" + assignmentBundleID + "' ORDER BY ROWID ASC LIMIT 1";
            System.out.println(sqlQuery);
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            HashMap<String, Float> questionToMarks = new HashMap<>();
            System.out.println(numColumns);
            for (int i =1; i <= numColumns-4; i++){
                String question = "question" + i;
                questionToMarks.put(question, rs.getFloat(question));
            }
            AssignmentOutline outline = new AssignmentOutline(assignmentType, questionToMarks);
            return outline;
        }catch(SQLException e){
            System.out.println("Get Assignment Outline");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Gets the ID of the assignment bundle with name 'assignmentBundleName' in the classroom with ID 'classroomID'
     * @param assignmentBundleName name of the assignment bundle
     * @param classroomID ID of the classroom this assignment bundle is in
     * @return ID of the assignmentbundle with the given name in this classroom
     * Note: There is only ever one assignmentbundle with a given name in a classroom, so there can only be one ID
     * returned
     */
    public String getIDOfAssignmentBundle(String assignmentBundleName, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{

            String sql = "SELECT assignmentbundle_id FROM AssignmentBundles WHERE assignment_name = '"
                    + assignmentBundleName + "'"+ " AND classroom_id ='" + classroomID + "'";
            System.out.println(sql);
            rs = this.connection.createStatement().executeQuery(sql);
            System.out.println(rs.next());
            System.out.println(classroomID);
            String result = rs.getString("assignmentbundle_id");
            System.out.println(result);
            return result;

        }catch(SQLException e){
            System.out.println("Get Assignment bundle ID");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Gets the name of the student with the given studentID 'studentNum'
     * @param studentNum student ID
     * @return name of the student with the given student ID
     */
    public String getStudentNameFromDatabase(String studentNum) throws SQLException{

        ResultSet rs = null;

        try{
            String sqlQuery = "SELECT student_name FROM students WHERE student_id = " + studentNum;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            String studentName = rs.getString("student_name");
            return studentName;
        }
        catch(SQLException e){
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Adds a student's assignment to the database in the assignmentbundle table this student assignment belongs in.
     * @param sqlInsert SQL Query to be executed
     * @param assignment Student's Assignment
     * @param inDatabase boolean representing whether this student assignment already exists in the corresponding
     *                   assignment bundle table
     */
    public void addStudentAssignmentToDatabaseModel(String sqlInsert, StudentAssignment assignment, boolean inDatabase) throws SQLException{

        PreparedStatement stmt = null;

        try{
            stmt = this.connection.prepareStatement(sqlInsert);
            if(!inDatabase) {
                stmt.setString(1, assignment.getStudentID());
                stmt.setString(2, assignment.getStudentName());
                stmt.setString(3, assignment.getAssignmentName());
                int q = 1;
                int numQuestions = assignment.getQuestions().size();
                while (q <= numQuestions) {
                    stmt.setFloat(q + 3, assignment.getQuestion(q).getFinalMark());
                    q += 1;
                }
                stmt.setFloat(q + 3, assignment.getFinalMark());
            }
            stmt.execute();

        }catch(SQLException e){
            System.out.println("Add Student assignment to database");
            System.out.println("Error" + e);
        }

        finally {
            if(stmt != null){
                stmt.close();
            }
        }
    }

    /**
     * Checks if there already exists an assignment for a student with ID 'studentNum' in the database in the table
     * for the given assignment bundle 'assignmentType'
     * @param studentNum StudentID of a student
     * @param assignmentType Name of an assignmentbundle in this classroom
     * @return True iff there already exists an assignment in the given assignment bundle in this classroom for a
     * student with the given studentID
     */
    public boolean studentAssignmentInDatabase(String studentNum, String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String assignmentbundleID = getIDOfAssignmentBundle(assignmentType, classroomID);
            String sqlQuery = "SELECT student_name FROM '" + assignmentbundleID + "' WHERE student_id =" + studentNum;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            return rs.next();
        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        finally{
            if(rs != null){
                rs.close();
            }
        }
        return false;
    }
}




