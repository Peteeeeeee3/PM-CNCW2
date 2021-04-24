package GUI;

import javax.swing.*;
import System.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionScreen {
    private JPanel panel1;
    private JTextField ip_field;
    private JTextField port_field;
    private JButton connect_button;
    private JTextField email_field;
    private TCPClient client;

    public ConnectionScreen(JFrame frame, TCPClient client) {
        this.client = client;
        frame.setContentPane(new ConnectionScreen(frame, client, true).panel1);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 200);
        frame.setVisible(true);
    }

    public ConnectionScreen(JFrame frame, TCPClient client, boolean placeholder) {
        connect_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (email_field.getText().equals("") || ip_field.getText().equals("") || port_field.getText().equals("")) {
                    JOptionPane.showMessageDialog(connect_button, "Missing email address, IP address, or port number.\nPlease fill out missing field.");
                } else {
                    if (!(email_field.getText().contains(".") && email_field.getText().contains("@")) || !(ip_field.getText().contains(".") || ip_field.getText().equals("localhost"))) {
                        JOptionPane.showMessageDialog(connect_button, "Incorrect email address, IP address or port number. \nPlease check and correct entered data.");
                    } else {
                        client.getDbc().connectToDB();
                        client.run(ip_field.getText(), Integer.parseInt(port_field.getText()));
                        new ChatScreen(frame, email_field.getText(), client);
                    }
                }
            }
        });
    }
}
