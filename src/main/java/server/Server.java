package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        try {
            //Lecture du fichier cours.txt
            BufferedReader reader = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
            String line;
            ArrayList<Course> cours = new ArrayList<>();

            //Lecture des lignes
            while ((line = reader.readLine()) != null){
                String[] contents = line.split("\t");
                if (contents[2].equals(arg)){
                    cours.add(new Course(contents[1], contents[0], contents[2]));
                }
            }
            // Fermeture de reader pour eviter des bugs
            reader.close();

            //Envoi de la liste d'objets cours au client
            objectOutputStream.writeObject(cours);
            objectOutputStream.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try{
            //Lecture de Registration form
            RegistrationForm form = (RegistrationForm) objectInputStream.readObject();
            //Ecriture des information de registration form dans inscription.txt
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/server/data/inscription.txt", true));
            writer.write(form.getCourse().getSession()+"\t"+form.getCourse().getCode()+"\t"+form.getMatricule()+
                    "\t"+form.getPrenom()+"\t"+form.getNom()+"\t"+form.getEmail()+"\n");
            writer.close();
            // Renvoi du message de confirmation
            objectOutputStream.writeObject("Inscription reussie de "+form.getPrenom()+" pour le cours "+form.getCourse().getCode()+"!");
            objectOutputStream.flush();
        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
    }
}

