
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Client
{
    public static void main(String[] args) {
        PrinterI printer = new PrinterI();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Ingresa la ruta del archivo con las cédulas o escribe 'exit' para salir:");
            String filePath = scanner.nextLine();

            if (filePath.equalsIgnoreCase("exit")) {
                break;
            }
            long startTimegen = System.currentTimeMillis();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String cedula;
                while ((cedula = reader.readLine()) != null) {
                    System.out.println("Consultando cédula: " + cedula);
                    Response response = printer.printString(cedula);
                    System.out.println("Respuesta: " + response.value);
                    System.out.println("Tiempo de respuesta: " + response.responseTime + "ms");
                }
                long endTimegen = System.currentTimeMillis();
                System.out.println("Tiempo total de ejecución: " + (endTimegen - startTimegen) + "ms");
            } catch (IOException e) {
                System.out.println("Error al leer el archivo: " + e.getMessage());
            }
        }

        System.out.println("Aplicación finalizada.");
    }
}