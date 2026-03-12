package com.example.javalabs.basic;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for the basic Java learning module.
 *
 * <p>The goal of this class is not to contain complex logic. Instead, it wires together the
 * smaller example classes so you can run the project and inspect how each concept behaves.</p>
 */
public final class LearningApp {

    private LearningApp() {
    }

    /**
     * Runs a small guided tour through the example classes.
     *
     * @param args command-line arguments that are currently unused
     */
    public static void main(String[] args) {
        printSection("Control Flow");
        System.out.println("classifyNumber(7) = " + ControlFlowExamples.classifyNumber(7));
        System.out.println("sumEvenNumbers(10) = " + ControlFlowExamples.sumEvenNumbers(10));
        System.out.println("factorial(5) = " + ControlFlowExamples.factorial(5));

        printSection("Strings");
        System.out.println("reverseWords('java is fun') = " + StringToolkit.reverseWords("java is fun"));
        System.out.println("countVowels('Documentation') = " + StringToolkit.countVowels("Documentation"));
        System.out.println("isPalindrome('level') = " + StringToolkit.isPalindrome("level"));

        printSection("Objects and Encapsulation");
        BankAccount account = new BankAccount("ACC-001", "Alice", 100.0);
        account.deposit(50.0);
        account.withdraw(20.0);
        System.out.println(account.summary());

        printSection("Collections and Streams");
        List<Student> students = List.of(
                new Student("Alice", 92, List.of("Java", "SQL")),
                new Student("Bob", 77, List.of("Spring")),
                new Student("Cara", 88, List.of("Java", "Testing", "Docker"))
        );
        System.out.println("Top student = " + StudentAnalytics.findTopStudent(students).name());
        System.out.println("Average score = " + StudentAnalytics.averageScore(students));
        System.out.println("Students studying Java = " + StudentAnalytics.filterByTopic(students, "java"));

        printSection("Polymorphism and Sealed Types");
        List<Shape> shapes = List.of(new Circle(2.0), new Rectangle(3.0, 4.0));
        for (Shape shape : shapes) {
            System.out.println(shape.describe() + ", area = " + shape.area());
        }

        printSection("Asynchronous Code");
        String report = AsyncExamples.buildUserReport("Ada").join();
        System.out.println(report);

        printSection("Enums and Generics");
        Pair<String, DifficultyLevel> learningPair = Pair.of("Current module", DifficultyLevel.INTERMEDIATE);
        System.out.println(learningPair.left() + " -> " + learningPair.right());
        System.out.println("Advice: " + learningPair.right().studyAdvice());

        printSection("Maps and Sorting");
        List<InventoryItem> inventory = List.of(
                new InventoryItem("Keyboard", 12, "hardware"),
                new InventoryItem("Mouse", 5, "hardware"),
                new InventoryItem("Notebook", 20, "stationery"),
                new InventoryItem("Pen", 3, "stationery")
        );
        System.out.println("Quantity by category = " + InventoryAnalytics.totalQuantityByCategory(inventory));
        System.out.println("Top stock item = " + InventoryAnalytics.sortByStockDescending(inventory).get(0).name());
        System.out.println("Low stock warning = " + InventoryAnalytics.firstLowStockItem(inventory, 3));

        printSection("Exceptions");
        System.out.println("safeDivide(9, 3) = " + ExceptionPlayground.safeDivide(9, 3));
        System.out.println("parsePositiveInt('24') = " + ExceptionPlayground.parsePositiveInt("24"));

        printSection("File I/O");
        try {
            Path demoFile = Files.createTempFile("java-labs-demo", ".txt");
            Files.writeString(demoFile, "Java\n\nSpring\nTesting\n");
            System.out.println(FileReport.readNonBlankLines(demoFile));
            System.out.println(FileReport.summarize(demoFile));
            Files.deleteIfExists(demoFile);
        } catch (Exception exception) {
            System.out.println("File demo failed: " + exception.getMessage());
        }

        printSection("Rate Limiting");
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 1_000, timeSource);
        System.out.println("allow #1 = " + limiter.allow("demo-client"));
        System.out.println("allow #2 = " + limiter.allow("demo-client"));
        System.out.println("allow #3 = " + limiter.allow("demo-client"));
        timeSource.advanceMillis(1_000);
        System.out.println("allow after reset = " + limiter.allow("demo-client"));

        printSection("Connection Pooling");
        SimpleConnectionPool<FakeDatabaseConnection> pool =
                new SimpleConnectionPool<>(2, id -> new FakeDatabaseConnection(id, "users-db"));
        try (SimpleConnectionPool.PooledConnection<FakeDatabaseConnection> connection = pool.borrow()) {
            System.out.println(connection.value().query("select * from users where active = true"));
        }
        System.out.println("available connections after release = " + pool.availableCount());

        printSection("Multi-Database Routing");
        MultiDatabaseUserProfileRepository multiRepository = new MultiDatabaseUserProfileRepository(java.util.Map.of(
                Region.APAC, new InMemoryUserProfileRepository("users-apac"),
                Region.EU, new InMemoryUserProfileRepository("users-eu"),
                Region.US, new InMemoryUserProfileRepository("users-us")
        ));
        UserProfile apacUser = new UserProfile("u-100", "apac@example.com", Region.APAC);
        multiRepository.save(apacUser);
        System.out.println("APAC database = " + multiRepository.databaseNameFor(Region.APAC));
        System.out.println("user lookup = " + multiRepository.findById("u-100"));

        printSection("Microservice-Style Service");
        InMemoryNotificationClient notificationClient = new InMemoryNotificationClient();
        RegistrationService registrationService =
                new RegistrationService(multiRepository, notificationClient, new FixedWindowRateLimiter(5, 60_000, new SystemTimeSource()));
        RegistrationResult registrationResult =
                registrationService.register("learning-api-key", new UserProfile("u-200", "new@example.com", Region.EU));
        System.out.println(registrationResult);
        System.out.println("sent notifications = " + notificationClient.sentMessages());
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
