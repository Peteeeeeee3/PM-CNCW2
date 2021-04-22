package System;

import GUI.ConnectionScreen;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TCPClient {
    private ArrayList<Message> messages = new ArrayList<Message>();
    private static int pm_v = 1;

    /**
     * Initialise a new client. To run the client, call run().
     */
    public TCPClient() {}
    private DataOutputStream writer;
    private Socket socket;
    private String ip;
    private int port;

    /**
     * Runs the client.
     * @throws IOException
     */
    public void run(String ip, int port) {
        messages.add(new Message("bc18ecb5316e029af586fdec9fd533f413b16652bafe079b23e021a6d8ed69aa", 1614686400, "martin.brain@city.ac.uk",
                "#announcements", "Hello!", 2, "Hello everyone!\n" +
                "This is the first message sent using PM.\n"));
        this.ip = ip;
        this.port = port;
        try {
            System.out.println("HTTPClient connecting to " + ip + ":" + port);
            socket = new Socket(ip, port);
            writer = new DataOutputStream(socket.getOutputStream());
            // Close down the connection
            //socket.close();
        } catch (IOException ioe) {
            System.out.println("Client connection error: " + ioe.getMessage());
        }

    }

    public void receive() {
        try {
            // Set up readers and writers for convenience
            DataInputStream reader = new DataInputStream(System.in);


            /*** Output the result ***/
            String msg;
            while (true) {
                msg = reader.readUTF();
                if (msg.length() == 0) {
                    break;
                }
                System.out.println(msg);
            }
        } catch (IOException ioe) {
            System.out.println("Receiving message error: " + ioe.getMessage());
        }
    }

    public void send(Message message) {
        try {
            StringBuilder text = new StringBuilder();
            for (String header : message.getHeaders()) {
                text.append(header).append("\n");
            }
            for (String line : message.getMessageContent()) {
                text.append(line).append("\n");
            }
            writer.writeUTF(text.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Client send error: " + e.getMessage());
        }
    }

    public void toServer(String text) {
        try {
            writer.writeUTF(text);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Client to server data transfer error: " + e.getMessage());
        }
    }

    public String getIp() {return ip;}
    public int getPort() {return port;}
    public ArrayList<Message> getMessages() {return messages;}
    public int getPm_v() {return pm_v;}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        JFrame frame = new JFrame();
        new ConnectionScreen(frame, client);
    }
}

