import cn.hutool.crypto.digest.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verify: " + BCrypt.checkpw(password, hash));
    }
}
