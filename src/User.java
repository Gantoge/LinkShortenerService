import java.util.UUID;

public class User {
    private final UUID uuid;

    public User() {
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}