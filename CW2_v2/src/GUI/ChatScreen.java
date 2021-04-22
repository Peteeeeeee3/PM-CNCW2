package GUI;

import javax.swing.*;
import System.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ChatScreen {
    private JPanel panel1;
    private JList messaging_window;
    private JButton sendButton;
    private JLabel connection_label;
    private JList topic_window;
    private JTextArea textArea1;
    private TCPClient client;
    private String email;
    private String search_subject;
    private String search_topic;

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
                    JOptionPaneMultiInput.main(null, this);
                    textArea1.setText("");
                } else {
                    //client.send(new Message(email, "", "", textArea1.getText()));
                    client.getMessages().add(new Message(email, "", "", textArea1.getText()));
                    //client.send(client.getMessages().get(0));
                    textArea1.setText("");
                }
            }
        });
    }

    public String getSearch_subject() {return search_subject;}
    public void setSearch_subject(String search_subject) {this.search_subject = search_subject;}
    public String getSearch_topic() {return search_topic;}
    public void setSearch_topic(String search_topic) {this.search_topic = search_topic;}
}
