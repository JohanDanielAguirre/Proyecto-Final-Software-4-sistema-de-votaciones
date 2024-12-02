import Demo.SubjectPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Server {

    public static void main(String[] args) {
        int status = 0;
        Communicator communicator = null;
        try {
            communicator = Util.initialize(args, "config.server");
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("ServerAdapter", "default -p 9099");

            // Crear e implementar SubjectImpl
            SubjectI subjectImpl = new SubjectI();
            SubjectPrx subjectPrx = SubjectPrx.uncheckedCast(
                    adapter.add(subjectImpl, Util.stringToIdentity("subject"))
            );
            System.out.println("SubjectImpl creado y añadido con éxito.");

            // Crear e implementar PrinterI
            PrinterI printerImpl = new PrinterI(subjectPrx);
            adapter.add((com.zeroc.Ice.Object) printerImpl, Util.stringToIdentity("SimpleServer"));
            System.out.println("PrinterI creado y añadido con éxito.");

            adapter.activate();
            System.out.println("Server started...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
        }
        if (communicator != null) {
            communicator.destroy();
        }
        System.exit(status);
    }
}