package Server;
import java.util.ArrayList;

/**
 * Following the Strategy design pattern, if we would like to implement another algorithm for parsing
 * the document name (because a teacher wants to name the document in a different format), we can just
 * create a new class for that algorithm and implement this interface
 */

public interface DocumentNameParsingAlgorithm {

    ArrayList<String> getDocNameInfo(String docname)throws InvalidDocumentNameException;
}
