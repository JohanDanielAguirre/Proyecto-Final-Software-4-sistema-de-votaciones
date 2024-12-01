import Demo.PrinterPrx;
import Demo.Response;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Client
{
    public static void main(String[] args) {
        Communicator communicator = null;
        try {
            communicator = Util.initialize(args, "config.client");
            ObjectPrx base = communicator.stringToProxy("SimpleServer:default -p 9099");
            PrinterPrx server = PrinterPrx.checkedCast(base);
            if (server == null) throw new Error("Invalid proxy");

            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Ingresa la ruta del archivo con las cédulas:");
                String filePath = scanner.nextLine(); // Obtener la ruta del archivo desde el usuario
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String cedula;
                    while ((cedula = reader.readLine()) != null) {
                        // Enviar cada cédula al servidor
                        System.out.println("Consultando cédula: " + cedula);
                        Response response = server.printString(cedula);
                        System.out.println("Respuesta del servidor: " + response.value);
                        System.out.println("Tiempo de espera: " + response.responseTime + "ms");
                    }
                } catch (IOException e) {
                    System.out.println("Error al leer el archivo: " + e.getMessage());
                }
                if (filePath.equals("exit")) {
                    break;
                }
            }
            if (communicator != null) {
                communicator.destroy();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logToClientAuditFile(String cedula, String mesa, boolean isPrime, long responseTime) {
        try (FileWriter writer = new FileWriter("client_audit_log.csv", true)) {
            writer.write(cedula + "," + mesa + "," + (isPrime ? 1 : 0) + "," + responseTime + "\n");
        } catch (IOException e) {
            System.out.println("Error escribiendo en el archivo de auditoría del cliente: " + e.getMessage());
        }
    }
}