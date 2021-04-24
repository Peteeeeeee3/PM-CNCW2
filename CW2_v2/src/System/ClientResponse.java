package System;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ClientResponse extends Thread {
    private TCPClient client;
    private DataInputStream reader;
    private boolean stopRequested = false;

    public ClientResponse(TCPClient client) {
        this.client = client;
        try {
            reader = new DataInputStream(client.getSocket().getInputStream());
        } catch (IOException e) {
            System.out.println("ClientResponse creation error: " + e.getMessage());
        }
    }

    public synchronized void threadPause() {
        this.stopRequested = true;
    }

    public synchronized void continueThread() {
        this.stopRequested = false;
    }

    @Override
    public void run() {
        boolean active = true;
        while (active) {
            while (stopRequested) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                    System.out.println("Client response suspend error: " + ie.getMessage());
                }
            }
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
                    client.sendMessage("PROTOCOL? " + client.getPm_v() + " Right");
                } else if (input.contains("TIME?")) {
                    client.sendMessage("NOW " + System.currentTimeMillis() / 1000);
                } else if (input.contains("LIST?")) {
                    //split headers
                    String[] headers = input.split("\r\n|\r|\n");
                    //split list command pos 1 = LIST? pos 2 = time value pos 3 = number of headers
                    String[] listComponents = headers[0].split("\\s+");
                    long time = Long.parseLong(listComponents[1]);
                    ArrayList<String> ids = new ArrayList<>();
                    for (Message msg : client.getMessages()) {
                        if (msg.getTime() >= time) {
                            ids.add(msg.getId());
                        }
                    }
                    StringBuilder send = new StringBuilder("MESSAGES " + ids.size());
                    for (String id : ids) {
                        send.append("\n").append(id);
                    }
                    client.sendMessage(send.toString());
                } else if (input.contains("GET?")) {
                    //split by space, pos 3 is id
                    String[] lines = input.split("\\s+");
                    for (Message message : client.getMessages()) {
                        if (message.getId().equals(lines[2])) {
                            System.out.println(lines[2]);
                            client.sendMessage(message);
                        }
                    }
                } else if (input.contains("BYE!")) {
                    client.getSocket().close();
                }
            } catch (IOException ioe) {
                System.out.println("Client response read error: " + ioe.getMessage());
            }
        }
    }
}
