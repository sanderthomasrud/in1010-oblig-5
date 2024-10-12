import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Oblig5Hele {
    
    private static Monitor2 monitorIkkeHatt = new Monitor2();
    private static Monitor2 monitorHatt = new Monitor2();
    private static int harHatt = 0;
    private static int harIkkeHatt;
    private final static int FLETTETRADER = 8;
    private final static int TOTAL_FLETTETRADER = 16;
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        String path = args[0];
        lagNyttReg(path);
        slaaSammen(monitorHatt);
        slaaSammen(monitorIkkeHatt);

        skrivUtDominante();
    }

    private static void lagNyttReg(String path) throws FileNotFoundException, InterruptedException{
        File nyFil = new File(path);
        Scanner sc;
        try {
            sc = new Scanner(nyFil);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        
        String nyPath = path.replace("metadata.csv", "");
        File fil = new File(nyPath);
        int lesbareFiler = fil.list().length - 1;
        CountDownLatch leseBarriere = new CountDownLatch(lesbareFiler);
        // CountDownLatch fletteBarriere = new CountDownLatch();

        String linje;
        path = path.replace("metadata.csv", "");

        while (sc.hasNextLine()) {
            linje = sc.nextLine();

            String biter[] = linje.split(",");

            Runnable lesetrad;

            if (Boolean.valueOf(biter[1])) {
                lesetrad = new LeseTrad(path + biter[0], monitorHatt, leseBarriere); 
                harHatt++;
            }
            else {
                lesetrad = new LeseTrad(path + biter[0], monitorIkkeHatt, leseBarriere); 
                harIkkeHatt++;
            }

            Thread trad = new Thread(lesetrad);
            trad.start();          
        }

        leseBarriere.await();
        System.out.println("Ferdig med leseBarriere");
        sc.close();

        int resterendeFlettinger = harHatt - 1;

        CountDownLatch fletteBarriere;

        if (harHatt > 1 && harIkkeHatt > 1) {
            fletteBarriere = new CountDownLatch(TOTAL_FLETTETRADER);
        }
        else {
            fletteBarriere = new CountDownLatch(FLETTETRADER);
        }

        for (int i = 0; i < FLETTETRADER; i++) {
            Runnable flettetrad = new FletteTrad(monitorHatt, fletteBarriere, resterendeFlettinger);
            Thread trad = new Thread(flettetrad);
            trad.start();

            resterendeFlettinger--;
        }

        resterendeFlettinger = harIkkeHatt - 1;
        fletteBarriere = new CountDownLatch(FLETTETRADER);
        // fletteBarriere = new CountDownLatch(lesbareFiler - 1);

        for (int i = 0; i < FLETTETRADER; i++) {
            Runnable flettetrad = new FletteTrad(monitorIkkeHatt, fletteBarriere, resterendeFlettinger);
            Thread trad = new Thread(flettetrad);
            trad.start();

            resterendeFlettinger--;
        }

        fletteBarriere.await();
        System.out.println("Ferdig med fletteBarriere");

    }

    public static void slaaSammen(Monitor2 monitor) throws InterruptedException {
        while (monitor.hentAntallHashMaps() > 1) {
            HashMap<String, Subsekvens> nyttHash = Monitor2.slaaSammen(monitor.taUtHashMap(), monitor.taUtHashMap());
            
            monitor.settInn(nyttHash);
        }
    }

    private static void skrivUtDominante() {
        HashMap<String, Subsekvens> harHatt = monitorHatt.hentRegister().get(0);
        HashMap<String, Subsekvens> harIkkeHatt = monitorIkkeHatt.hentRegister().get(0);
        int tall = 0;

        ArrayList<String> dominant = new ArrayList<>(); 

        for (String key1 : harHatt.keySet()) {

            if (harIkkeHatt.keySet().contains(key1)) {
                for (String key2 : harIkkeHatt.keySet()) {
                    if (key1.equals(key2)) {
                        tall = harHatt.get(key1).hentAntall() - harIkkeHatt.get(key2).hentAntall();
                    }
                }    
            }
            else {
                tall = harHatt.get(key1).hentAntall();
            }
            
            if (tall > 6) {
                dominant.add(key1);
            }

        }

        for (String sub : dominant) {
            int forskjell = harHatt.get(sub).hentAntall() - harIkkeHatt.get(sub).hentAntall();
            System.out.println(sub + " med " + forskjell + " flere forekomster.");
        }

    }
}
