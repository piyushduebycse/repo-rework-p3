import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDb {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/revworkforce_db?serverTimezone=UTC";
        String user = "root";
        String password = "root";
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM employees")) {
            
            boolean hasRows = false;
            while(rs.next()) {
                hasRows = true;
                System.out.println("User found: " + rs.getString("email") + " Role: " + rs.getString("role"));
            }
            if (!hasRows) {
                System.out.println("NO USERS FOUND IN DATABASE");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
