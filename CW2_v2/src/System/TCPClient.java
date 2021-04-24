package System;

import GUI.ConnectionScreen;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TCPClient extends Thread {
    private ClientResponse response;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private static int pm_v = 1;
    private DataOutputStream writer;
    private Socket socket;
    private String ip;
    private int port;
    private DBConnection dbc;

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
        this.dbc = new DBConnection();
        this.ip = ip;
        this.port = port;
        try {
            System.out.println("HTTPClient connecting to " + ip + ":" + port);
            socket = new Socket(ip, port);
            response = new ClientResponse(this);
            response.start();
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

    public void sendMessage(Message message) {
        try {
            StringBuilder text = new StringBuilder();
            text.append("FOUND\n");
            for (String header : message.getHeaders()) {
                text.append(header).append("\n");
            }
            for (String line : message.getMessageContent()) {
                text.append(line).append("\n");
            }
            //String toSend = "FOUND\n" + text.toString();
            writer.writeUTF(text.toString());
            System.out.println(text.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Client send error: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            writer.writeUTF(message);
            //System.out.println(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Client send error: " + e.getMessage());
        }
    }

    public void updateMessages(String topic, String subject, long earliestDate) {
        response.threadPause();
        sendMessage("PROTOCOL? " + pm_v + " Left");
        String current = readMessage();
        //check for protocol? reply
        if (current.contains("PROTOCOL?")) {
            System.out.println("here");
            if (Integer.parseInt(current.substring(10, 11)) >= pm_v && current.length() == 17) {
                System.out.println("here2");
                sendMessage("TIME?");
                current = readMessage();
                //check for time? reply
                if (current.contains("NOW")) {
                    long time = Long.parseLong(current.substring(4));
                    if (!subject.equals("") && !topic.equals("")) {
                        sendMessage("LIST? " + earliestDate + " 2" + "\nTopic: " + topic + "\nSubject: " + subject);
                    } else if (!subject.equals("") && topic.equals("")) {
                        sendMessage("LIST? " + earliestDate + " 1" + "\nSubject: " + topic);
                    } else if (!topic.equals("") && subject.equals("")) {
                        sendMessage("LIST? " + earliestDate + " 1" + "\nTopic: " + topic);
                    } else if (subject.equals("") && topic.equals("")){
                        sendMessage("LIST? " + earliestDate + " 0");
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
                            sendMessage("GET? SHA-256 " + id);
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
        response.continueThread();
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
                break;
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
                        //System.out.println(input);
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

    public ResultSet readFromDB(PreparedStatement sql) {
        return dbc.read(sql);
    }

    public void restoreMessages() {
        String sql;
        sql = "SELECT * FROM message";
        try {
            PreparedStatement prepStat = dbc.getConnection().prepareStatement(sql);
            ResultSet rs = dbc.read(prepStat);
            while (rs.next()) {
                boolean isContained = false;
                for (Message message : messages) {
                    if (rs.getString(1).equals(message.getId())) {
                        isContained = true;
                    }
                }
                if (!isContained) {
                    messages.add(new Message(rs.getString(1), rs.getLong(2), rs.getString(3), rs.getString(4),
                            rs.getString(5), rs.getInt(6), rs.getString(7)));
                }
            }
        } catch (SQLException sqlex) {
            System.out.println("Restore error: " + sqlex.getMessage());
        }
    }

    public void backupMessages() {
        //check whether entry already exists
        String sql;
        sql = "SELECT * FROM message";
        ArrayList<Message> toBackup = new ArrayList<Message>();
        try {
            PreparedStatement prepStat = dbc.getConnection().prepareStatement(sql);
            ResultSet rs = dbc.read(prepStat);
            while (rs.next()) {
                for (Message message : messages) {
                    boolean isContained = false;
                    if (message.getId().equals(rs.getString(1))) {
                        isContained = true;
                    }
                    if (!isContained) {
                        sql = "INSERT INTO message(ID, time_sent, sender, topic, subject, contents, text) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        prepStat = dbc.getConnection().prepareStatement(sql);
                        prepStat.setString(1, message.getId());
                        prepStat.setLong(2, message.getTime());
                        prepStat.setString(3, message.getSender());
                        prepStat.setString(4, message.getTopic());
                        prepStat.setString(5, message.getSubject());
                        prepStat.setInt(6, message.getContents());
                        StringBuilder text = new StringBuilder();
                        for (String line : message.getMessageContent()) {
                            text.append(line).append("\n");
                        }
                        prepStat.setString(7, text.toString());
                        dbc.write(prepStat);
                    }
                }
            }
        } catch (SQLException sqlex) {
            System.out.println("SQL error while backing up: " + sqlex.getMessage());
        }
    }

    public void writeToDB(PreparedStatement sql) {
        dbc.write(sql);
    }

    public String getIp() {return ip;}
    public int getPort() {return port;}
    public ArrayList<Message> getMessages() {return messages;}
    public int getPm_v() {return pm_v;}
    public Socket getSocket() {return socket;}
    public DBConnection getDbc() {return dbc;}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        JFrame frame = new JFrame();
        new ConnectionScreen(frame, client);
    }
}

