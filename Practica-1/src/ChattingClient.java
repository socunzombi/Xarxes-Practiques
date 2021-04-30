
// @author Joan Francesc Pedro Garcia
// Practica 1 de Xarxes

import java.net.*;
import java.io.*;

public class ChattingClient {

    private static final String address = "localhost";
    private static final int port = 1234;
    private static Socket socket;

    private static boolean endConnection;
    private static boolean inputClosed;
    private static boolean outputClosed;

    public static void main(String[] args) {

        try {
            socket = new Socket (address, port);

            Thread threadInput  = new Thread (new Input(), "Server");
            Thread threadOutput = new Thread (new Output());
            endConnection = false;
            threadInput.start();
            threadOutput.start();

            do {} while (!endConnection || !inputClosed || !outputClosed);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Input implements Runnable {

        DataInputStream dataInputStream;

        public Input () {
            inputClosed = false;
        }

        @Override
        public void run() {
            try {

                String name = Thread.currentThread().getName();

                dataInputStream = new DataInputStream  (socket.getInputStream());

                String string = "";

                do {

                    if (!socket.isClosed()){
                        string = dataInputStream.readUTF();
                        System.out.println(name + ": " + string);
                    } else {
                        break;
                    }

                    if (string.equals("FI")) {
                        endConnection = true;
                    }
                } while (!endConnection);

                dataInputStream.close();
                inputClosed = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Output implements Runnable {

        DataOutputStream dataOutputStream;

        public Output () {
            outputClosed = false;
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

                    if (string.equals("FI")) {
                        endConnection = true;
                    }
                } while (!endConnection);

                dataOutputStream.close();
                outputClosed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
