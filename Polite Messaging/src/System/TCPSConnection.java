package System;

import java.io.*;
import java.net.Socket;

public class TCPSConnection extends Thread{

    private Socket socket;
    private TCPServer server;
    private DataInputStream reader;
    private DataOutputStream writer;
    private boolean active = true;

    public TCPSConnection(Socket socket, TCPServer server) {
        super("TCPSCThread");
        this.socket = socket;
        this.server = server;
    }

    public void broadcastToClients(String text) {
        for (int i = 0; i < server.getConnections().size(); i++) {
            TCPSConnection conn = server.getConnections().get(i);
            conn.toClient(text);
        }
    }

    public void toClient(String text) {
        try {
            writer.writeUTF(text);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Data output issue: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            while (active) {
                while (reader.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println("Thread sleep error: " + e.getMessage());
                    }
                }
                /*** Output what the client says ***/
                String msg = reader.readUTF();
                System.out.println("Client says " + msg);
                broadcastToClients(msg);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Threading error: " + e.getMessage());
        }
    }
}
