package System;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Spliterator;
import java.util.concurrent.CyclicBarrier;

public class TCPClientConnection extends Thread {

    private Socket socket;
    private TCPClient client;
    private DataInputStream reader;
    private DataOutputStream writer;
    private boolean active = true;

    public TCPClientConnection(Socket socket, TCPClient client) {
        this.socket = socket;
        this.client = client;
    }

    public void toServer(String text) {
        try {
            writer.writeUTF(text);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Client to server data transfer error: " + e.getMessage());
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException ioe) {
            System.out.println("Socket and I/O closing error: " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Set up readers and writers for convenience
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            while (active) {
                try {
                    //sleep while no incoming data
                    while (reader.available() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ie) {
                            System.out.println("Client suspend error: " + ie.getMessage());
                        }
                    }
                    //print the reply
                    String msg = reader.readUTF();
                    System.out.println(msg);
                } catch (IOException ioe) {
                    System.out.println("Client read error: " + ioe.getMessage());
                    closeConnection();
                }
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
            closeConnection();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
