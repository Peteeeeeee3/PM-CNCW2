package GUI;

import javax.swing.*;
import System.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class ChatScreen {
    private JPanel panel1;
    private JButton sendButton;
    private JLabel connection_label;
    private JTextArea textArea1;
    private JTextArea messageArea;
    private JLabel topic_subject_label;
    private TCPClient client;
    private String email;
    private String search_subject;
    private String search_topic;
    private String search_time;

    public ChatScreen(JFrame frame, String email, TCPClient client) {
        this.client = client;
        frame.setContentPane(new ChatScreen( email, client).panel1);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setVisible(true);
    }

    public ChatScreen(String email, TCPClient client) {
        this.client = client;
        this.email = email;
        textArea1.setColumns(50);
        textArea1.setRows(5);
        search_subject = "";
        search_topic = "";
        ChatScreen chatScreen = this;
        topic_subject_label.setText("Topic: *ANY*       Subject: *ANY*");

        //https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
        if (client.getIp().equals("localhost")) {
            String ipTemp = "N/A";
            try {
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));

                ipTemp = in.readLine(); //you get the IP as a String
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection_label.setText("You are connected to: " + ipTemp + " via port: " + client.getPort() + ".");
        } else {
            connection_label.setText("You are connected to: " + client.getIp() + " via port: " + client.getPort() + ".");
        }

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textArea1.getText().equals("_UPDATE")) {
                    JOptionPaneMultiInput.main(null, chatScreen);
                    textArea1.setText("");
                    client.updateMessages(search_topic, search_subject);
                    //update search and topic label
                    if (search_subject.equals("") && search_topic.equals("")) {
                        topic_subject_label.setText("Topic: *ANY*       Subject: *ANY*");
                    } else if (!search_subject.equals("") && !search_topic.equals("")) {
                        topic_subject_label.setText("Topic: " + search_topic + "    Subject: " + search_subject);
                    } else if (!search_subject.equals("")) {
                        topic_subject_label.setText("Topic: " + search_topic + "    Subject: *ANY*");
                    } else {
                        topic_subject_label.setText("Topic: *ANY*       Subject: " + search_subject);
                    }
                    showMessages();
                } else {
                    //client.send(new Message(email, "", "", textArea1.getText()));
                    client.getMessages().add(new Message(email, "", "", textArea1.getText()));
                    //client.send(client.getMessages().get(0));
                    textArea1.setText("");
                }
            }
        });
    }

    private void showMessages() {
        StringBuilder display_text = new StringBuilder();
        for (Message message : client.getMessages()) {
            display_text.append(message.getHeaders().get(2)).append("\n");
            for (String lines : message.getMessageContent()) {
                display_text.append(lines);
            }
            display_text.append("\n\n");
        }
        messageArea.setText(display_text.toString());
    }

    String getSearch_subject() {return search_subject;}
    void setSearch_subject(String search_subject) {this.search_subject = search_subject;}
    String getSearch_topic() {return search_topic;}
    void setSearch_topic(String search_topic) {this.search_topic = search_topic;}
    public String getSearch_time() {return search_time;}
    public void setSearch_time(String search_time) {this.search_time = search_time;}
}
