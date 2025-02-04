package info.mackiewicz.bankapp.utils;

public class Util {

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
    }
}
