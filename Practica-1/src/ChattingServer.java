
// @author Joan Francesc Pedro Garcia
// Practica 1 de Xarxes


import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.*;

public class ChattingServer {

    private static int port = 1234;
    private static boolean connection = false;
    private static final AtomicBoolean endConnection = new AtomicBoolean();

    public static void main (String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket (port);

            if (!connection) {
                connection = true;
                endConnection.set(false);

                Socket socket = serverSocket.accept();

                Thread threadInput  = new Thread (new Input  (socket), "Client");
                Thread threadOutput = new Thread (new Output (socket));

                threadInput.start();
                threadOutput.start();

                do {

                } while (!endConnection.get());

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Input implements Runnable {

        private final Socket socket;
        DataInputStream  dataInputStream;

        public Input (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {

                String name = Thread.currentThread().getName();

                dataInputStream = new DataInputStream  (socket.getInputStream());

                String string;

                do {

                    string = dataInputStream.readUTF();
                    System.out.println(name + ": " + string);

                } while (!string.equals("FI\n") && !endConnection.get());

                endConnection.set(true);
                dataInputStream.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Output implements Runnable {

        private final Socket socket;
        DataOutputStream dataOutputStream;

        public Output (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {

                dataOutputStream = new DataOutputStream (socket.getOutputStream());

                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (System.in));

                String string = "Connexió acceptada.";

                System.out.println(string);
                dataOutputStream.writeUTF(string);
                dataOutputStream.flush();

                do {

                    string = bufferedReader.readLine();
                    dataOutputStream.writeUTF(string);
                    dataOutputStream.flush();

                } while (!string.equals("FI") && !endConnection.get());

                endConnection.set(true);
                dataOutputStream.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
