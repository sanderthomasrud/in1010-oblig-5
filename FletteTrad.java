import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class FletteTrad implements Runnable {

    private Monitor2 monitor;
    private CountDownLatch barriere;
    private volatile int antallFiler;

    public FletteTrad(Monitor2 monitor, CountDownLatch barriere, int antallFiler) {
        this.monitor = monitor;
        this.barriere = barriere;
        this.antallFiler = antallFiler;
    }

    @Override
    public void run() {
        System.out.println("Flettetrad starter...");

        try {
            if (antallFiler >= 1) {
                ArrayList<HashMap<String, Subsekvens>> toKart = monitor.hentUtToKart();
                System.out.println("Hentet ut to kart");
                HashMap<String, Subsekvens> kart = Monitor2.slaaSammen(toKart.get(0), toKart.get(1));
                monitor.settInnFlettet(kart);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        barriere.countDown();
    }
    
}
