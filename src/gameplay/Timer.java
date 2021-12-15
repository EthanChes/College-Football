package gameplay;
public class Timer {
    public static double getTime() { // gets time in seconds, converting from nanoseconds initially
        return (double) System.nanoTime() / (double) 1000000000L;
    }
}
