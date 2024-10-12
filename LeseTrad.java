import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable{

    private final String FILNAVN;
    private Monitor2 monitor;
    private CountDownLatch barriere;

        public LeseTrad(String filnavn, Monitor2 monitor, CountDownLatch barriere) {
            this.monitor = monitor;
            this.FILNAVN = filnavn;
            this.barriere = barriere;
        }

    @Override
    public void run() {
        System.out.println("Lesetrad starter...");
        HashMap<String, Subsekvens> kart;

        try {
            kart = Monitor2.lesFraFil(FILNAVN);
            monitor.settInn(kart);
            System.out.println("Satte inn kart!");
        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
        barriere.countDown();
    }
    

    
}
