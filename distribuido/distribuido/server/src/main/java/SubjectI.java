import java.util.List;
import java.util.ArrayList;
import Demo.SubjectPrx;
import Demo.ObserverPrx;
import com.zeroc.Ice.Current;
public class SubjectI implements Demo.Subject {

    private final List<ObserverPrx> observers = new ArrayList<>();

    @Override
    public void suscribirse(ObserverPrx observer, Current current) {
        observers.add(observer);
        System.out.println("Observador suscrito: " + observer);
    }

    @Override
    public void desuscribirse(ObserverPrx observer, Current current) {
        observers.remove(observer);
        System.out.println("Observador eliminado: " + observer);
    }

    @Override
    public void notifyObservers(String message, String eventType, Current current) {
        System.out.println("Notificando a observadores: " + eventType);
        for (ObserverPrx observer : observers) {
            try {
                observer._notify(message, eventType);
            } catch (Exception e) {
			System.err.println("Error: no se pudo notificar al observador: " + e.getMessage());
                observers.remove(observer);
            }
        }
    }
}
