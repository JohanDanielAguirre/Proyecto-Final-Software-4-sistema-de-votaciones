import Demo.Response;
import com.zeroc.Ice.Current;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrinterI implements Demo.Printer {

    @Override
    public Response printString(String message, Current __current) {
        long startTime = System.currentTimeMillis();
        String response = executeQuery(message);
        long endTime = System.currentTimeMillis();
        return new Response(endTime - startTime, response);
    }

    private String executeQuery(String documento) {
        String query = """
                SELECT ciudadano_nombre, ciudadano_apellido, ciudadano_documento, departamento, municipio, puesto_direccion, mesa
                FROM ciudadano
                JOIN votacion ON ciudadano.ciudadano_id = votacion.ciudadano_id
                JOIN puesto ON votacion.puesto_id = puesto.puesto_id
                JOIN mesa ON votacion.mesa_id = mesa.mesa_id
                WHERE ciudadano_documento = ?
        """;

        StringBuilder result = new StringBuilder();
        try (Connection connection = new DatabaseConnector().connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            //System.out.println("Entra a formar la query");
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                //System.out.println("Entra a uniendo la query");
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
        System.out.println("query result: " + result.toString());
        return result.toString();
    }

}