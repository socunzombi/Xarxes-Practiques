package Client;
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

import java.net.*;
import java.io.*;

import static java.lang.Thread.sleep;

public class Client {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private static String address = "localhost";
    private static int port = 1234;

    private static Socket socket;
    private static DataInputStream  dataInputStream;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) {

        System.out.println
            (ANSI_YELLOW +
            "\n-----| Benvingut a la Base de Dades de l'associació de jugadors de rol de Sant Esteve de les Roures |-----\n"
            + ANSI_RESET);

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
        System.out.println (ANSI_CYAN + "Menú d'opcions:");
        System.out.println ("1 - Llista tots els noms de personatge.");
        System.out.println ("2 - Obté la informació d'un personatge.");
        System.out.println ("3 - Afegeix un personatge.");
        System.out.println ("4 - Elimina un personatge.");
        System.out.println ("5 - Sortir.");
    }

    private static int getOption() {
        for (;;) {
            try {
                sleep(100); // temps per no colapsar en cas de que surti missatge d'error
                BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
                System.out.print ("Escull una opció: " + ANSI_RESET);
                String optionStr = in.readLine();
                System.out.println ();
                int option = Integer.parseInt (optionStr);
                if (0 < option && option <= 5) {
                    return option;
                }
            } catch (Exception ex) {
                System.err.println (ANSI_RED + "Error reading option." + ANSI_RESET);
            }
        }
    }

    private static void Option1 () {
        if (openConnection()) {
            try {
                dataOutputStream.writeInt(1);
                dataOutputStream.flush();

                int nchar = dataInputStream.readInt();
                System.out.println ("Hi han " + nchar + " personatges a la Base de Dades:");

                for (int i = 0; i < nchar; i++) {
                    String name = dataInputStream.readUTF();
                    System.out.println(i + 1 + ": " + name);
                }
                System.out.println ();
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeConnection();
        }
    }

    private static void Option2 () {
        if (openConnection()) {
            try {
                dataOutputStream.writeInt(2);

                BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
                System.out.print ("Escriu el nom del personatge: ");
                String name;
                name = in.readLine();
                dataOutputStream.writeUTF(name);

                dataOutputStream.flush();

                if (dataInputStream.readBoolean()) {
                    CharacterInfo character = CharacterInfo.fromBytes(dataInputStream.readAllBytes());
                    System.out.println (character);
                } else {
                    String errorMessage = dataInputStream.readUTF();
                    System.err.println (errorMessage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            closeConnection();
        }
    }

    private static void Option3 () {

    }

    private static void Option4 () {

    }

    private static void Option5 () {
        System.out.println (ANSI_RED + "-----| Esperem que tornis aviat! |-----\n" + ANSI_RESET);
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
        System.err.println ("ERROR");
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
