package digsign.signature;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;


public class Signing {

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
