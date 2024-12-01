
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException {
        Dao dao = new Dao();

        // Create the database tables
        dao.createTable(); // Uncomment if table creation is needed

        // Insert username and password records into the database
        dao.insertDefaultUsersIfNotExist();
        
        //Initialize login panel
        new Login();
    }
}
