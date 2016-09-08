package uk.gov.ea.datareturns.util;

/**
 * Extends the Spring stopwatch to provide additional convenience methods
 *
 * @author Sam Gardner-Dell
 *
 */
public class StopWatch extends org.springframework.util.StopWatch {
    /**
     * Create a new StopWatch for the given ID
     *
     * @param id the identifier for the stopwatch
     */
    public StopWatch(final String id) {
        super(id);
    }

    /**
     * Start a new task.
     *
     * This method automatically stops the current task if one is running before starting the new task
     *
     * @param taskName the name of the task to begin
     */
    public void startTask(final String taskName) {
        if (super.isRunning()) {
            super.stop();
        }
        super.start(taskName);
    }
}
