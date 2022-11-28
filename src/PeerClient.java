import java.rmi.Naming;
import java.util.*;

public class PeerClient {
    public static void main(String args[]) {
        String response;
//        String IP = "10.200.59.200";
//        String IP = "10.200.140.201";
        String port = "1900";
        String IP = "localhost";

        //master
        String mastePort = "1901";
        String masterIP = "localhost";
        try
        {
            // lookup method to find reference of remote object
            FDS access =
                    (FDS)Naming.lookup("rmi://"+IP+":"+port+"/master");
            System.out.println(access);
            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+masterIP+":"+mastePort+"/master");
            System.out.println(masterAccess);
            response = access.create("abc.txt", "Hello world");
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
