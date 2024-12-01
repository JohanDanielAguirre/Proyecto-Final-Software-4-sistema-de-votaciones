import Demo.Response;
import com.zeroc.Ice.Current;

public class PrinterI implements Demo.Printer {

    @Override
    public Response printString(String message, Current __current) {
        long startTime = System.currentTimeMillis();
        String response = executeQuery(message);
        long endTime = System.currentTimeMillis();
        return new Response(endTime - startTime, response);
    }

    private String executeQuery(String documento) {
        // Simular una respuesta en lugar de realizar una consulta a la base de datos
        return "Simulación: El ciudadano con documento " + documento + " debe votar en el departamento simulado, municipio simulado, en la dirección simulada. En la mesa simulada.";
    }
}