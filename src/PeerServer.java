import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PeerServer {
    public static void main(String args[]) {
        String masterPORT;
        String masterIP;
        String serverPORT;
        String serverIP;

        try
        {
            Properties prop = new Properties();
            prop.load(new FileInputStream("config.properties"));
            //Reading each property value
            masterPORT = prop.getProperty("MASTER_PORT");
            masterIP = prop.getProperty("MASTER_IP");
            serverPORT = prop.getProperty("SERVER_port");
            serverIP = prop.getProperty("SERVER_IP");


            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+masterIP+":"+masterPORT);

            // registers the user in Master server
            if(masterAccess.registerPeer(serverIP+":"+serverPORT)){
                System.out.println("Server registred with Master");
            }else{
                System.out.println("Server not registred with Master");
            }
            // Create an object of the interface
            // implementation class
            FDS obj = new FDSQuery();

            // rmiregistry within the server JVM with
            // port number 1900
            LocateRegistry.createRegistry(Integer.parseInt(serverPORT));

            // Binds the remote object by the name
            // geeksforgeeks
            Naming.rebind("rmi://"+serverIP+":"+serverPORT,obj);
        }
        catch(Exception ae)
        {
            ae.printStackTrace();
        }
    }
}
