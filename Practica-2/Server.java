

// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

// ACLARAMENT: Pràctica funcional amb el codi que ens has passat pel campus, pujo
// únicament els arxius Client.java i Server.java els quals son on està tota la feina feta.



import java.net.*;
import java.io.*;

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


    /**
     * 
     * La funció main inicia la BBDD i el serverSocket per establir connexions.
     * un cop tot iniciat, fem un bucle infinit amb el que cridem les funcions 
     * openConnection(), getOption() i  closeConnection(), no hi ha forma de parar
     * el programa més que matant-lo per terminal.
     * 
     */
    public static void main (String[] args) {

        try {
            charactersDB = new CharactersDB (CHARACTERS_DB_NAME);
            serverSocket = new ServerSocket (port);
        } catch (IOException ex) {
            System.err.println (ANSI_RED + "Error opening database!" + ANSI_RESET);
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



    /**
     * 
     * La funció getOption() agafa  la opció que ens envia l'usuari i executa la funció corresponent.
     * 
     */
    private static void getOption () {
        try {
            int option = dataInputStream.readInt();
            System.out.println ("L'usuari ha escollit la opció: " + option);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 
     * La Option1() és l'encarregada de passar a l'usuari el llistat de personatges disponibles
     * a la BBDD, el qual primer passa un int amb el numero de personatges, i seguidament el n.
     * total de Strings necessaries amb els noms
     * 
     */
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


    
    /**
     * 
     * La Option1() és l'encarregada de passar a l'usuari un personatge en concret i la seva informació,
     * llegeix el nom del personatge i, en cas de que el personatge existeixi retorna un true i la informació
     * del personatge amb una llista de bytes, en cas de no trobar-lo torna únicament el booleà en false.
     * 
     */
    private static void Option2 () {

        System.out.println ("El servidor ha rebut la petició n. 2, processant...");

        try {
            String name = dataInputStream.readUTF();

            int n = charactersDB.searchCharacterByName (name);
            dataOutputStream.writeBoolean(n != -1);
            if (n != -1) {
                CharacterInfo character = charactersDB.readCharacterInfo (n);
                dataOutputStream.write(character.toBytes());
            }
            dataOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println (ANSI_GREEN + "La Petició ha estat processada correctament." + ANSI_RESET);
    }



    /**
     * 
     * La Option3() és l'encarregada de crear un personatge nou, la qual rep una llista de bytes
     * la cual la guarda en forma de CharacterInfo i intenta crear l'usuari, en cas de poder li 
     * retorna s l'usuari un true i en cas contrari un false.
     * 
     */
    private static void Option3 () {
        System.out.println ("El servidor ha rebut la petició n. 3, processant...");

        try {
            CharacterInfo character = CharacterInfo.fromBytes(dataInputStream.readNBytes(CharacterInfo.SIZE));
            boolean success = charactersDB.insertNewCharacter (character);

            dataOutputStream.writeBoolean(success);
            dataOutputStream.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println (ANSI_GREEN + "La Petició ha estat processada correctament." + ANSI_RESET);
    }



    /**
     * 
     * La Option4() és l'encarregada d'eliminar un personatge, la cual rep un string de l'usuari
     * amb el nom del personatge a eliminar, en cas de que l'usuari existeixi l'elimina i retorna
     * un booleà amb true, en cas contrari unicament retorna false a l'usuari.
     * 
     */
    private static void Option4 () {
        System.out.println ("El servidor ha rebut la petició n. 4, processant...");

        try {
            
            boolean success = charactersDB.deleteByName (dataInputStream.readUTF());

            dataOutputStream.writeBoolean(success);
            dataOutputStream.flush();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println (ANSI_GREEN + "La Petició ha estat processada correctament." + ANSI_RESET);
    }



    /**
     * 
     * La funció openConnection() és la encarregada de fer la connexió entre client i servidor.
     * 
     */
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



    /**
     * 
     * La funció closeConnection() és la encarregada de tancar la connexió actual entre client i servidor.
     * 
     */
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
