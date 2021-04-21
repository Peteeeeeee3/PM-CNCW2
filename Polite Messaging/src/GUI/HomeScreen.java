package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

import System.*;

public class HomeScreen {
    private JPanel panel1;
    private JTextField ip_field;
    private JTextField port_field;
    private JButton connectButton;
    private JButton hostButton;
    private JLabel ipField;
    private JTextField usernameField;

    public HomeScreen(JFrame frame) {
        frame.setContentPane(new HomeScreen(frame, true).panel1);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }

    public HomeScreen(JFrame jFrame, boolean bool) {
        ipField.setText("Your IP address is: " + getIP());
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ip_field.getText().equals("") || port_field.getText().equals("")){
                    JOptionPane.showMessageDialog(connectButton, "Username or password is missing. Please enter!");
                } else {
                    if (!validateUsername(usernameField.getText())) {
                        JOptionPane.showMessageDialog(connectButton, "Please enter a valid username.\n Username must be at least 3 characters long.\n " +
                                "Username cannot contain: \" \", \"\"\", \"\'\", \"!\", \"?\", \"/\", \"#\", \"%\", \"^\", \"&\", \"*\", \"(\" or \")\".");
                    } else {
                        TCPClient client = new TCPClient(ip_field.getText(), Integer.parseInt(port_field.getText()));
                        client.start();
                        new ChatWindow(jFrame, client, ip_field.getText());
                    }
                }
            }
        });

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateUsername(usernameField.getText())) {
                    JOptionPane.showMessageDialog(connectButton, "Please enter a valid username.\n Username must be at least 3 characters long.\n " +
                            "Username cannot contain: \" \", \"\"\", \"\'\", \"!\", \"?\", \"/\", \"#\", \"%\", \"^\", \"&\", \"*\", \"(\", \")\".");
                } else {
                    TCPServer server = new TCPServer();
                    new Thread(server).start();
                    TCPClient client = new TCPClient("localhost", 20111);
                    client.start();
                    new ChatWindow(jFrame, client, getIP());
                }
            }
        });
    }

    public boolean validateUsername(String username) {
        if (username.contains(" ")) return false;
        if (username.contains("\"")) return false;
        if (username.contains("\'")) return false;
        if (username.contains("!")) return false;
        if (username.contains("?")) return false;
        if (username.contains("/")) return false;
        if (username.contains("#")) return false;
        if (username.contains("%")) return false;
        if (username.contains("^")) return false;
        if (username.contains("&")) return false;
        if (username.contains("*")) return false;
        if (username.contains("(")) return false;
        if (username.contains(")")) return false;
        if (username.length() < 3) return false;
        //add more code
        return true;
    }

    //https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
    public String getIP() {
        String ip = "N/A";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            ip = in.readLine(); //you get the IP as a String
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
