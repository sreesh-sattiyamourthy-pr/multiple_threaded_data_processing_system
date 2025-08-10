import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class RideSharingSystem {
    private static final Logger logger = Logger.getLogger(RideSharingSystem.class.getName());
    private static final BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();
    private static final List<String> results = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        int numWorkers = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);

        // Setup logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT [%4$s] %5$s %n");

        // Add ride requests to the queue
        for (int i = 1; i <= 10; i++) {
            taskQueue.add("RideRequest-" + i);
        }

        // Start worker threads
        for (int i = 0; i < numWorkers; i++) {
            executor.execute(new Worker(i));
        }

        // Shutdown executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warning("Executor did not terminate in time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Executor interrupted", e);
            executor.shutdownNow();
        }

        // Output results
        System.out.println("\nProcessed Results:");
        results.forEach(System.out::println);
    }

    static class Worker implements Runnable {
        private final int id;

        Worker(int id) {
            this.id = id;
        }

        public void run() {
            logger.info("Worker " + id + " started.");
            try {
                while (true) {
                    String task = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (task == null)
                        break;

                    // Simulate processing
                    Thread.sleep(500);

                    String result = "Worker " + id + " processed " + task;
                    results.add(result);
                    logger.info(result);
                }
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Worker " + id + " interrupted", e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Worker " + id + " error", e);
            } finally {
                logger.info("Worker " + id + " completed.");
            }
        }
    }
}
