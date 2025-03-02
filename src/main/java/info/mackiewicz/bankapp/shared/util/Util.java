package info.mackiewicz.bankapp.shared.util;

public class Util {

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
    }
}
