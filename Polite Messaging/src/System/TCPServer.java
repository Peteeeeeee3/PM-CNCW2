package System;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer implements Runnable {

    // Port numbers will be in lecture 5
    private int port = 20111;
    private ArrayList<TCPSConnection> connections = new ArrayList<TCPSConnection>();
    private boolean active = true;

    /**
     * Initialise a new server. To run the server, call run().
     */
    public TCPServer() {}

    /**
     * Runs the server.
     */
    public void run() {
        try {
            /*** Set up to accept incoming TCP connections ***/

            // Open the server socket
            System.out.println("Opening the server socket on port " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            newConnection(serverSocket);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

//    public void runLocal() {
//        // Waits until a host connects
//        System.out.println("Server waiting for host...");
//
//        TCPClient host = new TCPClient();
////        try {
//        System.out.println("here");
//        //host.run("localhost", 20111);
//        System.out.println("Host connected!");
//        run();
//
////        } catch (IOException ioe) {
////            System.out.println("Connection to localhost error: " + ioe.getMessage());
////            ioe.printStackTrace();
////        }
//    }

    private void newConnection(ServerSocket serverSocket) {
        try {
            /*** Receive client connection ***/
            //loop so new clients can always be added
            while (active) {
                // Waits until a client connects
                System.out.println("Server waiting for client...");
                //accept the server socket
                Socket socket = serverSocket.accept();
                //create new connection
                TCPSConnection conn = new TCPSConnection(socket, this);
                //start connection thread
                conn.start();
                //add connection to the array list
                connections.add(conn);
                System.out.println("Client connected!");
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        }
    }

    public ArrayList<TCPSConnection> getConnections() {
        return connections;
    }

//    /**
//     * @param args the command line arguments
//     */
    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.run();
    }
}

