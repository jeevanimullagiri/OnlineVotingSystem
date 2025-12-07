package voting;

import java.sql.*;

public class DBConnection {
    // Change to your MySQL credentials if needed
    private static final String URL  = "jdbc:mysql://localhost:3306/online_voting?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Ramcharan@2006#";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception ignored) {}
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS voters (
                    voter_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    age INT,
                    address VARCHAR(255),
                    has_voted BOOLEAN DEFAULT FALSE
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS candidates (
                    candidate_id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    party VARCHAR(100),
                    votes INT DEFAULT 0
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS app_settings (
                    setting_key VARCHAR(100) PRIMARY KEY,
                    setting_value VARCHAR(255) NOT NULL
                )
            """);
            st.execute("""
                INSERT INTO app_settings (setting_key, setting_value)
                VALUES ('results_released','false')
                ON DUPLICATE KEY UPDATE setting_value = setting_value
            """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
