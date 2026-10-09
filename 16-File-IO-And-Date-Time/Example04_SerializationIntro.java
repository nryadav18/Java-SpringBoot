/*
 * Example04_SerializationIntro.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Java serialization: turning an OBJECT into BYTES (to save in a file or
 *   send over a network) and back again (deserialization).
 *   - implements Serializable (a "marker" interface - no methods)
 *   - serialVersionUID (a version number for the class)
 *   - transient fields are NOT saved (e.g. passwords)
 *   - ObjectOutputStream / ObjectInputStream
 *
 *   And WHY modern applications (and Spring REST APIs) use JSON instead.
 *
 * HOW TO RUN (from this folder):
 *   java Example04_SerializationIntro.java
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class Example04_SerializationIntro {

    // Serializable has NO methods. It is just a "permission slip" saying
    // "objects of this class may be turned into bytes".
    static class UserAccount implements Serializable {
        // Version of this class's "shape". If you change the fields later and an old
        // file is read, a mismatched number causes InvalidClassException.
        private static final long serialVersionUID = 1L;

        private final String username;
        private final String email;
        private final int loyaltyPoints;
        // 'transient' = do NOT serialize this field (sensitive or not needed)
        private final transient String password;

        UserAccount(String username, String email, int loyaltyPoints, String password) {
            this.username = username;
            this.email = email;
            this.loyaltyPoints = loyaltyPoints;
            this.password = password;
        }

        @Override
        public String toString() {
            return "UserAccount{username=" + username + ", email=" + email
                    + ", loyaltyPoints=" + loyaltyPoints + ", password=" + password + "}";
        }

        // A hand-written JSON representation - in Spring, the Jackson library
        // does this AUTOMATICALLY for every object you return from a controller.
        String toJson() {
            return """
                    {"username":"%s","email":"%s","loyaltyPoints":%d}""".formatted(username, email, loyaltyPoints);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path file = Path.of("output", "user.ser");
        Files.createDirectories(file.getParent());

        UserAccount original = new UserAccount("asha_k", "asha@shop.com", 1200, "S3cret!");
        System.out.println("Original     : " + original);

        // ---------- Serialize: object -> bytes -> file ----------
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(original);
        }
        System.out.println("Saved to     : output/user.ser (" + Files.size(file) + " bytes, binary - not human readable)");

        // ---------- Deserialize: file -> bytes -> object ----------
        UserAccount loaded;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            loaded = (UserAccount) in.readObject();   // returns Object -> must cast
        }
        System.out.println("Loaded       : " + loaded);
        System.out.println("Same object? : " + (original == loaded) + " (a NEW object was created)");
        System.out.println("Password lost because it was transient -> null");

        // ---------- The modern alternative: JSON ----------
        String json = original.toJson();
        Path jsonFile = Path.of("output", "user.json");
        Files.writeString(jsonFile, json);
        System.out.println("As JSON      : " + json);
        System.out.println("JSON is readable by humans AND by JavaScript, Python, mobile apps...");
    }
}

/* Output:
Original     : UserAccount{username=asha_k, email=asha@shop.com, loyaltyPoints=1200, password=S3cret!}
Saved to     : output/user.ser (151 bytes, binary - not human readable)
Loaded       : UserAccount{username=asha_k, email=asha@shop.com, loyaltyPoints=1200, password=null}
Same object? : false (a NEW object was created)
Password lost because it was transient -> null
As JSON      : {"username":"asha_k","email":"asha@shop.com","loyaltyPoints":1200}
JSON is readable by humans AND by JavaScript, Python, mobile apps...
*/
