import java.util.ArrayList;
import java.util.Scanner;

class Book {
    int id;
    String title;
    String author;
    boolean isAvailable;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    @Override
    public String toString() {
        return "Book ID: " + id + ", Title: " + title + ", Author: " + author + ", Available: " + isAvailable;
    }
}

class User {
    int id;
    String name;
    ArrayList<Book> borrowedBooks;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User ID: " + id + ", Name: " + name + ", Borrowed Books: " + borrowedBooks.size();
    }
}

class Library {
    ArrayList<Book> books;
    ArrayList<User> users;

    public Library() {
        books = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added successfully.");
    }

    public void removeBook(int bookId) {
        books.removeIf(book -> book.id == bookId);
        System.out.println("Book removed successfully.");
    }

    public void searchBook(String query) {
        for (Book book : books) {
            if (book.title.contains(query) || book.author.contains(query)) {
                System.out.println(book);
            }
        }
    }

    public void displayBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void addUser(User user) {
        users.add(user);
        System.out.println("User added successfully.");
    }

    public void removeUser(int userId) {
        users.removeIf(user -> user.id == userId);
        System.out.println("User removed successfully.");
    }

    public void displayUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }

    public void issueBook(int bookId, int userId) {
        Book book = books.stream().filter(b -> b.id == bookId).findFirst().orElse(null);
        User user = users.stream().filter(u -> u.id == userId).findFirst().orElse(null);

        if (book != null && user != null && book.isAvailable) {
            book.isAvailable = false;
            user.borrowedBooks.add(book);
            System.out.println("Book issued successfully.");
        } else {
            System.out.println("Book cannot be issued.");
        }
    }

    public void returnBook(int bookId, int userId) {
        User user = users.stream().filter(u -> u.id == userId).findFirst().orElse(null);

        if (user != null) {
            Book book = user.borrowedBooks.stream().filter(b -> b.id == bookId).findFirst().orElse(null);
            if (book != null) {
                book.isAvailable = true;
                user.borrowedBooks.remove(book);
                System.out.println("Book returned successfully.");
            } else {
                System.out.println("Book not found in user's borrowed list.");
            }
        } else {
            System.out.println("User not found.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Library Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Search Book");
            System.out.println("4. Display Books");
            System.out.println("5. Add User");
            System.out.println("6. Remove User");
            System.out.println("7. Display Users");
            System.out.println("8. Issue Book");
            System.out.println("9. Return Book");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter book ID: ");
                    int bookId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    library.addBook(new Book(bookId, title, author));
                    break;
                case 2:
                    System.out.print("Enter book ID to remove: ");
                    bookId = scanner.nextInt();
                    library.removeBook(bookId);
                    break;
                case 3:
                    System.out.print("Enter book title or author to search: ");
                    scanner.nextLine();
                    String query = scanner.nextLine();
                    library.searchBook(query);
                    break;
                case 4:
                    library.displayBooks();
                    break;
                case 5:
                    System.out.print("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter user name: ");
                    String name = scanner.nextLine();
                    library.addUser(new User(userId, name));
                    break;
                case 6:
                    System.out.print("Enter user ID to remove: ");
                    userId = scanner.nextInt();
                    library.removeUser(userId);
                    break;
                case 7:
                    library.displayUsers();
                    break;
                case 8:
                    System.out.print("Enter book ID to issue: ");
                    bookId = scanner.nextInt();
                    System.out.print("Enter user ID: ");
                    userId = scanner.nextInt();
                    library.issueBook(bookId, userId);
                    break;
                case 9:
                    System.out.print("Enter book ID to return: ");
                    bookId = scanner.nextInt();
                    System.out.print("Enter user ID: ");
                    userId = scanner.nextInt();
                    library.returnBook(bookId, userId);
                    break;
                case 10:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
