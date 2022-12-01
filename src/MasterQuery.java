import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;


public class MasterQuery extends UnicastRemoteObject implements Master
{
    private HashMap<String, Set<String>> lookup;
    private List<String> peers;
    // Default constructor to throw RemoteException
    // from its parent constructor
    MasterQuery() throws RemoteException
    {
        super();
        lookup = new HashMap<>();
        peers = new ArrayList<>();

    }

    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean hasFile(String filename) throws RemoteException{
        try {
            if(lookup.containsKey(filename)){
                System.out.println("master has file "+ filename);
                return true;
            }
        } catch (Exception io){
            io.printStackTrace();
        }
        return false;
    }


    /**
     * @return
     * @throws IOException
     */
    @Override
    public String getPath() throws IOException{
        try {
            Random rand = new Random();
            // Generate random integers in range 0 to length of peers list
            int randIndex = rand.nextInt(peers.size());
            return peers.get(randIndex);
        } catch (Exception io) {
            io.printStackTrace();
        }
        return null;
    }


    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public List<String> getPaths(String filename) throws RemoteException{
        try {
            if(hasFile(filename)){
                Set<String> setOfPaths = lookup.get(filename);
                List<String> paths = new ArrayList<>(setOfPaths);
                return paths;
            }
        } catch (Exception io){
            io.printStackTrace();
        }
        return new ArrayList<String>();
    }

    /**
     * @param peerData
     * @return
     * @throws IOException
     */
    @Override
    public boolean registerPeer(String peerData) throws IOException{
        try {
            if(peerData!=null && peerData!="") {
                peers.add(peerData);
                return true;
            }
        } catch (Exception io){
            io.printStackTrace();
        }
        return false;
    }

    /**
     * @param path
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public String updateCache(String path, String filename) throws RemoteException{
        try {
            Set<String> paths;
            if(lookup.containsKey(filename)){
                paths = lookup.get(filename);
                paths.add(path);
            }
            else {
                paths = new HashSet<>();
                paths.add(path);
            }
            lookup.put(filename, paths);
            System.out.println("Successfully notified master.....");
        } catch (Exception io){
            io.printStackTrace();
        }
        return null;
    }
}