import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class MasterQuery extends UnicastRemoteObject implements Master
{
    // lookup : Filename -> List of peers containing that file
    private Map<String, Set<String>> lookup;
    // peers : Stores all the registered peers
    private Set<String> peers;
    // bin : to store what all files are deleted
    private Map<String, Boolean> isDeleted;
    // Permissions Hashmap to manage all the permissions related to a file
    private Map<String, Permissions> permissions;
    // Hashmap to manage encryption keys for each file
    private Map<String, String> secretKeys;

    //Scheduler for malware check
    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public String read(String fileName, String uri) throws RemoteException {
        try{
            String message;
            if(!hasFile(fileName)) {
                message = fileName + " doesn't exist";
                return message;
            }

            Permissions permissionObj = permissions.get(fileName);
            if(!permissionObj.canRead(uri)){
                message = "The peer doesn't have permission to read";
                return message;
            }

            List<String> paths = getPaths(fileName);
            String peerPath = paths.get(0);
            return peerPath;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String create(String fileName, String uri) throws RemoteException {
        try {
            if (hasFile(fileName)){
               System.out.println(fileName + " already exist");
               return null;
            }
            String otherPeerURI = getPath();
            Set<String> peerSet;
            if(lookup.containsKey(fileName)){
                peerSet = lookup.get(fileName);
                peerSet.add(otherPeerURI);
            }
            else {
                peerSet = new HashSet<>();
                peerSet.add(otherPeerURI);
            }
            Permissions permissionObj = new PermissionsImpl(fileName, uri);
            permissions.put(fileName, permissionObj);
            lookup.put(fileName, peerSet);
            isDeleted.put(fileName, false);
            System.out.println(fileName + " data updated in the lookup table");
            return otherPeerURI;
        } catch (Exception io){
            io.printStackTrace();
        }
        return null;
    }

    @Override
    public void write(String fileName, String URI) throws RemoteException {

    }

    @Override
    public String delegatePermission(String fileName, String uri, String otherURI, String permission) throws RemoteException {
        try{
            String message;
            if(!hasFile(fileName)) {
                message = fileName + " doesn't exit";
                return message;
            }
            Permissions permissionObj = permissions.get(fileName);
            if(permission.equals("read")){
                if(permissionObj.canRead(uri)){
                    if(permissionObj.canRead(otherURI)){
                        message = "The other peer already have "+permission;
                        return message;
                    } else {
                        permissionObj.setRead(otherURI);
                    }
                } else {
                    message = "The peer doesn't have " + permission + " permission";
                    return message;
                }
            }
            if(permission.equals("write")){
                if(permissionObj.canWrite(uri)){
                    if(permissionObj.canWrite(otherURI)){
                        message = "The other peer already have "+permission;
                        return message;
                    } else {
                        permissionObj.setWrite(otherURI);
                    }
                } else {
                    message = "The peer doesn't have " + permission + " permission";
                    return message;
                }
            }
            if(permission.equals("delete")){
                if(permissionObj.canDelete(uri)){
                    if(permissionObj.canDelete(otherURI)){
                        message = "The other peer already have "+permission;
                        return message;
                    } else {
                        permissionObj.setWrite(otherURI);
                    }
                } else {
                    message = "The peer doesn't have " + permission + " permission";
                    return message;
                }
            }

        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String delete(String fileName, String uri) throws RemoteException {
        try{
            String message;
            if(!hasFile(fileName)) {
                message = fileName + " doesn't exit";
                return message;
            }
            Permissions permissionObj = permissions.get(fileName);
            if(!permissionObj.canDelete(uri)){
                message = "The peer doesn't have permission to delete/restore";
                return message;
            }
            List<String> paths = getPaths(fileName);
            String peerURI = paths.get(0);
            isDeleted.put(fileName, true);
            return peerURI;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;

    }

    @Override
    public String update(String fileName, String uri) throws RemoteException {
        try{
            String message;
            if(!hasFile(fileName)) {
                message = fileName + " doesn't exit";
                return message;
            }

            Permissions permissionObj = permissions.get(fileName);
            if(!permissionObj.canWrite(uri)){
                message = "The peer doesn't have permission to write";
                return message;
            }

            List<String> paths = getPaths(fileName);
            String peerPath = paths.get(0);
            return peerPath;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;

    }

    @Override
    public String restore(String fileName, String uri) throws RemoteException {
        try{
            String message;
            if(hasFile(fileName)) {
                message = fileName + " already exist";
                return message;
            }

            Permissions permissionObj = permissions.get(fileName);
            if(!permissionObj.canWrite(uri)){
                message = "The peer doesn't have permission to delete/restore";
                return message;
            }

            List<String> paths = getPaths(fileName);
            String peerPath = paths.get(0);
            isDeleted.put(fileName, false);
            return peerPath;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;

    }

    // Default constructor to throw RemoteException
    // from its parent constructor
    MasterQuery() throws RemoteException
    {
        super();
        lookup = new HashMap<>();
        peers = new HashSet<>();
        isDeleted = new HashMap<>();
        permissions = new HashMap<>();
        secretKeys = new HashMap<>();

    }

    @Override
    public boolean hasFile(String filename) throws RemoteException{
        try {
            if(lookup.containsKey(filename) && !isDeleted.get(filename)){
                System.out.println("Lookup Successfull \n" +
                        "Master has "+ filename);
                return true;
            }
        } catch (Exception io){
            io.printStackTrace();
        }
        return false;
    }

    @Override
    public String getPath() throws IOException{
        try {
            int size = peers.size();
            int item = new Random().nextInt(size);
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
    public void maliciousCheck() throws IOException {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    for(String fileName : lookup.keySet()){
                        for(String peerPath : lookup.get(fileName)){
                            // connect with server
                            FDS peerServer =
                                    (FDS)Naming.lookup(peerPath);
                            String fileData = peerServer.read(fileName);
                            if(fileData==null){
                                System.out.println("Malicious activity detected in the Master Server......");
                                System.out.println("Exiting......");
                                System.exit(1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }


}