package main

import (
    "fmt"
    "log"
    "os"
    "sync"
    "time"
)

func main() {
    numWorkers := 4
    numTasks := 10

    tasks := make(chan string, numTasks)
    var results []string
    var mu sync.Mutex
    var wg sync.WaitGroup

    // Setup logging to file and console
    logFile, err := os.OpenFile("rideshare.log", os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
    if err != nil {
        log.Fatalf("Failed to open log file: %v", err)
    }
    defer logFile.Close()
    log.SetOutput(logFile)

    // Add tasks
    for i := 1; i <= numTasks; i++ {
        tasks <- fmt.Sprintf("RideRequest-%d", i)
    }
    close(tasks)

    // Start workers
    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func(id int) {
            defer wg.Done()
            log.Printf("Worker %d started.", id)
            for task := range tasks {
                time.Sleep(500 * time.Millisecond) // Simulate processing
                result := fmt.Sprintf("Worker %d processed %s", id, task)

                mu.Lock()
                results = append(results, result)
                mu.Unlock()

                log.Println(result)
            }
            log.Printf("Worker %d completed.", id)
        }(i)
    }

    wg.Wait()

    // Output results
    fmt.Println("\nProcessed Results:")
    for _, r := range results {
        fmt.Println(r)
    }
}
