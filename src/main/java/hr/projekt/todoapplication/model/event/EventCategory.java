package hr.projekt.todoapplication.model.event;

import java.util.Comparator;

/**
 * Definira kategorije dogadaja.
 * <p>
 *     Kategorije se koriste za organizaciju i filtriranje dogadaja
 *     prema njihovoj prirodi, npr. osobno, posao, shopping.
 * </p>
 */
public enum EventCategory {
    /** Osobni dogadaji poput slobodnog vremena ili hobija. */
    OSOBNO,

    /** Poslovni dogadaji i radne obaveze. */
    POSAO,

    /** Kupovina i zadaci povezani sa shoppingom. */
    SHOPPING,

    /** Opca ili osnovna kategorija. */
    OSNOVNO;

    /**
     * Vraca komparator koji usporeduje dogadaje prema nazivu kategorije.
     * <p>Kategorizira dogadaje po abecednom redoslijedu naziva enum vrijednosti.</p>
     *
     * @return komparator za usporedbu dogadaja prema kategoriji
     */
    public static Comparator<Event> byCategory() {
        return Comparator.comparing(e -> e.getInfo().category().name());
    }
}
