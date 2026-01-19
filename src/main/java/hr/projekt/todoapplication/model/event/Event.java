package hr.projekt.todoapplication.model.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Predstavlja jedan dogadaj u sustavu.
 * <p>
 *     Klasa sadrzi osnovne informacije o dogadaju poput naslova, opisa, datuma dospijeca,
 *     kategorije i prioriteta. Objekti ove klase stvaraju se putem unutarnje klase
 *     {@link EventBuilder} koja koristi "builder" obrazac.
 * </p>
 */
public class Event implements Serializable {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private EventInfo info;
    private String ownerId;

    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Privatni konstruktor koji se koristi samo unutar klase {@link EventBuilder}.
     *
     * @param builder objekt koji sadrzi sve podatke potrebne za stvaranje dogadaja
     */
    private Event(EventBuilder builder) {
        this.id = UUID.randomUUID().toString();
        this.title = builder.title;
        this.description = builder.description;
        this.dueDate = builder.dueDate;
        this.info = new EventInfo(builder.priority, builder.category);
        this.ownerId = builder.ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setInfo(EventInfo info) {
        this.info = info;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Omogucuje stvaranje objekta {@link Event} koristenjem "builder" obrasca.
     * <p>
     *     Klasa sadrzi metode za postupno dodavanje informacija o dogadaju
     *     kao sto su kategorija i prioritet, te na kraju stvara gotov objekt dogadaja.
     * </p>
     */
    public static class EventBuilder {
        private final String title;
        private final String description;
        private final LocalDateTime dueDate;
        private PriorityLevel priority = PriorityLevel.ZADANO;
        private EventCategory category = EventCategory.OSNOVNO;
        private String ownerId;

        /**
         * Inicijalizira osnovne podatke o dogadaju.
         *
         * @param title naslov dogadaja
         * @param description opis dogadaja
         * @param dueDate datum i vrijeme odrzavanja dogadaja
         */
        public EventBuilder(String title, String description, LocalDateTime dueDate, String ownerId) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.ownerId = ownerId;
        }

        /**
         * Postavlja kategoriju dogadaja.
         *
         * @param category kategorija dogadaja (npr. posao, kuca, slobodno)
         * @return trenutni objekt {@link EventBuilder}
         */
        public EventBuilder category(EventCategory category) {
            this.category = category;
            return this;
        }

        /**
         * Postavlja prioritet dogadaja.
         *
         * @param priority prioritet dogadaja (npr. nizak, srednji, visok)
         * @return trenutni objekt {@link EventBuilder}
         */
        public EventBuilder priority(PriorityLevel priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Stvara novi objekt {@link Event} koristeci podatke buildera.
         *
         * @return novi objekt klase {@link Event}
         */
        public Event build() {
            return new Event(this);
        }
    }

    /**
     * Vraca naslov dogadaja.
     *
     * @return naslov dogadaja
     */
    public String getTitle() { return title; }

    /**
     * Vraca opis dogadaja.
     *
     * @return opis dogadaja
     */
    public String getDescription() { return description; }

    /**
     * Vraca datum i vrijeme odrzavanja dogadaja.
     *
     * @return datum i vrijeme dogadaja
     */
    public LocalDateTime getDueDate() { return dueDate; }

    /**
     * Vraca dodatne informacije o dogadaju.
     *
     * @return objekt {@link EventInfo} s podacima o kategoriji i prioritetu
     */
    public EventInfo getInfo() { return info; }

    /**
     * Vraca tekstualni prikaz dogadaja koji sadrzi osnovne podatke.
     *
     * @return formatirani tekst dogadaja
     */
    @Override
    public String toString() {
        return "Naslov: " + title +
                "\nOpis: " + description +
                "\nDatum: " + dueDate +
                "\nKategorija: " + info.category() +
                "\nPrioritet: " + info.priority() +
                "\nVlasnik: " + ownerId +
                "\n----------------------------";
    }
}
