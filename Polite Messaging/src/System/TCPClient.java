package System;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {

    private TCPClientConnection clconn;
    private String host;
    private int port;

    /**
     * Initialise a new client. To run the client, call run().
     */
    public TCPClient() {}
    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Runs the client.
     * @throws IOException
     */
    public void run() {
        try {
            /*** Connect to the TCP server ***/
            System.out.println("TCPClient connecting to " + host + ":" + port);
            Socket socket = new Socket(host, port);
            clconn = new TCPClientConnection(socket, this);
            clconn.start();

            /*** Output the result ***/
            Scanner scanner = new Scanner(System.in);
            String input;
            while (true) {
                input = scanner.nextLine();
                clconn.toServer(input);
            }
        } catch (IOException e) {
            System.out.println("Client run error: " + e.getMessage());
        }

        // Close down the connection
        //clientSocket.close();
    }

    public void connect(String host, int port) {
        TCPClient client = new TCPClient();
        client.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        TCPClient client = new TCPClient("localhost", 20111);
        client.start();
    }

    public TCPClientConnection getClconn() {
        return clconn;
    }
}