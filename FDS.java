import java.io.IOException;
import java.rmi.*;
import java.util.List;

public interface FDS extends Remote {
    // Declaring the method prototype
    public String read(String filename) throws IOException;
    public String create(String filename, String data) throws IOException;
    public String update(String filename, String data) throws RemoteException;
    public boolean restore(String filename) throws RemoteException;
    public List<String> getAllFiles(String path) throws RemoteException;
    public boolean delete(String filename) throws RemoteException;
}