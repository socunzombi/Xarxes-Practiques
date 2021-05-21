package Client;
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private static String address = "localhost";
    private static int port = 1234;

    private static Socket socket;
    private static DataInputStream  dataInputStream;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) {

        System.out.println ("-----| Benvingut a la Base de Dades de l'associació de jugadors de rol de Sant Esteve de les Roures |-----\n");

        for (;;) {

            printMenu();

            int option = getOption();

            switch (option) {
                case 1 -> Option1();
                case 2 -> Option2();
                case 3 -> Option3();
                case 4 -> Option4();
                case 5 -> Option5();
            }
        }
    }

    private static void printMenu() {
        System.out.println ("Menú d'opcions:");
        System.out.println ("1 - Llista tots els noms de personatge.");
        System.out.println ("2 - Obté la informació d'un personatge.");
        System.out.println ("3 - Afegeix un personatge.");
        System.out.println ("4 - Elimina un personatge.");
        System.out.println ("5 - Sortir.");
    }

    private static int getOption() {
        for (;;) {
            try {
                BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
                System.out.println ("Escull una opció: ");
                String optionStr = in.readLine();
                int option = Integer.parseInt (optionStr);
                if (0 < option && option <= 5) {
                    return option;
                }
            } catch (Exception ex) {
                System.err.println ("Error reading option.");
            }
        }
    }

    private static void Option1 () {
        if (openConnection()) {
            try {
                dataOutputStream.writeInt(1);
                dataOutputStream.flush();
                int nchar = dataInputStream.readInt();

                for (int i = 0; i < nchar; i++) {
                    String name = dataInputStream.readUTF();
                    System.out.println(i + 1 + ": " + name);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeConnection();
        }
    }

    private static void Option2 () {

    }

    private static void Option3 () {

    }

    private static void Option4 () {

    }

    private static void Option5 () {
        System.out.println ("-----| Esperem que tornis aviat! |-----\n");
        System.exit(0);
    }

    private static boolean openConnection () { // TODO : posar "temporitzador"
        try {
            socket = new Socket (address, port);
            dataInputStream  = new DataInputStream  (socket.getInputStream());
            dataOutputStream = new DataOutputStream (socket.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void closeConnection() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
