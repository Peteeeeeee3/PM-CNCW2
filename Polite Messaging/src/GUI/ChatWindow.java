package GUI;

import javax.swing.*;
import System.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow {
    private JPanel panel1;
    private JList messageW;
    private JTextField textField1;
    private JButton button1;
    private JLabel ipField;
    private String ip;
    private TCPClient client;

    public ChatWindow(JFrame frame, TCPClient client, String ip) {
        this.ip = ip;
        this.client = client;
        frame.setContentPane(new ChatWindow(ip, client).panel1);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setVisible(true);
    }

    public ChatWindow(String ip, TCPClient client) {
        this.ip = ip;
        this.client = client;
        ipField.setText("The server's IP address is: " + ip);
        //add sending message functionality to button
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText().equals("")) {
                    JOptionPane.showMessageDialog(button1, "You cannot send an empty message!");
                } else {
                    client.getClconn().toServer(textField1.getText());
                }
            }
        });
    }
}
