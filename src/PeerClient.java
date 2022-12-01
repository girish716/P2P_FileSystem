import java.rmi.Naming;
import java.util.*;

public class PeerClient {
    public void createFile(Master masterServer){
        //fetch IP and PORT of random peer
        String peerData = masterServer.getPath();
        // lookup method to find reference of remote object
        FDS peerServer =
                (FDS)Naming.lookup("rmi://"+peerData+"/master");
        String response = peerServer.create("abc.txt", "Hello world");
        System.out.println("File Successfully created....");
        if(response!=null){
            masterServer.updateLookup("rmi://"+IP+":"+port+"/master", response);
            List<String> paths = masterServer.getPaths(response);
            System.out.println(paths);
        }
        System.out.println(response);
    }

    public void readFile(Master masterServer, String fileName){
        if(!masterServer.hasFile(fileName)) return;

        List<String> paths = masterServer.getPaths(fileName);
        String peerPath = paths.get(0);

        // connect with server
        FDS peerServer =
                (FDS)Naming.lookup("rmi://"+peerData+"/master");
        String fileData = peerServer.read(fileName)
        if(fileData==null){
            System.out.println("Failed to read file......")
        }
        System.out.println("File Data : "+ fileData);
    }

    public void updateFile(Master masterServer, String fileName, String updatedData){
        if(!masterServer.hasFile(fileName)) return;

        List<String> paths = masterServer.getPaths(fileName);
        String peerPath = paths.get(0);

        // connect with server
        FDS peerServer =
                (FDS)Naming.lookup("rmi://"+peerData+"/master");
        String fileData = peerServer.read(fileName);

    }

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
            System.out.println(masterAccess);
            // create
            createFile(masterAccess);



        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
}
