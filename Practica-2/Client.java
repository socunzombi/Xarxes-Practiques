

// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

// ACLARAMENT: Pràctica funcional amb el codi que ens has passat pel campus, pujo
// únicament els arxius Client.java i Server.java els quals son on està tota la feina feta.



import java.net.*;
import java.io.*;

public class Client {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static String address = "localhost";
    private static int port = 1234;
    private static int timeout = 2000;

    private static Socket socket;
    private static DataInputStream  dataInputStream;
    private static DataOutputStream dataOutputStream;


    /**
     * 
     * A la funció main fem un bucle infinit on cada cop es s'executa la funció printMenu()
     * i la funció getOption(), sobre la opció escollida es fa un switch el cual executa la
     * funció desitjada.
     * 
     */
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




    /**
     * la funció printMenu imprimeix el menú per pantalla mostrant les opcions que hi han
     */
    private static void printMenu() {
        System.out.println ("Menú d'opcions:");
        System.out.println ("1 - Llista tots els noms de personatge.");
        System.out.println ("2 - Obté la informació d'un personatge.");
        System.out.println ("3 - Afegeix un personatge.");
        System.out.println ("4 - Elimina un personatge.");
        System.out.println ("5 - Sortir.");
    }



    /**
     * La funció getOption() recull la opció escollida per l'usuari
     */
    private static int getOption() {
        for (;;) {
            try {
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



    /**
     * 
     * La opció 1 és la encarregada de fer la petició a la BBDD per llistar els personatges,
     * envia un WriteInt(1) cap a la BBDD, i la resposta rebuda es un Int amb el n. de personatges
     * a la BBDD i tants Strings com personatges hi hagin, els quals els printa per pantalla.
     * 
     */
    private static void Option1 () {
        if (openConnection()) {
            System.out.println(ANSI_GREEN + "Connexió establerta amb el servidor.\n" + ANSI_RESET);
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



    /**
     * 
     * La opció 2 és la encarregada de fer la petició a la BBDD per llistar la informació d'un
     * personatge en concret, envia a la BBDD la petició amb el nom del personatge a printar, i
     * el servidor retorna un booleà el qual si es true vol dir que ha trobat el jugador, i si
     * es fals que no, en cas de ser true retorna també una llista de bytes amb la informació 
     * del personatge.
     * 
     */
    private static void Option2 () {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriu el nom del personatge: ");
            String name;
            name = in.readLine();
            System.out.println();

            if (openConnection()) {

                System.out.println(ANSI_GREEN + "Connexió establerta amb el servidor.\n" + ANSI_RESET);

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



    /**
     * 
     * La opció 3 és la encarregada de fer la petició a la BBDD per crear un personatge,
     * demanem al usuari el nom, la inteligència, la força i la constitució del personatge,
     * i envia la informació al servidor en una llista de bytes, el servidor retorna un
     * booleà amb true si s'ha creat el personatge i un false en cas de que ja existís.
     * 
     */
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

                System.out.println(ANSI_GREEN + "Connexió establerta amb el servidor.\n" + ANSI_RESET);
                System.out.println ("Afegint l'usuari a la Base de Dades...");

                dataOutputStream.writeInt(3);
                dataOutputStream.write(character.toBytes());
                dataOutputStream.flush();

                if (dataInputStream.readBoolean()) {
                    System.out.println(ANSI_GREEN + "Personatge afegit correctament.\n" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Error, aquest personatge ja està a la Base de Dades.\n" + ANSI_RESET);
                }
                closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 
     * La opció 4 és la encarregada de fer la petició a la BBDD per eliminar un personatge,
     * demanem a l'usuari el nom del personatge que volem eliminar i l'enviem al servidor,
     * el servidor ens retorna un booleà conforme si s'ha eliminat amb true o si no un false.
     * 
     */
    private static void Option4 () {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriu el nom del personatge: ");
            String name;
            name = in.readLine();
            System.out.println();

            if (openConnection()) {

                System.out.println(ANSI_GREEN + "Connexió establerta amb el servidor.\n" + ANSI_RESET);

                dataOutputStream.writeInt(4);
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                if (dataInputStream.readBoolean()) {
                    System.out.println(ANSI_GREEN + "Personatge eliminat correctament\n" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Personatge no trobat\n" + ANSI_RESET);
                }
                closeConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 
     * La opció 5 és la encarregada de tancar el client, amb un missatge i un exit 0 indicant
     * que no ha hagut error.
     * 
     */
    private static void Option5 () {
        System.out.println (ANSI_RED + "-----| Esperem que tornis aviat! |-----\n" + ANSI_RESET);
        System.exit(0);
    }



    /**
     * 
     * La funció openConnection() és la encarregada de fer la connexió entre client i servidor,
     * amb un temporitzador al socket al cual si s'acaba retorna servidor acupat.
     * 
     */
    private static boolean openConnection () {
        try {
            System.out.println("Intentant connexió...");
            socket = new Socket (address, port);
            socket.setSoTimeout(timeout);
            dataInputStream  = new DataInputStream  (socket.getInputStream());
            dataOutputStream = new DataOutputStream (socket.getOutputStream());
            return true;
        } catch (Exception e) {
            System.out.println (ANSI_RED + "Error, servidor ocupat." + ANSI_RESET);
            return false;
        }
    }



    /**
     * 
     * La funció closeConnection() és la encarregada de tancar la connexió entre client i servidor.
     * 
     */
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
