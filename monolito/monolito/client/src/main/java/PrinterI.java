import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class PrinterI {

    public Response printString(String message) {
        long startTime = System.currentTimeMillis();
        String response = executeQuery(message);
        long endTime = System.currentTimeMillis();
        return new Response(endTime - startTime, response);
    }

    private String executeQuery(String documento) {
        String query = """
                SELECT c.nombre AS ciudadano_nombre,c.apellido AS ciudadano_apellido, c.documento AS ciudadano_documento,d.nombre AS departamento,
               m.nombre AS municipio,pv.direccion AS puesto_direccion, mv.id AS mesa
               FROM ciudadano c
               JOIN mesa_votacion mv ON c.mesa_id = mv.id
               JOIN puesto_votacion pv ON mv.puesto_id = pv.id
               JOIN municipio m ON pv.municipio_id = m.id
               JOIN departamento d ON m.departamento_id = d.id
               WHERE c.documento = ?
        """;

        StringBuilder result = new StringBuilder();
        try (Connection connection = new DatabaseConnector().connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result.append("El ciudadano ")
                            .append(rs.getString("ciudadano_nombre")).append(" ")
                            .append(rs.getString("ciudadano_apellido"))
                            .append(" identificado con el documento ")
                            .append(rs.getString("ciudadano_documento"))
                            .append(" debe votar en ")
                            .append(rs.getString("departamento")).append(", ")
                            .append(rs.getString("municipio")).append(", ")
                            .append("En la direccion ")
                            .append(rs.getString("puesto_direccion"))
                            .append(". En la mesa ")
                            .append(rs.getInt("mesa")).append(".");
                } else {
                    result.append("No se encontró ningún ciudadano con el documento ").append(documento).append(".");
                }
            }
        } catch (SQLException e) {
            result.append("Error ejecutando consulta: ").append(e.getMessage());
        }
        return result.toString();
    }
}