package utils;

import controllers.ServerController;
import javafx.application.Platform;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TaskClientConnection implements Runnable {

    private Socket socket;
    private ServerController server;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public TaskClientConnection(Socket socket, ServerController server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // membuat object stream input dan output
            output = new ObjectOutputStream(
                    socket.getOutputStream());
            input = new ObjectInputStream(new BufferedInputStream(
                    socket.getInputStream()));

            try {
                server.addNewClient((String) input.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            while (true) {
                // menerima pesan dari klien
                String message = null;
                try {
                    message = (String) input.readObject();
                } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                }

                // mengirim pesan  broadcast melalui server
                server.broadcast(message);

                //append message of the Text Area of UI (GUI Thread)
                String finalMessage = message;
                Platform.runLater(() -> server.lv_conversation.getItems().add(finalMessage + "\n"));
            }


        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        }

    }

    //send message back to client
    public void sendMessage(String message) {
        try {
            output.writeObject(message);
            output.flush();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
