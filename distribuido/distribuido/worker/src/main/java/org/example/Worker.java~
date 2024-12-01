package org.example;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.Util;
import Demo.PrinterPrx;
import Demo.WorkerPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Value;

import java.util.Random;

public class Worker {
    public static void main(String[] args) {
    try {
        Communicator communicator = Util.initialize(args);
        ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("WorkerAdapter", "tcp -p 9098");
        Random random = new Random();
        // Crear la implementación del Worker
        WorkerImpl workerImpl = new WorkerImpl("worker" + random.nextInt(1000));
        WorkerPrx workerProxy = (WorkerPrx) adapter.add(workerImpl, Util.stringToIdentity("worker" + random.nextInt(1000)));
        // Registrar el Worker con el Printer
        PrinterPrx printer = PrinterPrx.checkedCast(communicator.stringToProxy("printer:tcp -p 9099"));
        if (printer != null) {
            printer.registerWorker("worker1", (Value) workerProxy);
        }
        // Iniciar el adaptador y comunicar el worker
        adapter.activate();
        System.out.println("Worker is running...");
        // Bloquear el servidor hasta que se interrumpa
        communicator.waitForShutdown();
    } catch (Exception e) {
        e.printStackTrace();
    }

    }
}