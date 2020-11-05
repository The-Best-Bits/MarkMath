package Server;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;



public class SocketIOServer {
    /** The SocketIOServer controller class
     * This is the Socket IO Server that will sqlite.Connect.java to the Hypatia app and receive
    the emitted events

    SocketIO is a library that allows for bi-directional communication between clients and
    servers. We are using the server-side library to implement this SocketIO server. SocketIO
    uses Websocket protocol. We must communicate over this protocol because the Hypatia app does.
    The SocketIO library will handle the Websocket Handshake for us upon connecting with Hypatia.

    */

    /**
     * Configures the server, starts the server, connects to Hypatia's CheckMath, and receives the results events
     * emtited by Hypatia
     * @param args
     */
    public static void main(String[] args) {
        // configure this server to listen as localhost on port 3333
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3333);
        com.corundumstudio.socketio.SocketIOServer server = new com.corundumstudio.socketio.SocketIOServer(config);


        // once Hypatia is connected to the server this method will be run
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Connected to Hypatia's CheckMath");
            }
        });

//        // listen for the "expressions" event emitted by Hypatia
//        server.addEventListener("expressions", String.class, new DataListener<String>() {
//            @Override
//            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
//                String expressionData = s;
//                final String[] resultsData = new String[1];
//                System.out.println("Client Expression" + s);
//            }
//        });

        // listen for the "results" event emitted by Hypatia
        // we only need the information from this event, not the "expressions" event
        server.addEventListener("result", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                System.out.println("Client Results" + s);
                CheckMathParser temp = new CheckMathParser();
                temp.parseResult(s);
            }
        });

        server.start();
    }



}
