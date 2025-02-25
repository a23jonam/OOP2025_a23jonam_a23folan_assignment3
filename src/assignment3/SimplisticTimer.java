package assignment3;
/**
 * A simplistic class to create timestamps at a millisecond granularity to
 * support debugging.
 */
public class SimplisticTimer {
    private final long startTime;
    private long previousLapTime;

    /**
     * Creates an object that stores two millisecond granularity timestamps. One
     * stores the time the class was instantiated, the other can be used to measure
     * shorter periods of elapsed time or "laps".
     */
    public SimplisticTimer() {
        this.startTime = System.currentTimeMillis();
        this.previousLapTime = startTime;
    }

    /**
     * Returns the time that has elapsed since the object was instantiated.
     * 
     * @return a long value representing the time elapsed since object creation.
     */
    public long elapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Creates and returns a lap time by storing the time the method was invoked and
     * returning the difference between this invocation and the previous.
     * 
     * @return a long value representing the time elapsed since the previous call to
     *         this method, or the elapsed time on the first invocation.
     */
    public long lapTime() {
        long now = System.currentTimeMillis();
        long lap = now - this.previousLapTime;
        this.previousLapTime = now;
        return lap;
    }

    /**
     * Clears the lap time, resetting it to the value of startTime.
     */
    public void resetLapTime() {
        this.previousLapTime = this.startTime;
    }
}