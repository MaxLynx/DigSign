package digsign.dao;


import digsign.signature.Signing;
import digsign.signature.User;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class DAO {
    private static String USERDATA_SOURCE_FILENAME = "src\\main\\resources\\userdata.txt";

    public DAO(){

    }

    public List<User> loadUsers(){
        List<User> users = new ArrayList<User>();

        String[] userdata = getFileAsString(USERDATA_SOURCE_FILENAME).split("\t");
        for (String line:
             userdata) {
                String[] fields = line.split(" ");
                if(fields.length == 3) {
                    User user = new User();
                    user.setUsername(fields[0]);
                    user.setPassword((String) deserialize(fields[1]));
                    user.setKeyPair((KeyPair) deserialize(fields[2]));
                    users.add(user);
                }

        }

        return users;
    }

    public void saveUsers(List<User> users){
        String userdata = "";
        for(User user:
                users){
            userdata += user.getUsername() + " "
                    + serialize(user.getPassword()) + " "
                    + serialize(user.getKeyPair()) + "\t";
        }
        getStringAsFile(userdata, USERDATA_SOURCE_FILENAME);
    }

    private static String serialize(Object object){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String result = new BASE64Encoder().encode(byteArrayOutputStream.toByteArray());
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return result;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

    }

    private static Object deserialize(String serializedObject){
        try{
            byte[] bytes = new BASE64Decoder().decodeBuffer(serializedObject);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private static String getFileAsString(String filename) {

        String result = "";

        Path path = Paths.get(filename);

        try (InputStream in = new BufferedInputStream(
                Files.newInputStream(path))) {
            result = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException x) {
            System.err.println("Problem with file " + x.getMessage());
        }

        return result;
    }

    private static Path getStringAsFile(String string, String filename) {

        Path path = Paths.get(filename);

        byte data[] = string.getBytes();

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, WRITE, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println("Problem with file " + x.getMessage());
        }

        return path;
    }

    public static void main(String[] args) throws Exception {
        User testUser = new User("John Doe", "0000");
        KeyPair keyPair = testUser.getKeyPair();
        Signature signature = testUser.getSignature();

        System.out.println(deserialize(serialize(keyPair)) instanceof KeyPair);
    }

}
