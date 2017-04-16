package me;

import java.util.concurrent.*;
import java.util.function.BiFunction;

public class Main {

    static class Octopus {
        public Octopus() {}
        public String getName() {
            return "Cupis";
        }
    }

    static final Octopus octopusItself = new Octopus();
    static volatile boolean acquired = false;

    class OctopusNotAcquiredException extends RuntimeException {
        public OctopusNotAcquiredException(String reason) {
            super(reason);
        }
    }

    private static ScheduledExecutorService async = Executors.newSingleThreadScheduledExecutor();

    private CompletableFuture tryAcquireOctopus(final CompletableFuture promise, final int attempts) {
        final int ACQUIRE_RECHECK_TIME = 1;
        if (acquired) {
            System.out.println("Octopus already acquired by somebody else, waiting");
            if (attempts > 0)
                async.schedule(() -> tryAcquireOctopus(promise, attempts - 1), ACQUIRE_RECHECK_TIME, TimeUnit.SECONDS);
            else
                promise.completeExceptionally(new OctopusNotAcquiredException("Fed up with waiting!"));
        }
        else {
            acquired = true;
            promise.complete(octopusItself);
        }
        return promise;
    }

    private CompletionStage acquireOctopus() {
        final int ATTEMPTS = 5;
        return tryAcquireOctopus(new CompletableFuture<>(), ATTEMPTS);
    }

    private CompletionStage fetchWithTags() {
        CompletableFuture promise = new CompletableFuture();
        async.schedule(()->promise.complete(null), 1000, TimeUnit.MILLISECONDS);
        return promise;
    }

    public CompletableFuture doMergeWorkflow(String sprintNo, String featureNo) {
        return CompletableFuture
            .runAsync(()->System.out.println("Starting merge workflow, trying to acquire octopus..."))
            .thenComposeAsync(f->acquireOctopus())
            .thenAcceptAsync(o -> System.out.println("Octopus " + ((Octopus)o).getName() + " successfully acquired"))
            .thenComposeAsync(f->fetchWithTags())
            .thenRunAsync(()->System.out.println("Fetched from origin successfully"))
            .thenRunAsync(()->System.out.println("Merge workflow completed successfully"))
            .handleAsync((obj, ex)-> {
                if (ex != null)
                    System.out.println("Error in merge workflow: " +  ((Throwable)ex).getMessage());
                return obj;
            });
    }

    public static CompletableFuture testOctopusIsFree() {
        System.out.println("--- Octopus is free ---");
        acquired = false;
        return (new Main()).doMergeWorkflow("27", "4299")
            .thenRun(()-> System.out.println("Completed with free octopus\n\n"));
    }

    public static CompletableFuture testOctopusIsAcquiredThenFreed() {
        System.out.println("--- Octopus is acquired then freed ---");
        acquired = true;
        async.schedule(()->acquired = false, 3, TimeUnit.SECONDS);
        return (new Main()).doMergeWorkflow("27", "4299")
                .thenRun(()-> System.out.println("Completed with octopus acquired then freed \n\n"));
    }

    public static CompletableFuture testOctopusIsAcquired() {
        System.out.println("--- Octopus is acquired ---");
        acquired = true;
        return (new Main()).doMergeWorkflow("27", "4299")
                .handle((obj,ex)-> {System.out.println("Completed with free octopus\n\n"); return null;});
    }

    public static void main(String[] args) {
        testOctopusIsFree()
            .thenComposeAsync(f -> testOctopusIsAcquiredThenFreed())
            .thenComposeAsync(f -> testOctopusIsAcquired())
            .handleAsync((obj,ex)->{
                async.shutdown();
                return null;
            });
    }
}
