import com.zeroc.Ice.Current;
import Demo.SubjectPrx;
import Demo.ObserverPrx;

public class ObserverI implements Demo.Observer {
    @Override
    public void _notify(String message, String eventType, Current current) {
        System.out.println("Notificacion del server: [" + eventType + "] " + message);
    }
}
