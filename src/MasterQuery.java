import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;


public class MasterQuery extends UnicastRemoteObject implements Master
{
    private HashMap<String, Set<String>> lookup;
    private List<String> peers;

    private HashMap<String, Set<String>> bin;

    // Default constructor to throw RemoteException
    // from its parent constructor
    MasterQuery() throws RemoteException
    {
        super();
        lookup = new HashMap<>();
        peers = new ArrayList<>();
        bin = new HashMap<>();

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

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        try{
            Set<String> removedFilePaths = lookup.remove(fileName);
            if(removedFilePaths!=null){
                bin.put(fileName, removedFilePaths);
                return true;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    @Override
    public boolean restoreFile(String fileName) throws IOException {
        try{
            Set<String> restoreFilePaths = bin.remove(fileName);
            if(restoreFilePaths!=null){
                lookup.put(fileName, restoreFilePaths);
                return true;
            }
        }
        catch (Exception e){
            System.out.println(e);
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