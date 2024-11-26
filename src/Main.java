import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LinkShortenerService service = new LinkShortenerService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to сервис сокращения ссылок!");
        System.out.println("Создайте двух пользователей:");

        System.out.print("Введите имя первого пользователя: ");
        String userName1 = scanner.nextLine();; // Первый пользователь, стоит по умолчанию
        User user1 = new User();
        System.out.println(userName1 + " зарегистрирован с UUID: " + user1.getUuid());

        System.out.print("Введите имя второго пользователя: ");
        String userName2 = scanner.nextLine();; //Второй пользователь
        User user2 = new User();
        System.out.println(userName2 + " зарегистрирован с UUID: " + user2.getUuid());

        User currentUser = user1;


        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Переключение пользователя");
            System.out.println("2. Сократить ссылку");
            System.out.println("3. Перейти по короткой ссылке");
            System.out.println("4. Посмотреть свои ссылки");
            System.out.println("5. Изменить лимит переходов");
            System.out.println("6. Удалить ссылку");
            System.out.println("7. Выход");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Переключение пользователя.");
                    currentUser = currentUser.equals(user1) ? user2 : user1;

                    System.out.println("Теперь вы работаете как пользователь: " +
                            (currentUser.equals(user1) ? userName1 : userName2));
                    break;

                case 2:
                    System.out.print("Введите оригинальную ссылку: ");
                    String originalUrl = scanner.nextLine();

                    System.out.print("Введите лимит переходов: ");
                    String maxClicksInput = scanner.nextLine();
                    int maxClicks = maxClicksInput.isEmpty() ? ConfigLoader.getMaxClicksLimit() : Integer.parseInt(maxClicksInput);

                    System.out.print("Введите время жизни ссылки (в секундах): ");
                    String durationInput = scanner.nextLine();
                    long duration = durationInput.isEmpty() ? ConfigLoader.getMaxLinkLifetime() : Long.parseLong(durationInput);
                    long maxLifetime = ConfigLoader.getMaxLinkLifetime();
                    String shortUrl = service.shortenLink(originalUrl, currentUser.getUuid(), maxClicks, duration);
                    System.out.println("Сокращенная ссылка: " + shortUrl);
                    long finalMaxClicks = ConfigLoader.getMaxClicksLimit();

                    if (maxClicks > finalMaxClicks){
                        System.out.println("Максимальный лимит переходов: " + finalMaxClicks);
                    }
                    else{
                        System.out.println("Лимит переходов: " + maxClicks);
                    }
                    if (duration > maxLifetime){
                        System.out.println("Максимальное время жизни ссылки: " + maxLifetime + " секунд");
                    }
                    else {
                        System.out.println("Время жизни ссылки: " + duration + " секунд");
                    }
                    break;

                case 3:
                    System.out.print("Введите короткую ссылку: ");
                    String shortLink = scanner.nextLine();

                    String original = service.getOriginalUrl(shortLink, currentUser.getUuid());
                    System.out.println(original);

                    if (!original.startsWith("Ссылка недоступна")) {
                        try {
                            Desktop.getDesktop().browse(new URI(original));
                        } catch (Exception e) {
                            System.out.println("Ошибка при открытии ссылки: " + e.getMessage());
                        }
                    }
                    break;

                case 4:
                    System.out.println("Ваши ссылки:");
                    service.getUserLinks(currentUser.getUuid()).forEach(link ->
                            System.out.println(link.getShortUrl() + " -> " + link.getOriginalUrl())
                    );
                    break;

                case 5:
                    System.out.print("Введите короткую ссылку для изменения лимита: ");
                    String shortUrlToUpdate = scanner.nextLine();

                    System.out.print("Введите новый лимит переходов: ");
                    int newMaxClicks = scanner.nextInt();
                    long updFinalMaxClicks = ConfigLoader. getMaxClicksLimit();
                    if (service.updateLinkLimit(shortUrlToUpdate, currentUser.getUuid(), newMaxClicks)) {
                        if (newMaxClicks > updFinalMaxClicks){
                            System.out.println("Максимальный лимит переходов не может превышать " + updFinalMaxClicks + ". Обновлено: " + updFinalMaxClicks);
                        }
                        else{
                            System.out.println("Лимит переходов успешно обновлен: " + newMaxClicks);
                        }

                    } else {
                        System.out.println("Ошибка: вы не являетесь создателем этой ссылки или она недоступна.");
                    }
                    break;

                case 6:
                    System.out.print("Введите короткую ссылку для удаления: ");
                    String deleteShortLink = scanner.nextLine();

                    if (service.deleteLink(deleteShortLink, currentUser.getUuid())) {
                        System.out.println("Ссылка успешно удалена.");
                    } else {
                        System.out.println("Ошибка: вы не являетесь создателем этой ссылки или она недоступна.");
                    }
                    break;

                case 7:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }

            service.cleanExpiredLinks();
        }
    }
}