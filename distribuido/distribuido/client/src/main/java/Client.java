import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

public class Client {
    public static void main(String[] args) {
        Communicator communicator = null;
        try {
            communicator = Util.initialize(args, "config.client");
            Broker broker = new Broker("config.client");

            // Enviar una solicitud de prueba con una cédula fija
            String testCedula = "123456789";
            System.out.println("Consultando cédula de prueba: " + testCedula);
            String response = broker.sendRequest(testCedula);
            System.out.println("Respuesta del servidor: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (communicator != null) {
                communicator.destroy();
            }
        }
    }
}