import java.rmi.Naming;
import java.util.*;
import java.io.FileInputStream;


public class PeerClient {
    String masterPORT;
    String masterIP;
    String serverPORT;
    String serverIP;
    String myURI;

    PeerClient(){
        try{
            Properties prop = new Properties();
            prop.load(new FileInputStream("../resources/config.properties"));
            //Fetching each property value
            this.masterPORT = prop.getProperty("MASTER_PORT");
            this.masterIP = prop.getProperty("MASTER_IP");
            this.serverPORT = prop.getProperty("SERVER_PORT");
            this.serverIP = prop.getProperty("SERVER_IP");
            this.myURI = "rmi://" + this.serverIP+":"+this.serverPORT + "/peer";
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createFile(Master masterServer, String fileName, String fileData){
        try{
            //fetch IP and PORT of random peer
            Set<String> peersURI = masterServer.create(fileName, myURI);
            if(peersURI == null) {
                System.out.println(fileName + " already exists");
                return;
            }
            // lookup method to find reference of remote object
            for(String peerURI : peersURI){
                FDS peerServer =
                        (FDS)Naming.lookup(peerURI);
                peerServer.create(fileName, fileData);
            }
            System.out.println("Successfully created " + fileName);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void readFile(Master masterServer, String fileName){
        try{
            String response = masterServer.read(fileName, myURI);
            if(response.equals(fileName + " doesn't exist")){
                System.out.println(response);
                return;
            } else if(response.equals("The peer doesn't have permission to read")){
                System.out.println("You don't have permission to read");
                return;
            }

            // connect with server
            FDS peerServer =
                    (FDS)Naming.lookup(response);
            String fileData = peerServer.read(fileName);
            if(fileData==null){
                System.out.println("Failed to read " + fileName);
            }
            System.out.println("File Data : \n"+ fileData);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void updateFile(Master masterServer, String fileName, String newData){
        try {
            Map.Entry<String, Set<String>> response = masterServer.update(fileName, myURI);
            if(response.getKey().equals(fileName + " doesn't exit")){
                System.out.println(response);
                return;
            } else if(response.getKey().equals("The peer doesn't have permission to write")){
                System.out.println("You don't have permission to write");
                return;
            }
            // connect with server
            for(String peer : response.getValue()){
                FDS peerServer =
                        (FDS)Naming.lookup(peer);
                peerServer.update(fileName, " "+newData);
            }
            System.out.println("Successfully updated the " + fileName + " data");
        }
            catch(Exception e){
            System.out.println(e);
        }
    }
    public void writeFile(Master masterServer, String fileName, String data){
        try {
            Map.Entry<String, Set<String>> response = masterServer.update(fileName, myURI);
            if(response.getKey().equals(fileName + " doesn't exit")){
                System.out.println(response);
                return;
            } else if(response.getKey().equals("The peer doesn't have permission to write")){
                System.out.println("You don't have permission to write");
                return;
            }
            // connect with server
            for(String peer : response.getValue()){
                FDS peerServer =
                        (FDS)Naming.lookup(peer);
                peerServer.write(fileName, " "+data);
            }
            System.out.println("Successfully wrote to the " + fileName);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void delete(Master masterServer, String fileName){
        try{
            String response = masterServer.delete(fileName, myURI);
            if(response.equals(fileName + " doesn't exit")){
                System.out.println(response);
                return;
            } else if(response.equals("The peer doesn't have permission to delete/restore")){
                System.out.println("You don't have permission to delete");
                return;
            }

            // connect with server
            FDS peerServer =
                    (FDS)Naming.lookup(response);
            peerServer.delete(fileName);
            System.out.println("successfully deleted "+ fileName);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void restore(Master masterServer, String fileName){
        try{
            String response = masterServer.restore(fileName, myURI);
            if(response.equals(fileName + " already exist")){
                System.out.println(response);
                return;
            } else if(response.equals("The peer doesn't have permission to delete/restore")){
                System.out.println("You don't have permission to delete/restore");
                return;
            }
            FDS peerServer =
                    (FDS)Naming.lookup(response);
            peerServer.restore(fileName);
            System.out.println(fileName + " Successfully Restored ");
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void delegatePermission(Master masterServer, String fileName, String otherURI, String permission) {
        try{
            String response = masterServer.delegatePermission(fileName, myURI, otherURI, permission);
            System.out.println(response);
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
                        "7. Delegate permissions" +
                        "8. Help" + " " +
                        "9. Exit"
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
                    System.out.println("Enter File name to be updated - ");
                    String fileName = sc.nextLine();
                    System.out.println("Enter the file data - ");
                    String fileData = sc.nextLine();
                    clientServer.writeFile(masterAccess, fileName, fileData);
                } else if (userInput == 4){
                    System.out.println("Enter File name to be updated - ");
                    String fileName = sc.nextLine();
                    System.out.println("Enter new data to be appended - ");
                    String fileData = sc.nextLine();
                    clientServer.updateFile(masterAccess, fileName, fileData);
                } else if (userInput == 5){
                    System.out.println("Enter File name to delete - ");
                    String fileName = sc.nextLine();
                    clientServer.delete(masterAccess, fileName);
                } else if (userInput == 6){
                    System.out.println("Enter File name to restore - ");
                    String fileName = sc.nextLine();
                    clientServer.restore(masterAccess, fileName);
                } else if(userInput == 7){
                    System.out.println("Enter file name to delegate the permission");
                    String fileName = sc.nextLine();
                    System.out.println("Enter IP address and port ex:10.0.4.245:1099");
                    String uri = sc.nextLine();
                    uri = "rmi://" + uri +"/peer";
                    System.out.println("Enter permission i.e, read, write, delete");
                    String permission = sc.nextLine();
                    clientServer.delegatePermission(masterAccess, fileName, uri, permission);
                } else if (userInput == 8){
                    clientServer.displayHelp();
                } else if (userInput == 9){
                    System.out.println("Exiting out of file distributed system");
                    break;
                }
                else{
                    System.out.println("Invalid Choice, please enter correct choice");
                }
            };
        }
        catch(Exception ae)
        {
            System.out.println(ae);
        }
    }



    public void displayHelp() {
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
