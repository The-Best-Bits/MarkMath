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
    private com.corundumstudio.socketio.SocketIOServer server;
    private SocketIOClient client;
    private boolean receiveResultsEvents;

    /**
     * When a new SocketIOServer is instantiated it is configured to listen on port 3333
     */
    public SocketIOServer(){
        config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3333);
        server = new com.corundumstudio.socketio.SocketIOServer(config);
        receiveResultsEvents = false;
    }

    /**
     *
     */
    public void start(){

        // once Hypatia is connected to the server this method will be run
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Connected to Hypatia's CheckMath");
                client = socketIOClient;
            }
        });

        // listen for the "results" event emitted by Hypatia
        // we only need the information from this event, not the "expressions" event
        server.addEventListener("result", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                //add some code here so that we only pass on this results event if the mark button has been clicked
                if (receiveResultsEvents) {
                    System.out.println("Client Results" + s);
                    CheckMathParser temp = new CheckMathParser();
                    temp.parseResult(s);
                }
            }
        });

        server.start();

    }
    /**
     * Emits check_all_math event. Server will receive the all of the results events emitted by Hypatia from
     * the currently opened Hypatia document
     *
     */
    public void getResultsEvents(){

        if (client != null) {
            client.sendEvent("check_all_math");
            System.out.println("Event sent");
        }
    }

    /**
     * Set receiveResultsEvents to true. If a results event is received now it will be parsed
     */
    public void openReceiveResultsEvents(){

        this.receiveResultsEvents = true;
    }

    /**
     * Set receiveResultsEvents to false. If a results event is received it will now not be parsed
     */
    public void closeReceiveResultsEvents(){

        this.receiveResultsEvents = false;
    }



}
