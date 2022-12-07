import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public class FDSQuery extends UnicastRemoteObject implements FDS
{
    public HashMap<String, Boolean> isDeleted;

    public int replicaFactor;

    // Default constructor to throw RemoteException
    // from its parent constructor
    FDSQuery() throws IOException {
        super();
        isDeleted = new HashMap<>();
        Properties prop = new Properties();
        prop.load(new FileInputStream("../resources/config.properties"));
        //Reading each property value
        this.replicaFactor = Integer.parseInt(prop.getProperty("REPLICA_FACTOR"));
    }

    @Override
    public String read(String filename) throws Exception{
        FutureTask readTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(isDeleted.containsKey(filename) && !isDeleted.get(filename)){
                    try {
                        return Files.readString(Path.of(filename));
                    } catch (IOException io){
                        io.printStackTrace();
                    }
                }
                return null;
            }
        });
        new Thread(readTask).start();
        return (String) readTask.get();
    }

    @Override
    public String create(String filename, String data) throws Exception {
        FutureTask createTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                try {
                    FileWriter myWriter = new FileWriter(filename);
                    isDeleted.put(filename, false);
                    myWriter.write(data);
                    myWriter.close();
                    System.out.println("Successfully created " + filename);
                    return filename;
                } catch (IOException io) {
                    io.printStackTrace();
                }

                return null;
            }
        });
        new Thread(createTask).start();
        return (String) createTask.get();
    }

    @Override
    public String update(String filename, String data) throws Exception {
        FutureTask updateTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(isDeleted.containsKey(filename) && !isDeleted.get(filename)){
                    try {
                        Files.write(Paths.get(filename), data.getBytes(), StandardOpenOption.APPEND);
                        return Files.readString(Path.of(filename));
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        new Thread(updateTask).start();
        return (String) updateTask.get();

    }

    @Override
    public boolean restore(String filename) throws Exception {
        FutureTask createTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                if(isDeleted.containsKey(filename)){
                    isDeleted.put(filename, false);
                    System.out.println("Successfully restored - " + filename);
                    return true;
                }
                return false;
            }
        });
        new Thread(createTask).start();
        return (boolean) createTask.get();
    }

    @Override
    public List<String> getAllFiles(String path) throws RemoteException {
        return null;
    }

    @Override
    public boolean delete(String filename) throws Exception {
        FutureTask deleteTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if(isDeleted.containsKey(filename)){
                    isDeleted.put(filename, true);
                    System.out.println("Successfully deleted - " + filename);
                    return true;
                }
                return false;
            }
        });
        new Thread(deleteTask).start();
        return (boolean) deleteTask.get();
    }
}