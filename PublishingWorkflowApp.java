import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

// Base class for Workflow Stages
abstract class WorkflowStage {
    protected String name;
    protected boolean isComplete;

    public WorkflowStage(String name) {
        this.name = name;
        this.isComplete = false;
    }

    public abstract boolean checkCompletion(Book book);

    public String getName() {
        return name;
    }

    public boolean isComplete() {
        return isComplete;
    }
}

// Concrete Workflow Stages
class ReviewStage extends WorkflowStage {
    private int requiredReviews;
    private int currentReviews;

    public ReviewStage(int requiredReviews) {
        super("Review Stage");
        this.requiredReviews = requiredReviews;
        this.currentReviews = 0;
    }

    public void addReview() {
        currentReviews++;
        if (currentReviews >= requiredReviews) {
            isComplete = true;
        }
    }

    @Override
    public boolean checkCompletion(Book book) {
        return isComplete;
    }
}

class EditingStage extends WorkflowStage {
    public EditingStage() {
        super("Editing Stage");
    }

    public void approveEditing() {
        isComplete = true;
    }

    @Override
    public boolean checkCompletion(Book book) {
        return isComplete;
    }
}

class ApprovalStage extends WorkflowStage {
    public ApprovalStage() {
        super("Approval Stage");
    }

    public void approve() {
        isComplete = true;
    }

    @Override
    public boolean checkCompletion(Book book) {
        return isComplete;
    }
}

// Book class representing the manuscript
class Book {
    private String title;
    private String genre;
    private User author;
    private String status;
    private WorkflowStage currentStage;
    private static int bookCounter = 0;
    private int serialNumber;

    public Book(String title, String genre, User author) {
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.status = "Draft";
        this.serialNumber = ++bookCounter;
    }

    public void setStage(WorkflowStage stage) {
        this.currentStage = stage;
    }

    public WorkflowStage getCurrentStage() {
        return currentStage;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
}

// Base class for Users
abstract class User {
    protected String name;

    public User(String name) {
        this.name = name;
    }

    public abstract void performRole(Book book, Scanner scanner);
}

// Specific User roles
class Author extends User {
    public Author(String name) {
        super(name);
    }

    @Override
    public void performRole(Book book, Scanner scanner) {
        System.out.println(name + " submitted the book: " + book.getTitle());
    }
}

class Reviewer extends User {
    public Reviewer(String name) {
        super(name);
    }

    public void reviewBook(ReviewStage stage) {
        stage.addReview();
        System.out.println(name + " reviewed the book.");
    }

    @Override
    public void performRole(Book book, Scanner scanner) {
        if (book.getCurrentStage() instanceof ReviewStage) {
            reviewBook((ReviewStage) book.getCurrentStage());
        }
    }
}

class Editor extends User {
    public Editor(String name) {
        super(name);
    }

    public void editBook(EditingStage stage) {
        stage.approveEditing();
        System.out.println(name + " edited and approved the book.");
    }

    public void approveBook(ApprovalStage stage) {
        stage.approve();
        System.out.println(name + " gave final approval for the book.");
    }

    @Override
    public void performRole(Book book, Scanner scanner) {
        WorkflowStage stage = book.getCurrentStage();
        if (stage instanceof EditingStage) {
            editBook((EditingStage) stage);
        } else if (stage instanceof ApprovalStage) {
            approveBook((ApprovalStage) stage);
        }
    }
}

// PublishingManager to oversee the workflow
class PublishingManager {
    public void advanceWorkflow(Book book) {
        WorkflowStage currentStage = book.getCurrentStage();
        if (currentStage.checkCompletion(book)) {
            if (currentStage instanceof ReviewStage) {
                book.setStage(new EditingStage());
                System.out.println("Book moved to Editing Stage.");
            } else if (currentStage instanceof EditingStage) {
                book.setStage(new ApprovalStage());
                System.out.println("Book moved to Approval Stage.");
            } else if (currentStage instanceof ApprovalStage) {
                book.updateStatus("Published");
                System.out.println("Book has been published.");
            }
        } else {
            System.out.println("Current stage is not complete yet.");
        }
    }
}

// ReportingSystem to show book status and reviews
class ReportingSystem {
    private List<Book> booksDatabase;

    public ReportingSystem() {
        booksDatabase = new ArrayList<>();
    }

    public void addBook(Book book) {
        booksDatabase.add(book);
    }

    public void generateReport(Book book) {
        System.out.println("\nGenerating report for book: " + book.getTitle());
        System.out.println("Serial Number: " + book.getSerialNumber());
        System.out.println("Status: " + book.getStatus());
        System.out.println("Current Stage: " + book.getCurrentStage().getName());
    }

    public void showAllBooks() {
        System.out.println("\nAll Books in the system:");
        for (Book book : booksDatabase) {
            System.out.println("Book Title: " + book.getTitle() + ", Serial Number: " + book.getSerialNumber() + ", Status: " + book.getStatus());
        }
    }
}

// Main method to simulate the workflow
public class PublishingWorkflowApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get details from user
        System.out.print("Enter the title of the book: ");
        String bookTitle = scanner.nextLine();

        System.out.print("Enter the genre of the book: ");
        String bookGenre = scanner.nextLine();

        System.out.print("Enter the name of the author: ");
        String authorName = scanner.nextLine();
        Author author = new Author(authorName);

        System.out.print("Enter the name of the first reviewer: ");
        String reviewer1Name = scanner.nextLine();
        Reviewer reviewer1 = new Reviewer(reviewer1Name);

        System.out.print("Enter the name of the second reviewer: ");
        String reviewer2Name = scanner.nextLine();
        Reviewer reviewer2 = new Reviewer(reviewer2Name);

        System.out.print("Enter the name of the editor: ");
        String editorName = scanner.nextLine();
        Editor editor = new Editor(editorName);

        // Creating Book and Workflow Manager
        Book book = new Book(bookTitle, bookGenre, author);
        PublishingManager manager = new PublishingManager();
        ReportingSystem reportSystem = new ReportingSystem();

        // Adding book to reporting system (Database)
        reportSystem.addBook(book);

        // Initial Submission by Author
        author.performRole(book, scanner);

        // Setting up the workflow stages
        ReviewStage reviewStage = new ReviewStage(2);
        book.setStage(reviewStage);

        // Prompt for Review Stage
        System.out.println("---- Review Stage ----");
        while (!reviewStage.isComplete()) {
            System.out.println("Enter 1 to add a review by " + reviewer1.name + ", 2 to add a review by " + reviewer2.name + ":");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            if (choice == 1) {
                reviewer1.performRole(book, scanner);
            } else if (choice == 2) {
                reviewer2.performRole(book, scanner);
            }
        }

        manager.advanceWorkflow(book);
        reportSystem.generateReport(book);

        // Prompt for Editing Stage
        System.out.println("---- Editing Stage ----");
        System.out.println("Enter 1 to approve editing by " + editor.name + ":");
        int editChoice = scanner.nextInt();
        if (editChoice == 1) {
            editor.performRole(book, scanner);
        }

        manager.advanceWorkflow(book);
        reportSystem.generateReport(book);

        // Prompt for Approval Stage
        System.out.println("---- Approval Stage ----");
        System.out.println("Enter 1 to approve the final book by " + editor.name + ":");
        int approveChoice = scanner.nextInt();
        if (approveChoice == 1) {
            editor.performRole(book, scanner);
        }

        manager.advanceWorkflow(book);
        reportSystem.generateReport(book);

        // Final Status
        System.out.println("Final Status: " + book.getStatus());
        scanner.close();

        // Show all books in the system
        reportSystem.showAllBooks();
    }
}
