package utils;

import com.sun.imageio.plugins.common.I18N;
import controllers.ServerController;
import javafx.application.Platform;
import sun.net.ConnectionResetException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TaskClientConnection implements Runnable {

    Socket socket;
    ServerController server;
    // Create data input and output streams
    DataInputStream input;
    DataOutputStream output;

    public TaskClientConnection(Socket socket, ServerController server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // Create data input and output streams
            input = new DataInputStream(
                    socket.getInputStream());
            output = new DataOutputStream(
                    socket.getOutputStream());

            while (true) {
                // Get message from the client
                String message = input.readUTF();

                //send message via server broadcast
                server.broadcast(message);

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(() -> {
                    server.lv_conversation.getItems().add(message + "\n");
                });
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
            output.writeUTF(message);
            output.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
