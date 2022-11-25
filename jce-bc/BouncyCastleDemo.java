import java.security.Provider;
import java.security.Security;

public class BouncyCastleDemo {
    public static void main(String[] args) {
        String providerName = "BC";
        Provider provider = Security.getProvider(providerName);
        if (provider == null) {
            System.out.println(providerName + " provider not installed");
            return;
        }

        System.out.println("Provider Name :"+ provider.getName());
        System.out.println("Provider Version :"+ provider.getVersion());
        System.out.println("Provider Info:" + provider.getInfo());
    }
}