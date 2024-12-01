import Demo.PrinterPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.LocatorPrx;

public class Broker {
    private Communicator communicator;
    private String config;

    public Broker(String config) {
        this.config = config;
        this.communicator = Util.initialize(new String[]{}, config);
    }

    public String sendRequest(String cedula) throws Exception {
        try {
            // Attempt to select the server
            PrinterPrx printer = selectServer();
            return "Server selected successfully for cedula: " + cedula;
        } catch (Exception e) {
            // Handle the exception and find the object by identity
            LocatorPrx locator = communicator.getDefaultLocator();
            ObjectPrx base = communicator.stringToProxy("SimpleServer-1@server-1.ServerAdapter");
            PrinterPrx printer = PrinterPrx.checkedCast(base);
            if (printer == null) {
                return "No available servers";
            }
            return "Server found by identity for cedula: " + cedula;
        }
    }

    private PrinterPrx selectServer() throws Exception {
        // Server selection implementation
        ObjectPrx base = communicator.stringToProxy("SimpleServer-1@server-1.ServerAdapter");
        return PrinterPrx.checkedCast(base);
    }
}