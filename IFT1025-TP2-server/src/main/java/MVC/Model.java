package MVC;

import server.Server;
import server.ServerLauncher;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Model {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Connecte le GUI a notre serveur ce qui permet l'echange d'information notamment la reception de cours disponibles
     * ainsi que l'inscription au cours
     */
    public void connectServer(){
        try {

            Socket socket = new Socket("localhost", ServerLauncher.PORT);
             outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction sert a prendre les cours du server selon la session specifiee. Appelee par controlleur
     * @param session La session pour laquelle on veut afficher les cours
     * @return ArrayList des cours
     */
    public ArrayList<Course> loadCours(String session){
        ArrayList<Course> cours = new ArrayList<>();
        try {
            outputStream.writeObject(Server.LOAD_COMMAND + " " + session);
            outputStream.flush();

            cours = (ArrayList<Course>) inputStream.readObject();

        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        finally {
            return cours;
        }
    }
}
