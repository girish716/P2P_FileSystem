import java.rmi.Naming;
import java.util.*;

public class PeerClient {
    public static void main(String args[]) {
        String response;
        Integer masterPORT = 1901;
        String masterIP = "localhost";
        Integer port = 1900;
        String IP = "localhost";

        try
        {
            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+masterIP+":"+masterPORT+"/master");
            //fetch IP and PORT of random peer
            String peerData = masterAccess.getPath();
            // lookup method to find reference of remote object
            FDS access =
                    (FDS)Naming.lookup("rmi://"+peerData+"/master");
            System.out.println(access);
            System.out.println(masterAccess);

            response = access.create("abc.txt", "Hello world");
            System.out.println("File Successfully created....");
            if(response!=null){
                masterAccess.notifyMaster("rmi://"+IP+":"+port+"/master", response);
                List<String> paths = masterAccess.getPaths(response);
                System.out.println(paths);
            }
            System.out.println(response);
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
}
