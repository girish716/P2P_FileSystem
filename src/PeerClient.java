import java.rmi.Naming;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;


public class PeerClient {
    String masterPORT;
    String masterIP;
    String serverPORT;
    String serverIP;

    PeerClient(){
        try{
            Properties prop = new Properties();
            prop.load(new FileInputStream("config.properties"));
            //Reading each property value
            this.masterPORT = prop.getProperty("MASTER_PORT");
            this.masterIP = prop.getProperty("MASTER_IP");
            this.serverPORT = prop.getProperty("SERVER_port");
            this.serverIP = prop.getProperty("SERVER_IP");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createFile(Master masterServer){
        try{
            //fetch IP and PORT of random peer
            String peerData = masterServer.getPath();
            // lookup method to find reference of remote object
            FDS peerServer =
                    (FDS)Naming.lookup("rmi://"+peerData);
            String response = peerServer.create("abc.txt", "Hello world");
            System.out.println("File Successfully created....");
            if(response!=null){
                masterServer.updateCache("rmi://"+this.serverIP+":"+this.serverPORT, response);
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
                    (Master)Naming.lookup("rmi://"+clientServer.masterIP+":"+clientServer.masterPORT);
            System.out.println(masterAccess);
            // create
            clientServer.createFile(masterAccess);
            clientServer.readFile(masterAccess, "abc.txt");
            clientServer.deleteFile(masterAccess, "abc.txt");
            clientServer.restoreFile(masterAccess, "abc.txt");
            clientServer.updateFile(masterAccess, "abc.txt", " - updated");
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }
}
