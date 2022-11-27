import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.*;
import java.rmi.server.*;
import java.util.List;


public class FDSQuery extends UnicastRemoteObject implements FDS
{
    // Default constructor to throw RemoteException
    // from its parent constructor
    FDSQuery() throws RemoteException
    {
        super();
    }

    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public String read(String filename) throws RemoteException{
        try {
            Files.readString(Path.of(filename));
        } catch (IOException io){
            io.printStackTrace();
        }
        return null;
    }

    /**
     * @param filename
     * @param data
     * @return
     * @throws RemoteException
     */
    @Override
    public String create(String filename, String data) throws RemoteException {
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            return filename;
        } catch (IOException io) {
            io.printStackTrace();
        }

        return null;
    }

    /**
     * @param filename
     * @param data
     * @return
     * @throws RemoteException
     */
    @Override
    public String update(String filename, String data) throws RemoteException {
        try {
            Files.write(Paths.get("myfile.txt"), "the text".getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean restore(String filename) throws RemoteException {
        return false;
    }

    /**
     * @return
     * @throws RemoteException
     */
    @Override
    public List<String> getAllFiles(String path) throws RemoteException {
        return null;
    }

    /**
     * @param filename
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean delete(String filename) throws RemoteException {
        return false;
    }
}