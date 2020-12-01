package Server;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;



public class SocketIOServer {
    /** The SocketIOServer controller class
     * This is the Socket IO Server that will connect to the Hypatia app and receive
    the emitted events

    SocketIO is a library that allows for bi-directional communication between clients and
    servers. We are using the server-side library to implement this SocketIO server. SocketIO
    uses Websocket protocol. We must communicate over this protocol because the Hypatia app does.
    The SocketIO library will handle the Websocket Handshake for us upon connecting with Hypatia.

    */

    private Configuration config;
    final private com.corundumstudio.socketio.SocketIOServer server;
    private SocketIOClient client;
    private boolean parseResultsEvents;

    /**
     * When a new SocketIOServer is instantiated it is configured to listen on port 3333.
     */
    public SocketIOServer(){
        config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3333);
        server = new com.corundumstudio.socketio.SocketIOServer(config);
        parseResultsEvents = false;

        // once Hypatia is connected to the server this method will be run
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Connected to Hypatia's CheckMath");
                client = socketIOClient;
            }
        });

        server.start();
    }

    /**
     * Server will start listening for results events emmitted by Hypatia. These results events will only be parsed
     * if parseResultsEvents is true
     */
    public void start(){

        // listen for the "results" event emitted by Hypatia
        // we only need the information from this event, not the "expressions" event
        server.addEventListener("result", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                if (parseResultsEvents) {
                    System.out.println("Client Results" + s);
                    CheckMathParser temp = new CheckMathParser();
                    temp.parseResult(s);
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
     * Emits check_all_math event. Server will receive all of the results events emitted by Hypatia from
     * the currently opened Hypatia document
     */
    public void getAllResultsEvents(){

        if (client != null) {
            //is check math not receiving the event right away?
            client.sendEvent("check_all_math");
            System.out.println("Event sent");
        }

    }

}
