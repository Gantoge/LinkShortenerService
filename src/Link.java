import java.util.UUID;

public class Link {
    private final String originalUrl;
    private final String shortUrl;
    private int maxClicks;
    private final long expirationTime;
    private final UUID creatorUuid;
    private int currentClicks;

    public Link(String originalUrl, String shortUrl, int maxClicks, long durationInSeconds, UUID creatorUuid) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.maxClicks = maxClicks;
        this.creatorUuid = creatorUuid;
        this.expirationTime = System.currentTimeMillis() + durationInSeconds * 1000;
        this.currentClicks = 0;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public void setMaxClicks(int newMaxClicks) {
        this.maxClicks = newMaxClicks;
    }

    public int getCurrentClicks() {
        return currentClicks;
    }

    public void incrementClicks() {
        currentClicks++;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public UUID getCreatorUuid() {
        return creatorUuid;
    }
}