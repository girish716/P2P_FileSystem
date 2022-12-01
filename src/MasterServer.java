import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MasterServer {
    public static void main(String args[]) {
        try
        {
            // Create an object of the interface
            // implementation class
            Master obj = new MasterQuery();

            // rmiregistry within the server JVM with
            // port number 1901
            LocateRegistry.createRegistry(1901);

            // Binds the remote object by the name
            // geeksforgeeks
            Naming.rebind("rmi://10.200.57.66:1901/master",obj);
        }
        catch(Exception ae)
        {
            ae.printStackTrace();
        }
    }
}
