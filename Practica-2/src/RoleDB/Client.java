package RoleDB;
// Aquesta practica ha estat realitzada per l'alumne Joan Francesc Pedro Garcia
// Practica 2 de Xarxes

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    public static void main(String[] args) {
        try {

        } catch (Exception e) {
            System.out.println("Error de connexió");
            System.exit(0);
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
}
