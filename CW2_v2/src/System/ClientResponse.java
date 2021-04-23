package System;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientResponse extends Thread {
    private TCPClient client;
    private DataInputStream reader;

    public ClientResponse(TCPClient client) {
        this.client = client;
        try {
            reader = new DataInputStream(client.getSocket().getInputStream());
        } catch (IOException e) {
            System.out.println("ClientResponse creation error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        boolean active = true;
        while (active) {
            String input;
            try {
                //sleep while no incoming data sleep
                while (reader.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ie) {
                        System.out.println("Client response suspend error: " + ie.getMessage());
                    }
                }
                //print the reply
                input = reader.readUTF();
                System.out.println(input);
                //handle requests
                if (input.contains("PROTOCOL?")) {
                    client.send("PROTOCOL? " + client.getPm_v() + " Right");
                } else if (input.contains("TIME?")) {
                    client.send("NOW " + System.currentTimeMillis() / 1000);
                } else if (input.contains("LIST?")) {

                } else if (input.contains("GET?")) {

                } else if (input.contains("BYE!")) {

                }

            } catch (IOException ioe) {
                System.out.println("Client response read error: " + ioe.getMessage());
            }
        }
    }
}
