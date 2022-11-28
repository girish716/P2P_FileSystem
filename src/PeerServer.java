import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class PeerServer {
    public static void main(String args[]) {
        Integer masterPORT = 1901;
        String masterIP = "localhost";
        Integer serverPORT = 1900;
        String serverIP = "localhost";

        try
        {
            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+masterIP+":"+masterPORT+"/master");

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
            LocateRegistry.createRegistry(1900);

            // Binds the remote object by the name
            // geeksforgeeks
            Naming.rebind("rmi://"+serverIP+":"+serverPORT+"/master",obj);
        }
        catch(Exception ae)
        {
            ae.printStackTrace();
        }
    }
}
