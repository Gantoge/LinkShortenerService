import java.util.*;

public class LinkShortenerService {
    private final Map<String, Link> links = new HashMap<>();
    private final Map<UUID, List<Link>> userLinks = new HashMap<>();

    public String shortenLink(String originalUrl, UUID userUuid, int maxClicks, long durationInSeconds) {
        long maxLifetime = ConfigLoader.getMaxLinkLifetime();
        long finalDuration = Math.min(durationInSeconds, maxLifetime);
        int maxClicksFromConfig = ConfigLoader.getMaxClicksLimit();
        int finalMaxClicks = Math.max(maxClicks, maxClicksFromConfig);

        String shortUrl = "http://short.ly/" + userUuid.toString().substring(0, 8) + UUID.randomUUID().toString().substring(0, 8);
        Link link = new Link(originalUrl, shortUrl, maxClicks, finalDuration, userUuid);

        links.put(shortUrl, link);
        userLinks.computeIfAbsent(userUuid, k -> new ArrayList<>()).add(link);
        return shortUrl;

    }

    public String getOriginalUrl(String shortUrl, UUID userId) {
        Link link = links.get(shortUrl);

        if (link == null) {
            return "Ссылка недоступна: не существует.";
        }

        if (link.isExpired()) {
            links.remove(shortUrl);
            userLinks.get(link.getCreatorUuid()).remove(link);
            return "Ссылка недоступна: время жизни истекло.";
        }

        if (link.getCurrentClicks() >= link.getMaxClicks()) {
            return "Ссылка недоступна: лимит кликов исчерпан.";
        }

        link.incrementClicks();
        return link.getOriginalUrl();
    }

    public List<Link> getUserLinks(UUID userId) {
        return userLinks.getOrDefault(userId, Collections.emptyList());
    }

    public boolean updateLinkLimit(String shortUrl, UUID userUuid, int newMaxClicks) {
        Link link = links.get(shortUrl);

        if (link == null || !link.getCreatorUuid().equals(userUuid)) {
            return false;
        }

        link.setMaxClicks(newMaxClicks);
        return true;
    }

    public boolean deleteLink(String shortUrl, UUID userUuid) {
        Link link = links.get(shortUrl);

        if (link == null || !link.getCreatorUuid().equals(userUuid)) {
            return false;
        }

        links.remove(shortUrl);
        userLinks.get(userUuid).remove(link);
        return true;
    }

    public void cleanExpiredLinks() {
        long now = System.currentTimeMillis();

        Iterator<Map.Entry<String, Link>> iterator = links.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Link> entry = iterator.next();
            Link link = entry.getValue();

            if (link.isExpired()) {
                iterator.remove();
                userLinks.get(link.getCreatorUuid()).remove(link);
            }
        }
    }
}