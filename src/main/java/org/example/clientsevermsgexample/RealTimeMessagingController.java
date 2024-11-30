package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RealTimeMessagingController {
    @FXML
    private TextArea messagesArea;
    @FXML
    private TextField messageInput;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    @FXML
    private void initialize() {
        new Thread(this::setupConnection).start();
    }

    private void setupConnection() {
        try {
            socket = new Socket("localhost", 6666);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String message = input.readUTF();
                Platform.runLater(() -> messagesArea.appendText("Server: " + message + "\n"));
            }
        } catch (IOException e) {
            Platform.runLater(() -> messagesArea.appendText("Connection error: " + e.getMessage() + "\n"));
        }
    }

    @FXML
    private void sendMessage() {
        try {
            String message = messageInput.getText();
            output.writeUTF(message);
            messagesArea.appendText("You: " + message + "\n");
            messageInput.clear();
        } catch (IOException e) {
            messagesArea.appendText("Error sending message: " + e.getMessage() + "\n");
        }
    }

    public void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            messagesArea.appendText("Error closing connection: " + e.getMessage() + "\n");
        }
    }
}
