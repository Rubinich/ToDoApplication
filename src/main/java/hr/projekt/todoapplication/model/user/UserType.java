package hr.projekt.todoapplication.model.user;

public enum UserType {
    ADMIN("ADMIN"),
    USER("USER");

    private final String type;

    UserType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static UserType fromString(String value) {
        for (UserType userType : values()) {
            if (userType.type.equalsIgnoreCase(value)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Nepoznat tip: " + value);
    }
}
