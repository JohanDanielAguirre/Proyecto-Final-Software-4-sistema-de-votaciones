import Demo.PrinterPrx;
import Demo.WorkerPrx;
import com.zeroc.Ice.*;

import java.lang.Exception;
import java.lang.Object;

public class Worker {
    public static void main(String[] args) {
    try {
        Communicator communicator = Util.initialize(args, "config.worker");
        ObjectPrx base = communicator.stringToProxy("SimpleServer:default -p 9099");
        PrinterPrx server = PrinterPrx.checkedCast(base);
        if (server == null) throw new Error("Invalid proxy");
        WorkerPrx worker = WorkerPrx.uncheckedCast(communicator.stringToProxy("Worker:default -p 9099"));
        if (worker == null) throw new Error("Invalid proxy");
        communicator.waitForShutdown();
        server.registerWorker("worker1",worker);
        System.out.println("Worker registrado");
        communicator.waitForShutdown();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}