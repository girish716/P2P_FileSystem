import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class PeerServer {
    public static void main(String args[]) {
        try
        {
            // Create an object of the interface
            // implementation class
            FDS obj = new FDSQuery();

            // rmiregistry within the server JVM with
            // port number 1900
            LocateRegistry.createRegistry(1900);

            // Binds the remote object by the name
            // geeksforgeeks
            Naming.rebind("rmi://localhost:1900/master",obj);
        }
        catch(Exception ae)
        {
            ae.printStackTrace();
        }
    }
}
