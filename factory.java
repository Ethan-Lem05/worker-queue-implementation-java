import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ExecutionException;
import java.util.UUID;
import java.util.HashMap;

public class factory {

    private int numWorkers;
    private ExecutorService executor;
    private HashMap<UUID, Future<?>> futures;

    public factory(int _numWorkers) {
        numWorkers = _numWorkers;
        executor = Executors.newFixedThreadPool(numWorkers);
        futures = new HashMap<UUID, Future<?>>();
    }

    public UUID addTask(FutureTask<?> task) {

        // create a UUID for the task
        UUID taskID = UUID.randomUUID();

        // submit the task to the executor
        try {
            Future<?> future = executor.submit(task);
            // add the future to the hashmap
            futures.put(taskID, future);
        } catch (RejectedExecutionException e) {
            System.out.println("Task submission rejected: " + e.getMessage());
        }

        return taskID;
    }

    public HashMap<UUID, Future<?>> getCompletedFutures() {

        // create a hashmap for all completed futures
        HashMap<UUID, Future<?>> completed = new HashMap<UUID, Future<?>>();

        // iterate through all futures
        for (UUID k : futures.keySet()) {

            // get the future
            Future<?> f = futures.get(k);

            // if the future is null or not done, continue
            if (f == null || !f.isDone()) {
                continue;
            }

            // get the value of the future and put it in the completed hashmap
            try {
                Object value = f.get();
                completed.put(k, f);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return completed;
    }

    // shutdown the executor
    public void shutdown() {
        executor.shutdown();
    }

}