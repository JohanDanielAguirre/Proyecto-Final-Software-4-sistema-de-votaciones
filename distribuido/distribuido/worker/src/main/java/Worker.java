import Demo.PrinterPrx;
import Demo.WorkerPrx;
import com.zeroc.Ice.*;

import java.lang.Exception;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.net.InetAddress.getLocalHost;

public class Worker implements Demo.Worker {

    private String workerId;
    private PrinterPrx printer;

    public Worker(String workerId) {
        this.workerId = workerId;
    }
    public static void main(String[] args) {
    try {
        Communicator communicator = Util.initialize(args, "config.worker");
        ObjectPrx base = communicator.stringToProxy("SimpleServer:default -p 9099");
        PrinterPrx server = PrinterPrx.checkedCast(base);
        if (server == null) throw new Error("Invalid proxy");
        String hostname = InetAddress.getLocalHost().getHostName();
        String clientEndpoints = "tcp -h " + hostname + " -p 9099";
        // Create and activate the object adapter for Worker
        ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("WorkerAdapter", clientEndpoints);
        Worker worker = new Worker("worker1");
        adapter.add(worker, Util.stringToIdentity("worker1"));
        adapter.activate();

        WorkerPrx workerProxy = WorkerPrx.uncheckedCast(adapter.createProxy(Util.stringToIdentity("worker1")));
        server.registerWorker("worker1", workerProxy);

        System.out.println("Worker registrado: " + workerProxy);
        communicator.waitForShutdown();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @Override
    public void processTask(String taskData, com.zeroc.Ice.Current current) {
        System.out.println("Worker " + workerId + " processing task: " + taskData);
        String[] cedulas = taskData.split(",");
        StringBuilder result = new StringBuilder();

        for (String cedula : cedulas) {
            System.out.println("Cédula: " + cedula);
            String queryResult = executeQuery(cedula);
            result.append("Cédula: ").append(cedula).append(" - ").append(queryResult).append("\n");
            boolean isPrime = isPrime(Integer.parseInt(cedula)); // Verificamos si la cédula es primo
            result.append("Cédula: ").append(cedula).append(" - ").append(queryResult)
                    .append(isPrime ? " - Es primo\n" : " - No es primo\n");
            System.out.println("Cédula: " + cedula + " - " + queryResult + (isPrime ? " - Es primo" : " - No es primo"));
        }

        if (printer != null) {
            System.out.println("Entra a enviar el resultado");
            printer.receivePartialResult(current.id.name, result.toString(), null);
        }

    }

    @Override
    public void shutdown(com.zeroc.Ice.Current current) {
        System.out.println("Shutting down...");
        current.adapter.getCommunicator().shutdown();
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
            System.out.println("Entra a formar la query");
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Entra a uniendo la query");
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

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}