package utils;

import controllers.ServerController;
import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TaskClientConnection implements Runnable {

    private Socket socket;
    private ServerController server;
    private ObjectOutputStream output;

    public TaskClientConnection(Socket socket, ServerController server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // Create data input and output streams
            ObjectInputStream input = new ObjectInputStream(
                    socket.getInputStream());
            output = new ObjectOutputStream(
                    socket.getOutputStream());

            while (true) {
                // Get message from the client
                String message = null;
                try {
                    message = (String) input.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //send message via server broadcast
                server.broadcast(message);

                //append message of the Text Area of UI (GUI Thread)
                String finalMessage = message;
                Platform.runLater(() -> server.lv_conversation.getItems().add(finalMessage + "\n"));
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

    //send message back to client
    public void sendMessage(String message) {
        try {
            output.writeObject(message);
            output.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
