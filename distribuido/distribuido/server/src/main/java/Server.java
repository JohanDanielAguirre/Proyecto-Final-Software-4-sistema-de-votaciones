import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import Demo.SubjectPrx;

public class Server
{

    public static void main(String[] args) {
        int status = 0;
        Communicator communicator = null;
        try {
            communicator = Util.initialize(args, "config.server");
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("ServerAdapter", "default -p 9099");
            Object object = new PrinterI();
            adapter.add((com.zeroc.Ice.Object) object, Util.stringToIdentity("SimpleServer"));
			SubjectPrx subject = SubjectPrx.checkedCast(
            communicator.propertyToProxy("Subject.Proxy"));
            adapter.add((com.zeroc.Ice.Object) subject, Util.stringToIdentity("subject"));
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