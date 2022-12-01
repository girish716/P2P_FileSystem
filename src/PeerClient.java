import java.rmi.Naming;
import java.util.*;

public class PeerClient {
    Integer masterPORT;
    String masterIP;
    Integer port;
    String IP;

    PeerClient(){
        this.masterPORT = 1901;
        this.masterIP = "10.200.5.190";
        this.port = 1900;
        this.IP = "10.200.5.190";
    }

    public void createFile(Master masterServer){
        try{
            //fetch IP and PORT of random peer
            String peerData = masterServer.getPath();
            // lookup method to find reference of remote object
            FDS peerServer =
                    (FDS)Naming.lookup("rmi://"+peerData+"/master");
            String response = peerServer.create("abc.txt", "Hello world");
            System.out.println("File Successfully created....");
            if(response!=null){
                masterServer.updateCache("rmi://"+this.IP+":"+this.port+"/master", response);
                List<String> paths = masterServer.getPaths(response);
                System.out.println(paths);
            }
            System.out.println(response);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void readFile(Master masterServer, String fileName){
        try{
            if(!masterServer.hasFile(fileName)) return;

            List<String> paths = masterServer.getPaths(fileName);
            String peerPath = paths.get(0);

            // connect with server
            FDS peerServer =
                    (FDS)Naming.lookup(peerPath);
            String fileData = peerServer.read(fileName);
            if(fileData==null){
                System.out.println("Failed to read file......");
            }
            System.out.println("File Data : "+ fileData);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void updateFile(Master masterServer, String fileName, String updatedData){
        try{
            if(!masterServer.hasFile(fileName)) return;

            List<String> paths = masterServer.getPaths(fileName);
            String peerPath = paths.get(0);

            // connect with server
            FDS peerServer =
                    (FDS)Naming.lookup(peerPath);
            String fileData = peerServer.read(fileName);
            String newData = fileData + updatedData;
            String updatedFileData = peerServer.update(fileName, newData);
            System.out.println("Updated file data - "+updatedFileData);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void deleteFile(Master masterServer, String fileName){
        try{
            if(!masterServer.hasFile(fileName)){
                System.out.println("File not available....");
                return;
            }
            List<String> paths = masterServer.getPaths(fileName);
            String peerPath = paths.get(0);

            // connect with server
            FDS peerServer =
                    (FDS)Naming.lookup(peerPath);
            peerServer.delete(fileName);
            masterServer.deleteFile(fileName);
            System.out.println("successfully deleted - "+ fileName);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void restoreFile(Master masterServer, String fileName){
        try{
            if(masterServer.restoreFile(fileName)){
                List<String> paths = masterServer.getPaths(fileName);
                String peerPath = paths.get(0);

                // connect with server
                FDS peerServer =
                        (FDS)Naming.lookup(peerPath);
                peerServer.restore(fileName);
                System.out.println("restore completed for - "+ fileName);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String args[]) {
        String response;

        try
        {
            PeerClient clientServer = new PeerClient();

            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+clientServer.masterIP+":"+clientServer.masterPORT+"/master");
            System.out.println(masterAccess);
            // create
            clientServer.createFile(masterAccess);
            clientServer.readFile(masterAccess, "abc.txt");
            clientServer.deleteFile(masterAccess, "abc.txt");
            clientServer.restoreFile(masterAccess, "abc.txt");
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
}
