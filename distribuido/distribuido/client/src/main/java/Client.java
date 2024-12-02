import Demo.NotificationPrx;
import Demo.PrinterPrx;
import Demo.Response;
import com.zeroc.Ice.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Exception;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.commons.math3.primes.Primes.isPrime;

public class Client implements Demo.Notification {

    private Communicator communicator;
    private  PrinterPrx server;
    private ExecutorService executorService;
    private static final int MAX_QUERIES_PER_BATCH = 100000;
    public static void main(String[] args) {
        Client client = new Client();
        client.run(args);

    }

    private void run(String[] args) {
        communicator = null;
        try {
            communicator = Util.initialize(args, "config.client");
            ObjectPrx base = communicator.stringToProxy("SimpleServer:default -p 9099");
            server = PrinterPrx.checkedCast(base);

            if (server == null) throw new Error("Invalid proxy");
            String hostname = InetAddress.getLocalHost().getHostName();
            String clientEndpoints = "tcp -h " + hostname + " -p 9099";
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("ClientAdapter", clientEndpoints);
            adapter.add(this, Util.stringToIdentity("client10"));
            adapter.activate();
            NotificationPrx clientProxy = NotificationPrx.uncheckedCast(adapter.createProxy(Util.stringToIdentity("client10")));
            server.registerObserver("client1", clientProxy);
            System.out.println("Cliente registrado y esperando instrucciones...");
            executorService = Executors.newFixedThreadPool(10);
            interactWithServer();
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

    @Override
    public void notifyLoadFile(String filePath, Current current) {
        System.out.println("Notificación recibida: " + filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> cedulasBatch = new ArrayList<>();
            String cedula;
            while ((cedula = reader.readLine()) != null) {
                cedulasBatch.add(cedula);

                // Si el número de cédulas llega a 100,000, enviamos ese batch
                if (cedulasBatch.size() >= MAX_QUERIES_PER_BATCH) {
                    sendBatchToExecutor(cedulasBatch);
                    cedulasBatch.clear();  // Limpiar el batch
                }
            }

            // Si hay cédulas restantes, procesarlas
            if (!cedulasBatch.isEmpty()) {
                sendBatchToExecutor(cedulasBatch);
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void interactWithServer() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Escribe 'exit' para salir o espera instrucciones del servidor...");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
        }
    }

    private void sendBatchToExecutor(List<String> cedulasBatch) {
        // Enviar un batch de cédulas al ThreadPool para que las procese en paralelo
        executorService.submit(() -> {
            for (String cedula : cedulasBatch) {
                processCedula(cedula);
            }
        });
    }

    private void processCedula(String cedula) {
        try {
            System.out.println("Procesando cédula: " + cedula);
            Response response = server.printString(cedula);
            System.out.println("Respuesta del servidor: " + response.value);
            System.out.println("Tiempo de espera: " + response.responseTime + "ms");
            logToClientAuditFile(cedula, response.value, isPrime(Integer.parseInt(cedula)), response.responseTime);
        } catch (Exception e) {
            System.out.println("Error procesando la cédula " + cedula + ": " + e.getMessage());
        }
    }
}