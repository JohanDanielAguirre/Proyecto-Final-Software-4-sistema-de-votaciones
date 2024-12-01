module Demo
{
    // Interfaz del Worker
    interface Worker
    {
        void processTask(string s);
        void shutdown();
    }

    // Clase de Respuesta
    class Response {
        long responseTime;
        string value;
    }

    // Interfaz del Printer
    interface Printer
    {
        Response printString(string s);
        void registerWorker(string workerId, Worker workerProxy);  // Se cambió WorkerPrx a Worker
        void assignTaskToWorker(string taskId, string taskData);
        void receivePartialResult(string taskId, string partialResult);
        void notifyTaskCompletion(string taskId);
    }
}
