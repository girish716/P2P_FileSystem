import java.rmi.Naming;

public class PeerClient {
    public static void main(String args[]) {
        String response;
//        String IP = "10.200.59.200";
//        String IP = "10.200.140.201";
        String port = "1900";
        String IP = "localhost";
        try
        {
            // lookup method to find reference of remote object
            FDS access =
                    (FDS)Naming.lookup("rmi://"+IP+":"+port+"/master");
            System.out.println(access);
            response = access.create("abc.txt", "Hello world");

            System.out.println(response);
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
}
