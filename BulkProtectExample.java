import com.protegrity.ap.java.*;
import java.util.Arrays;

public class BulkProtectExample {
    public static void main(String[] args) {
        try {
            Protector protector = Protector.getProtector();
            String userName = "superuser";
            String dataElement = "mask";
            String[] data = {"5555555555554444", "378282246310005", "4111111111111111"};
            
            SessionObject session = protector.createSession(userName);
            System.out.println("Original Data: " + Arrays.toString(data));

            // Protect bulk
            String[] protectedData = new String[data.length];
            protector.protect(session, dataElement, data, protectedData);
            System.out.println("Protected Data: " + Arrays.toString(protectedData));
            
            // Unprotect bulk
            String[] unprotectedData = new String[data.length];
            protector.unprotect(session, dataElement, protectedData, unprotectedData);
            System.out.println("Unprotected Data: " + Arrays.toString(unprotectedData));
            
        } catch (ProtectorException e) {
            e.printStackTrace();
        }
    }
}
