package space.itoncek.trailcompass.app.utils;

public class RunnableUtils {
    public static void runOnBackgroundThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
    }
}
