package hr.projekt.todoapplication.model.event;

import java.util.Comparator;

/**
 * Definira razine prioriteta dogadaja.
 * <p>
 *     Svaki dogadaj moze imati razlicitu vaznost koja utjece na njegov prikaz
 *     i obradu unutar aplikacije.
 * </p>
 */
public enum PriorityLevel {
    /** Najnizi prioritet, dogadaj se moze ignorirati. */
    IGNORIRAJ(3),

    /** Zadani prioritet bez posebne vaznosti. */
    ZADANO(2),

    /** Visoki prioritet, dogadaj je vazan. */
    VAZNO(1);

    private final int weight;

    /**
     * Stvara novi prioritet s pripadajucom tezinom.
     *
     * @param weight numericka vrijednost prioriteta; manja vrijednost znaci veci prioritet
     */
    private PriorityLevel(int weight) {
        this.weight = weight;
    }

    /**
     * Vraca tezinu prioriteta. Manja vrijednost oznacava visu vaznost.
     *
     * @return tezina prioriteta
     */
    public int weight() {
        return weight;
    }

    /**
     * Vraca komparator koji usporeduje dogadaje prema njihovom prioritetu.
     * <p>Dogadaji s vecim prioritetom (nizom tezinom) bit ce sortirani ispred ostalih.</p>
     *
     * @return komparator za usporedbu dogadaja prema prioritetu
     */
    public static Comparator<Event> byPriority() {
        return Comparator.comparingInt(e -> e.getInfo().priority().weight());
    }
}
