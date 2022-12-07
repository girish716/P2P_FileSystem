import java.io.IOException;
import java.rmi.*;
import java.util.*;

public interface Master extends Remote{
    // Declaring the method prototype
    public boolean hasFile(String filename) throws RemoteException;
    public List<String> getPaths(String filename) throws RemoteException;
    public String getPath() throws IOException;
    public int registerPeer(String peerData) throws IOException;
    public void maliciousCheck() throws IOException;
    public String read(String fileName, String uri) throws RemoteException;
    public Set<String> create(String fileName, String uri) throws RemoteException;
    public Map.Entry<String, Set<String>> write(String fileName, String uri) throws RemoteException;
    public String delete(String fileName, String uri) throws RemoteException;
    public Map.Entry<String, Set<String>> update(String fileName, String uri) throws RemoteException;
    public String restore(String fileName, String uri) throws RemoteException;
    public String delegatePermission(String fileName, String uri, String otherURI, String permission) throws RemoteException;
}