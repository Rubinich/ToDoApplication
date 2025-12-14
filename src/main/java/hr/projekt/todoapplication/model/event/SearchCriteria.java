package hr.projekt.todoapplication.model.event;

public enum SearchCriteria {
    USERNAME("Korisničko ime"),
    TITLE("Naslov događaja"),
    DESCRIPTION("Opis događaja"),
    DATETIME("Datum/Vrijeme");

    private final String label;
    SearchCriteria(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
