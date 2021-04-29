
// @author Joan Francesc Pedro Garcia
// Practica 1 de Xarxes

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChattingClient {

    private static String address = "localhost";
    private static int port = 1234;
    private static Socket socket;
    private static final AtomicBoolean endConnection = new AtomicBoolean();

    public static void main(String[] args) {

        try {

            endConnection.set(false);
            socket = new Socket (address, port);

            Thread threadInput  = new Thread (new Input(), "Server");
            Thread threadOutput = new Thread (new Output());

            threadInput.start();
            threadOutput.start();

            do{}while (!endConnection.get());

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Input implements Runnable {

        DataInputStream dataInputStream;

        public Input () {
            endConnection.set(false);
        }

        @Override
        public void run() {
            try {

                String name = Thread.currentThread().getName();

                dataInputStream = new DataInputStream  (socket.getInputStream());

                String string = "";

                do {

                    string = dataInputStream.readUTF();
                    System.out.println(name + ": " + string);

                } while (!string.equals("FI\n") && !endConnection.get());

                endConnection.set(true);
                dataInputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Output implements Runnable {

        DataOutputStream dataOutputStream;

        public Output () {
            endConnection.set(false);
        }

        @Override
        public void run() {
            try {

                dataOutputStream = new DataOutputStream (socket.getOutputStream());

                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (System.in));

                String string = "";

                do {

                    string = bufferedReader.readLine();
                    dataOutputStream.writeUTF(string);
                    dataOutputStream.flush();

                } while (!string.equals("FI") && !endConnection.get());

                endConnection.set(true);
                dataOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
