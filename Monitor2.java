import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;
// import java.util.concurrent.locks.Condition;
// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;


public class Monitor2 extends SubsekvensRegister {
    private static Lock laas = new ReentrantLock(true);
    private Condition harToKlare = laas.newCondition();
    private final int MINST = 2;

    public static HashMap<String, Subsekvens> lesFraFil(String filnavn) throws FileNotFoundException {
        laas.lock();
        try {
            HashMap<String, Subsekvens> nyHash = new HashMap<>();
            File nyFil = new File(filnavn);
            Scanner sc;
            try {
                sc = new Scanner(nyFil);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException();
            }

            ArrayList<String> bokstaver = new ArrayList<>();
            ArrayList<String> sekvens = new ArrayList<>();
            String linje;

            while (sc.hasNextLine()) {
                linje = sc.nextLine();

                if (linje.length() < 3) {
                    System.exit(-1);
                }
                String[] biter = linje.split("");

                for (String bit : biter) {
                    bokstaver.add(bit);
                }

                while (bokstaver.size() >= 3) {
                    String sek = bokstaver.get(0) + bokstaver.get(1) + bokstaver.get(2);
                    if (!sekvens.contains(sek)) {
                        sekvens.add(sek);
                    }
                    bokstaver.remove(0);
                }

                for (String s : sekvens) {
                    Subsekvens ny = new Subsekvens(s, 1);
                    nyHash.put(s, ny);
                }
                bokstaver.clear();
                sekvens.clear();
            }
            sc.close();

            return nyHash;
        } finally {
            laas.unlock();
        }
    }

    @Override
    public void settInn(HashMap<String, Subsekvens> hashmap) throws InterruptedException {
        laas.lock();
        try {
           super.settInn(hashmap);
           if (hentAntallHashMaps() >= MINST) {
               harToKlare.signalAll();
           }
        } finally {
            laas.unlock();
        }
    }

    public void settInnFlettet(HashMap<String, Subsekvens> hashmap) throws InterruptedException {
        laas.lock();
        try {
            super.settInn(hashmap);
            if (hentAntallHashMaps() >= MINST) {
                harToKlare.signalAll();
            }
            // teller++; // mulig denne maa bort
            System.out.println("Satte inn flettet kart!");
            // if (teller == antallFiler - 1) { // mulig denne maa bort
            //     ferdig = true;
            // }

        } finally {
            laas.unlock();
        }
    }

    public ArrayList<HashMap<String, Subsekvens>> hentUtToKart() throws InterruptedException {
        laas.lock();
        try {
            ArrayList<HashMap<String, Subsekvens>> toKart = new ArrayList<>();
            while (hentAntallHashMaps() < MINST) {
                harToKlare.await();
            }
            toKart.add(taUtHashMap()); toKart.add(taUtHashMap());

            return toKart;
        } finally {
            laas.unlock();
        }
    }

    public static HashMap<String, Subsekvens> slaaSammen(HashMap<String, Subsekvens> hash1, HashMap<String, Subsekvens> hash2) {
        laas.lock();
        try {
            HashMap<String, Subsekvens> nyttMap = new HashMap<>();

            for (String key1 : hash1.keySet()) {
                if (!nyttMap.containsKey(key1)) {
                    Subsekvens ny = new Subsekvens(key1, hash1.get(key1).hentAntall());
                    nyttMap.put(key1, ny);
                }
                else {
                    int antall = hash1.get(key1).hentAntall();
                    nyttMap.get(key1).leggTil(antall);
                }
            }

            for (String key2 : hash2.keySet()) {
                if (!nyttMap.containsKey(key2)) {
                    Subsekvens ny = new Subsekvens(key2, hash2.get(key2).hentAntall());
                    nyttMap.put(key2, ny);
                }
                else {
                    int antall = hash2.get(key2).hentAntall();
                    nyttMap.get(key2).leggTil(antall);
                }
            }

        return nyttMap;
        } finally {
            laas.unlock();
        }

    }

    // public boolean hentStatus() { // mulig denne maa bort
    //     return ferdig;
    // }

}
