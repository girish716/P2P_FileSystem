import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;


public class MasterQuery extends UnicastRemoteObject implements Master
{
    // lookup : Filename -> List of peers containing that file
    private Map<String, Set<String>> lookup;
    // peers : Stores all the registered peers
    private Set<String> peers;
    // bin : to store what all files are deleted
    private Map<String, Boolean> isDeleted;

    // Default constructor to throw RemoteException
    // from its parent constructor
    MasterQuery() throws RemoteException
    {
        super();
        lookup = new HashMap<>();
        peers = new HashSet<>();
        isDeleted = new HashMap<>();

    }

    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean hasFile(String filename) throws RemoteException{
        try {
            if(lookup.containsKey(filename) && !isDeleted.get(filename)){
                System.out.println("Master has "+ filename);
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
            int size = peers.size();
            int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
            int i = 0;
            for(String peer : peers)
            {
                if (i == item)
                    return peer;
                i++;
            }
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
        return null;
    }

    /**
     * @param peerData
     * @return
     * @throws IOException
     */
    @Override
    public int registerPeer(String peerData) throws IOException{
        try {
            if(peerData!=null && peerData!="") {
                if(!peers.contains(peerData)){
                    peers.add(peerData);
                    return 1;
                } else {
                    return 0;
                }

            }
        } catch (Exception io){
            io.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        try{
            isDeleted.put(fileName, true);
            return true;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    @Override
    public boolean restoreFile(String fileName) throws IOException {
        try{
            isDeleted.put(fileName, false);
            return true;
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
            isDeleted.put(filename, false);
            System.out.println(filename + " updated in the lookup table");
        } catch (Exception io){
            io.printStackTrace();
        }
        return null;
    }
}