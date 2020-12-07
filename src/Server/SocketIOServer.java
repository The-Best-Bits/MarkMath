package Server;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;



public class SocketIOServer {
    /** Following Clean Architecture, this is a controller class. This is the Socket IO Server that will connect to the
     * Hypatia app and receive the emitted events.
     * Attributes to note:
     * client: the client that has connected to this server on port 3333
     * parseResultsEvents: a boolean representing whether we will parse the 'result' events sent by Hypatia
     * parser: the parser class that receives and parses the 'result' events received by this server. In this case,
     * parsing refers to parsing for the information we need to use in our program.
     *
    */

    private Configuration config;
    final private com.corundumstudio.socketio.SocketIOServer server;
    private SocketIOClient client;
    private boolean parseResultsEvents;
    private CheckMathParser parser;

    /**
     * When a new SocketIOServer is instantiated it is configured to listen on port 3333.
     */
    public SocketIOServer(){
        config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3333);
        server = new com.corundumstudio.socketio.SocketIOServer(config);
        parseResultsEvents = false;
        parser = new CheckMathParser();

        // once Hypatia is connected to the server this method will be run
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Connected to Hypatia's CheckMath");
                client = socketIOClient;
            }
        });

        //starts the server
        server.start();
    }

    /**
     * Server will start listening for "result" events emitted by Hypatia when this method is called. These "result"
     * events will only be parsed if parseResultsEvents is true
     */
    public void start(){

        // listen for the "results" event emitted by Hypatia
        server.addEventListener("result", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                if (parseResultsEvents) {
                    System.out.println("Client Results" + s);
                    if (parser.getFinalParsedData().isEmpty()){
                        parser.parseFirstResultEvent(s);
                    }
                    else{
                        parser.addParsedData(s);
                    }
                }
            }
        });
    }

    /**
     * Set parseResultsEvents. If set to true, any results events that are received will be parsed. If set to false,
     * all results events that are received will not be parsed
     * @param parse boolean value
     */
    public void setParseResultsEvents(boolean parse){this.parseResultsEvents = parse;}

    /**
     * Emits check_all_math event. Server will receive all of the 'result' events emitted by Hypatia from
     * the currently opened Hypatia document
     */
    public void getAllResultsEvents(){

        if (client != null) {
            client.sendEvent("check_all_math");
            System.out.println("Event sent");
        }

    }

    /**
     * Returns this SocketIOServer's parser CheckMathParser
     * @return parser
     */
    public CheckMathParser getparser() {
        return this.parser;
    }

}
