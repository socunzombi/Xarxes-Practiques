package Server;
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.*;

public class Server {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private static int port = 1234;

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static DataInputStream  dataInputStream;
    private static DataOutputStream dataOutputStream;

    private static final String CHARACTERS_DB_NAME = "charactersDB.dat";
    private static CharactersDB charactersDB;

    public static void main (String[] args) {

        try {
            charactersDB = new CharactersDB (CHARACTERS_DB_NAME);
            serverSocket = new ServerSocket (port);
        } catch (IOException ex) {
            System.err.println ("Error opening database!");
            System.exit (-1);
        }

        System.out.println
            (ANSI_YELLOW +
            "\n-----| Servidor de la Base de Dades de l'associació de jugadors de rol de Sant Esteve de les Roures |-----\n"
            + ANSI_RESET);

        for (;;) {
            openConnection();
            getOption();
            closeConnection();
        }
    }

    private static void getOption () {
        try {
            int option = dataInputStream.readInt();
            System.out.println ("L'usuari ha escollit la opció: " + option);
            switch (option) {
                case 1 -> Option1();
                case 2 -> Option2();
                case 3 -> Option3();
                case 4 -> Option4();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Option1 () {

        System.out.println ("El servidor ha rebut la petició n. 1, processant...");

        int nchar = charactersDB.getNumCharacters();

        try {
            dataOutputStream.writeInt(nchar);
            for (int i = 0; i < nchar; i++) {
                CharacterInfo character = charactersDB.readCharacterInfo (i);
                dataOutputStream.writeUTF(character.getName());
            }
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println (ANSI_GREEN + "La Petició ha estat processada correctament." + ANSI_RESET);
    }

    private static void Option2 () {

        System.out.println ("El servidor ha rebut la petició n. 2, processant...");

        try {
            String name = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println (ANSI_GREEN + "La Petició ha estat processada correctament." + ANSI_RESET);
    }

    private static void Option3 () {
        System.out.println ("Option 3");
    }

    private static void Option4 () {
        System.out.println ("Option 4");
    }

    private static void openConnection () {
        System.out.println("Esperant connexió...");
        try {
            socket = serverSocket.accept();
            dataInputStream  = new DataInputStream  (socket.getInputStream());
            dataOutputStream = new DataOutputStream (socket.getOutputStream());
            System.out.println (ANSI_GREEN + "Connexió establerta correctament." + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection() {
        System.out.println ("Tancant connexió...");
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
            System.out.println (ANSI_GREEN + "Connexió tancada correctament." + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
