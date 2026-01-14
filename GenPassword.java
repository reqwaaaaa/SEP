// 在项目根目录运行: java -cp sep-common/target/classes;%USERPROFILE%/.m2/repository/cn/hutool/hutool-all/5.8.23/hutool-all-5.8.23.jar GenPassword.java
import cn.hutool.crypto.digest.BCrypt;

public class GenPassword {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password);
        System.out.println("Generated hash for 'admin123': " + hash);
        System.out.println("Verify: " + BCrypt.checkpw(password, hash));
    }
}
