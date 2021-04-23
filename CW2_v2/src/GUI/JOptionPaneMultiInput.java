package GUI;

import javax.swing.*;

//https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog/6555051
public class JOptionPaneMultiInput {
    public static void main(String[] args, ChatScreen chatScreen) {
        JTextField topicField = new JTextField(5);
        JTextField subjectField = new JTextField(5);
        JTextField timeField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Topic: #"));
        myPanel.add(topicField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Subject:"));
        myPanel.add(subjectField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Subject:"));
        myPanel.add(timeField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Update Messages", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            chatScreen.setSearch_topic(topicField.getText());
            System.out.println("Topic: #" + chatScreen.getSearch_topic());
            chatScreen.setSearch_subject(subjectField.getText());
            System.out.println("Subject: " + chatScreen.getSearch_subject());
        }
    }
}