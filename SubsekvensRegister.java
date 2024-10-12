import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Random;
import java.util.Scanner;

public class SubsekvensRegister {
    protected ArrayList<HashMap<String, Subsekvens>> register = new ArrayList<>();

    public void settInn(HashMap<String, Subsekvens> hashmap) throws InterruptedException {
        register.add(hashmap);
    }

    public HashMap<String, Subsekvens> taUtHashMap() {
        HashMap<String, Subsekvens> retur = register.get(0);
        register.remove(0);
        return retur;
    }

    public int hentAntallHashMaps() {
        return register.size();
    }

    public ArrayList<HashMap<String, Subsekvens>> hentRegister() {
        return register;
    }

    public static HashMap<String, Subsekvens> lesFraFil(String filnavn) throws FileNotFoundException {
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
    }

    public static HashMap<String, Subsekvens> slaaSammen(HashMap<String, Subsekvens> hash1, HashMap<String, Subsekvens> hash2) {
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
    }
}
