public class Subsekvens {
    public final String SUBSEKVENS;
    private int antall;

    public Subsekvens (String subsekvens, int antall) { // antall forekomster starter alltid paa 1
        this.SUBSEKVENS = subsekvens;
        this.antall = antall;
    }

    public void leggTil(int antall) {
        this.antall += antall;
    }

    public int hentAntall() {
        return antall;
    }

    public String toString() {
        String str = SUBSEKVENS;
        str += ",";
        str += Integer.toString(antall);
        return str;
    }

    public String hentNavn() {
        return SUBSEKVENS;
    }
}
