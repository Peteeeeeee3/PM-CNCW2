package System;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Message {
    private String id;
    private long time;
    private String sender, topic, subject;
    private int contents;
    private ArrayList<String> headers = new ArrayList<>();
    private ArrayList<String> messageContent = new ArrayList<>();

    public Message(String id, long time, String sender, String topic, String subject, int contents, String text) {
        headers.add("Message-id: SHA-256 " + id);
        headers.add("Time-sent: " + time);
        headers.add("From: " + sender);
        headers.add("Topic: " + topic);
        headers.add("Subject: " + subject);
        headers.add("Contents: " + contents);
        //store actual message
        messageContent.add(text);
    }

    public Message(String sender, String topic, String subject, String text) {
        //calculate time
        time = currentTime();
        //store values
        this.sender = sender;
        this.topic = topic;
        this.subject = subject;
        //store actual message
        messageContent.add(text);
        //get contents value
        String[] lines = text.split("\r\n|\r|\n");
        contents = lines.length;
        //calculate id
        ArrayList<String> temp = new ArrayList<>();
        temp.add(Long.toString(time));
        temp.add(sender);
        temp.add(topic);
        temp.add(subject);
        temp.add(Integer.toString(contents));
        id = hashID(temp);
        //add headers
        headers.add("Message-id: SHA-256 " + id);
        headers.add("Time-sent: " + time);
        headers.add("From: " + sender);
        headers.add("Topic: " + topic);
        headers.add("Subject: " + subject);
        headers.add("Contents: " + contents);
    }

    private void readMessage(ArrayList<String> msg) {
        for (int i = 0; i < msg.size(); i++) {

        }
    }

    public static String hashID(ArrayList<String> headers) {
        StringBuilder toHash = new StringBuilder();
        String id = "";
        for (String header : headers) {
            toHash.append(header);
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(toHash.toString().getBytes(StandardCharsets.UTF_8));
            id = bytesToHex(encodedhash);
            if (id.equals("")){
                throw new Exception();
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Message ID hashing error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("No Message ID available error: " + e.getMessage());
        }
        return id;
    }

    //https://www.baeldung.com/sha-256-hashing-java
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static long currentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }
    public ArrayList<String> getMessageContent() {
        return messageContent;
    }
}
