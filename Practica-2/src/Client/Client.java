
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

import java.net.*;
import java.io.*;

public class Client {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

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
                case 1:
                    Option1();
                    break;
                case 2:
                    Option2();
                    break;
                case 3:
                    Option3();
                    break;
                case 4:
                    Option4();
                    break;
                case 5:
                    Option5();
                    break;
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
                //sleep(200); // temps per no colapsar en cas de que surti missatge d'error
                BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
                System.out.print ("Escull una opció: ");
                String optionStr = in.readLine();
                System.out.println ();
                int option = Integer.parseInt (optionStr);
                if (0 < option && option <= 5) {
                    return option;
                }
            } catch (Exception e) {
                System.out.println (ANSI_RED + "Error reading option." + ANSI_RESET);
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
                closeConnection();
                System.out.println ();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void Option2 () {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriu el nom del personatge: ");
            String name;
            name = in.readLine();
            System.out.println();

            if (openConnection()) {
                dataOutputStream.writeInt(2);
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                if (dataInputStream.readBoolean()) {
                    CharacterInfo character = CharacterInfo.fromBytes(dataInputStream.readNBytes(CharacterInfo.SIZE));
                    System.out.println(character);
                } else {
                    System.out.println(ANSI_RED + "Personatge no trobat\n" + ANSI_RESET);
                }
                closeConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void Option3 () {

        BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
        CharacterInfo character;

        try {

            String name;
            int intelligence = -1;
            int strength = -1;
            int constitution = -1;

            System.out.print ("Escriu el nom del personatge a afegir: ");
            name = in.readLine();
            while (name == null || name.isEmpty()) {
                System.out.println (ANSI_RED + "El nom del personatge no pot ser buit." + ANSI_RESET);
                System.out.print ("Escriu el nom del personatge a afegir: ");
                name = in.readLine();
            }

            while (intelligence < 0) {
                System.out.print ("Introdueix la intel·ligència (Enter major a 0): ");
                String intelligenceStr = in.readLine();
                if (intelligenceStr != null) {
                    try {
                        intelligence = Integer.parseInt (intelligenceStr);
                    } catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
            }

            while (strength < 0) {
                System.out.print ("Introdueix la força (Enter major a 0): ");
                String strengthStr = in.readLine();
                if (strengthStr != null) {
                    try {
                        strength = Integer.parseInt (strengthStr);
                    } catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
            }

            while (constitution < 0) {
                System.out.print ("Introdueix la constitució (Enter major a 0): ");
                String constitutionStr = in.readLine();
                if (constitutionStr != null) {
                    try {
                        constitution = Integer.parseInt (constitutionStr);
                    } catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
            }

            character = new CharacterInfo (name, intelligence, strength, constitution);

        } catch (Exception e) {
            System.out.println (ANSI_RED + "Error al llegir la informació del personatge, abortant..." + ANSI_RESET);
            return;
        }

        try {
            if (openConnection()){
                System.out.println ("Afegint l'usuari a la Base de Dades...");

                dataOutputStream.writeInt(3);
                dataOutputStream.write(character.toBytes());
                dataOutputStream.flush();

                if (dataInputStream.readBoolean()) {
                    System.out.println(ANSI_GREEN + "Personatge afegit correctament" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Error, aquest personatge ja està a la Base de Dades." + ANSI_RESET);
                }
                closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
