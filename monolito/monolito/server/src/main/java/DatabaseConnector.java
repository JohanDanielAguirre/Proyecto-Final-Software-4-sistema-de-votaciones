import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL = "jdbc:postgresql://xhgrid2:5432/votaciones"; // Cambia la IP si es necesario
    private static final String USER = "postgres"; // Usuario de la base de datos
    private static final String PASSWORD = "postgres"; // Contraseña del usuario

    public Connection connect() throws SQLException {
        //System.out.println("Entra en database conector");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            //System.out.println("Conexión exitosa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public ResultSet executeQuery(String query) throws SQLException {
        //System.out.println("entra en executequery");
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            return stmt.executeQuery();
        }
    }
}

