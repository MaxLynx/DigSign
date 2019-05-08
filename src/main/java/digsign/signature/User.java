package digsign.signature;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

public class User {
    private String username;
    private String password;

    private KeyPair keyPair;
    private Signature signature;

    public User(String username, String password){
        this.username = username;
        this.password = password;

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            this.keyPair = kpg.genKeyPair();
            this.signature = Signature.getInstance("MD5WithRSA");
        }
        catch(NoSuchAlgorithmException ex){
            System.out.println(ex.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }
}
