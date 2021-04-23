package System;

import GUI.ConnectionScreen;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TCPClient {
    private ClientResponse response;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private static int pm_v = 1;
    private DataOutputStream writer;
    private Socket socket;
    private String ip;
    private int port;

    /**
     * Initialise a new client. To run the client, call run().
     */
    public TCPClient() {}

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
            response = new ClientResponse(this);
            //response.start();
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

    public void send(String message) {
        try {
            writer.writeUTF(message);
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

    public void updateMessages(String topic, String subject) {
//        try {
//            response.wait();
//        } catch (InterruptedException e) {
//            System.out.println("Client response thread pause error: " + e.getMessage());
//            return;
//        }
        send("PROTOCOL? " + pm_v + " Left");
        String current = readMessage();
        //check for protocol? reply
        if (current.contains("PROTOCOL?")) {
            System.out.println("here");
            if (Integer.parseInt(current.substring(10, 11)) >= pm_v && current.length() == 17) {
                System.out.println("here2");
                send("TIME?");
                current = readMessage();
                //check for time? reply
                if (current.contains("NOW")) {
                    long time = Long.parseLong(current.substring(4));
                    if (!subject.equals("") && !topic.equals("")) {
                        send("LIST? " + (time - 10000) + " 2" + "\nTopic: " + topic + "\nSubject: " + subject);
                    } else if (!subject.equals("") && topic.equals("")) {
                        send("LIST? " + (time - 10000) + " 1" + "\nSubject: " + topic);
                    } else if (!topic.equals("") && subject.equals("")) {
                        send("LIST? " + (time - 10000) + " 1" + "\nTopic: " + topic);
                    } else if (subject.equals("") && topic.equals("")){
                        send("LIST? " + (time - 10000) + " 0");
                    }
                    System.out.println("here3");
                    current = readMessage();
                    //check Number of messages
                    if (current.contains("MESSAGES")) {
                        if (current.substring(9, 10).equals("0")) {
                            JOptionPane.showMessageDialog(null, "No matching messages found.");
                            return;
                        }
                        System.out.println("here4");
                        int start = 11;
                        //store message ids
                        String[] ids = current.substring(start).split("\r\n|\r|\n");
                        //get all messages
                        for (String id : ids) {
                            System.out.println("GET? SHA-256 " + id);
                            send("GET? SHA-256 " + id);
                            current = readMessage();
                            //handle received message
                            if (current.substring(0, 5).equals("FOUND")) {
                                convertMessage(current.substring(5));
                                System.out.println(current.substring(5));
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: No or incorrect reply to list request.");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: No or incorrect reply to time request.");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "The person you are connecting to, may have an incompatible version of Polite Messaging");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "An error occurred while trying to update messages.\nPlease try again.");
            return;
        }
//        response.notify();
    }

    private void convertMessage(String text) {
        ArrayList<String> lines = new ArrayList<>();
        int start = 0;
        //split lines
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.substring(i, i + 2).equals("\n")) {
                lines.add(text.substring(start, i));
                start = i + 2;
            }
        }
        //get header values
        String id = "", sender = "", topic = "", subject = "";
        long time = 0;
        int contents = 0;
        //get headers
        for (String line : lines) {
            if (line.substring(0, 4).equals("Mess")) {
                id = line;
            } else if (line.substring(0, 4).equals("Time")) {
                time = Integer.parseInt(line);
            } else if (line.substring(0, 4).equals("From")) {
                sender = line;
            } else if (line.substring(0, 4).equals("Topi")) {
                topic = line;
            } else if (line.substring(0, 4).equals("Subj")) {
                subject = line;
            } else if (line.substring(0, 4).equals("Cont")) {
                contents = Integer.parseInt(line);
            }
        }
        //create message object
        if (!(id.equals("") && time == 0 && sender.equals("") && topic.equals("") && subject.equals("") && contents == 0)) {
            messages.add(new Message(id, time, sender, topic, subject, contents, AL_String_ToString(lines)));
        }
    }

    private String AL_String_ToString(ArrayList<String> lines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    private String readMessage() {
        StringBuilder input = new StringBuilder();
        try {
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            boolean active = true;
            while (active) {
                try {
                    //sleep while no incoming data sleep
                    while (reader.available() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ie) {
                            System.out.println("Client suspend error: " + ie.getMessage());
                        }
                    }
                    //print the reply
                    input.append(reader.readUTF());
                    System.out.println(input);
                    active = false;
                } catch (IOException ioe) {
                    System.out.println("Client read error: " + ioe.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Message receiving error: " + e.getMessage());
        }
        return input.toString();
    }

    public String getIp() {return ip;}
    public int getPort() {return port;}
    public ArrayList<Message> getMessages() {return messages;}
    public int getPm_v() {return pm_v;}
    public Socket getSocket() {return socket;}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        JFrame frame = new JFrame();
        new ConnectionScreen(frame, client);
    }
}

