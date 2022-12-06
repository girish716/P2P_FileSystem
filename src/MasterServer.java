import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.io.FileInputStream;

public class MasterServer {
    public static void main(String args[]) {
        String masterPORT;
        String masterIP;

        try
        {
            Properties prop = new Properties();
            //ResourceBundle prop
                  //  = ResourceBundle.getBundle("config.properties");
            prop.load(new FileInputStream("../resources/config.properties"));
            //Reading each property value
            masterPORT = prop.getProperty("MASTER_PORT");
            masterIP = prop.getProperty("MASTER_IP");

            // Create an object of the interface
            // implementation class
            Master obj = new MasterQuery();

            // rmiregistry within the server JVM with
            // port number 1901
            LocateRegistry.createRegistry(Integer.parseInt(masterPORT));

            // Binds the remote object by the name
            // geeksforgeeks
            Naming.rebind("rmi://"+masterIP+":"+masterPORT+"/master",obj);
        }
        catch(Exception ae)
        {
            ae.printStackTrace();
        }
    }
}
