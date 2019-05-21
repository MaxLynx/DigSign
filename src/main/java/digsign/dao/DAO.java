package digsign.dao;


import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import digsign.signature.Signing;
import digsign.signature.User;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.google.gson.Gson;

import javax.jws.soap.SOAPBinding;

public class DAO {
    private static String USERDATA_SOURCE_FILENAME = "src\\main\\resources\\userdata.json";

    public DAO(){

    }

    public List<User> loadUsers(){

        List<User> users = new ArrayList<User>();

        try {
            FileReader reader = new FileReader(USERDATA_SOURCE_FILENAME);

            Type colType = new TypeToken<List<PrivateData>>(){}.getType();
            List<PrivateData> userdata = new Gson().fromJson(reader, colType);
            reader.close();
            for (PrivateData line :
                    userdata) {
                    User user = new User();
                    user.setUsername(line.getUsername());
                    user.setPassword((String) deserialize(line.getPassword()));
                    user.setKeyPair((KeyPair) deserialize(line.getKeys()));
                    users.add(user);


            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        return users;
    }

    public void saveUsers(List<User> users){
        List<PrivateData> privateData = new ArrayList<>();
        for(User user:
                users){

            privateData.add(new PrivateData(user.getUsername(),
                    serialize(user.getPassword()),
                    serialize(user.getKeyPair())));
        }
        try {
            Writer writer = new FileWriter(USERDATA_SOURCE_FILENAME);
            new Gson().toJson(privateData, writer);
            writer.flush();
            writer.close();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
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
