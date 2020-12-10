package markmath.entities;

public class Classroom {
    /** Following Clean Architecture this is an entity class that represents a Classroom
     *Attributes:
     *classname: name of this classroom
     *id: ID of this classroom
     */
    private final String classname;
    private final String id;

    public Classroom(String name, String id){
        this.classname = name;
        this.id = id;
    }

    public String getClassname(){ return this.classname;}

    public String getId(){return this.id;}

}
