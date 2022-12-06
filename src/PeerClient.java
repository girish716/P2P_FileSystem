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
            prop.load(new FileInputStream("../resources/config.properties"));
            //Reading each property value
            this.masterPORT = prop.getProperty("MASTER_PORT");
            this.masterIP = prop.getProperty("MASTER_IP");
            this.serverPORT = prop.getProperty("SERVER_PORT");
            this.serverIP = prop.getProperty("SERVER_IP");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createFile(Master masterServer, String fileName, String fileData){
        try{
            //fetch IP and PORT of random peer
            String peerData = masterServer.getPath();
            // lookup method to find reference of remote object
            FDS peerServer =
                    (FDS)Naming.lookup(peerData);
            String response = peerServer.create(fileName, fileData);
            System.out.println("Successfully created " + fileName);
            if(response!=null){
                masterServer.updateCache("rmi://" + this.serverIP+":"+this.serverPORT + "/peer", response);
                List<String> paths = masterServer.getPaths(response);
                System.out.println(paths);
            }
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
        int userInput;

        try
        {
            PeerClient clientServer = new PeerClient();

            Master masterAccess =
                    (Master)Naming.lookup("rmi://"+clientServer.masterIP+":"+clientServer.masterPORT+"/master");
            System.out.println(masterAccess);
            System.out.println("Welcome to the Peer to Peer Distributed File System");

            Scanner sc = new Scanner(System.in);
            while(true){
                System.out.println( "Please select one of the below options" + "\n" +
                        "1. Create " + " " +
                        "2. Read " + " " +
                        "3. Write " + "\n" +
                        "4. Update " + " " +
                        "5. Delete " + " " +
                        "6. Restore " + "\n" +
                        "7. Help" + " " +
                        "8. Exit"
                );
                if(sc.hasNextInt())
                    userInput = Integer.parseInt(sc.nextLine());
                else {
                    sc.nextLine();
                    userInput = 0;
                }
                if(userInput == 1){
                    System.out.println("Enter File name to be created - ");
                    String fileName = sc.nextLine();
                    System.out.println("Enter File data - ");
                    String fileData = sc.nextLine();
                    clientServer.createFile(masterAccess, fileName, fileData);
                } else if (userInput == 2){
                    System.out.println("Enter File name to read - ");
                    String fileName = sc.nextLine();
                    clientServer.readFile(masterAccess, fileName);
                } else if (userInput == 3){
                    // yet to write
                } else if (userInput == 4){
                    System.out.println("Enter File name to be created - ");
                    String fileName = sc.nextLine();
                    System.out.println("Enter File data to be appended - ");
                    String fileData = sc.nextLine();
                    clientServer.updateFile(masterAccess, fileName, fileData);
                } else if (userInput == 5){
                    System.out.println("Enter File name to read - ");
                    String fileName = sc.nextLine();
                    clientServer.deleteFile(masterAccess, fileName);
                } else if (userInput == 6){
                    System.out.println("Enter File name to restore - ");
                    String fileName = sc.nextLine();
                    clientServer.restoreFile(masterAccess, fileName);
                } else if(userInput == 7){
                    clientServer.displayHelp();
                } else if (userInput == 8){
                    System.out.println("Exiting out of file distributed system");
                    break;
                } else{
                    System.out.println("Invalid Choice, please enter correct choice");
                }
            };




            clientServer.updateFile(masterAccess, "abc.txt", " - updated");
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }

    private void displayHelp() {
        System.out.println(
                "1. Create - create a file with the given name and given data" + "\n" +
                "2. Read - read a file with the given name" + "\n" +
                "3. Write - Replace the contents of the file with new given data" + "\n" +
                "4. Update - Append new content to the existing contents of the file" + "\n" +
                "5. Delete - Delete the file" + "\n" +
                "6. Restore - Restore the file"
        );
    }
}
