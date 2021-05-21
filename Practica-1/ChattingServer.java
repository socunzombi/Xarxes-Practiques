
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 1 de Xarxes

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.*;

public class ChattingServer {

    ////  Variables inicialitzades:
    ////  connection: Variable per comprobar si ja hi ha alguna connexió.
    ////  finished: comprova si la connexió actual es tanca.

    private static boolean connection = false;
    private static final AtomicBoolean finished = new AtomicBoolean();

    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;


    ////  A la funció main iniciem el servidor, amb un serversocket i dos threads, iniciem la variable finished a false
    public static void main (String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket (1234);

            if (!connection) {
                connection = true;

                socket = serverSocket.accept();

                Thread threadInput  = new Thread (new Input  (), "Client");
                Thread threadOutput = new Thread (new Output ());

                threadInput.start();
                threadOutput.start();

                finished.set(false);

            } else {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////  A la classe input fem tots els processos amb el dataInputStream, que sin els de rebre els missatges del  Servidor.
    ////  En cas de rebre un FI inicia la funció finishProgram()
    private static class Input implements Runnable {

        public Input () {
        }

        @Override
        public void run() {
            try {
                String name = Thread.currentThread().getName();
                dataInputStream = new DataInputStream  (socket.getInputStream());
                String string = "";

                while (!finished.get()) {
                    string = dataInputStream.readUTF();
                    System.out.println(name + ": <<" + string + ">>");

                    if (string.equals("FI")) {
                        finishProgram();
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Connexió finalitzada");
                finishProgram();
            }
        }
    }


    ////  A la classe output fem tots els processos amb el dataOutputStream, abans d'enviar el que l'usuari ha escrit per teclat, comprovem que no sigui un string buida,
    ////  en cas d'enviar un FI inicia la funció finishProgram()
    private static class Output implements Runnable {

        public Output () {
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

                while (!finished.get()) {
                    if (bufferedReader.ready()) {
                        string = bufferedReader.readLine();

                        if (!string.equals("")) {
                            dataOutputStream.writeUTF(string);
                            dataOutputStream.flush();
                        }
                    }

                    if (string.equals("FI")) {
                        finishProgram();
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Connexió finalitzada");
                finishProgram();
            }
        }
    }

    ////  La funcio finishProgram tanca els data i el socket, seteja el finished a true i fa un exit
    private static void finishProgram () {
        try {

            dataOutputStream.close();
            dataInputStream.close();
            socket.close();

            finished.set(true);

            System.exit (0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
