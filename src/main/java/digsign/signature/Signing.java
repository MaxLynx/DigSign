package digsign.signature;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;


public class Signing {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.genKeyPair();

        byte[] data = "test".getBytes("UTF8");

        Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        System.out.println("Signature:" + new BASE64Encoder().encode(signatureBytes));

        sig.initVerify(keyPair.getPublic());
        sig.update(data);

        System.out.println(sig.verify(signatureBytes));
    }

    public static String createSignature(User user, String text){
        try {
            byte[] data = text.getBytes("UTF8");
            Signature sig = user.getSignature();
            sig.initSign(user.getKeyPair().getPrivate());
            sig.update(data);
            byte[] signatureBytes = sig.sign();
            return new BASE64Encoder().encode(signatureBytes);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static boolean verifySignature(User user, String signature, String text){
        try {
            byte[] signatureBytes = new BASE64Decoder().decodeBuffer(signature);
            byte[] data = text.getBytes("UTF8");
            Signature sig = user.getSignature();
            sig.initVerify(user.getKeyPair().getPublic());
            sig.update(data);
            return sig.verify(signatureBytes);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
