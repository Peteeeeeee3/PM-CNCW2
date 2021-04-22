package GUI;

import javax.swing.*;
import java.awt.event.ActionListener;

//https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog/6555051
public class JOptionPaneMultiInput {
    public static void main(String[] args, ActionListener chatScreen) {
        JTextField topicField = new JTextField(5);
        JTextField subjectField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Topic: #"));
        myPanel.add(topicField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Subject:"));
        myPanel.add(subjectField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("Topic: #" + topicField.getText());
            System.out.println("Subject: " + subjectField.getText());
        }
    }
}