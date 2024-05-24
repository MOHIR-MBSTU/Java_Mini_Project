import java.sql.*;
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
    private Connection connection;

    public Library() {
        try {
            String url = "jdbc:mysql://localhost:3306/library_db";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        String query = "INSERT INTO books (id, title, author, is_available) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, book.id);
            stmt.setString(2, book.title);
            stmt.setString(3, book.author);
            stmt.setBoolean(4, book.isAvailable);
            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book removed successfully.");
            } else {
                System.out.println("No book found with ID: " + bookId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchBook(String query) {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                book.isAvailable = rs.getBoolean("is_available");
                System.out.println(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayBooks() {
        String query = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                book.isAvailable = rs.getBoolean("is_available");
                System.out.println(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        String queryCheckDuplicate = "SELECT name FROM users WHERE id = ?";
        String queryInsertUser = "INSERT INTO users (id, name) VALUES (?, ?)";

        try (PreparedStatement stmtCheckDuplicate = connection.prepareStatement(queryCheckDuplicate);
             PreparedStatement stmtInsertUser = connection.prepareStatement(queryInsertUser)) {

            stmtCheckDuplicate.setInt(1, user.id);
            ResultSet resultSet = stmtCheckDuplicate.executeQuery();

            if (resultSet.next()) {
                System.out.println("This is a duplicate value. Please try again.");
            } else {
                stmtInsertUser.setInt(1, user.id);
                stmtInsertUser.setString(2, user.name);
                stmtInsertUser.executeUpdate();
                System.out.println("User added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            System.out.println("User removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayUsers() {
        String query = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"));
                System.out.println(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void issueBook(int bookId, int userId) {
        String checkBookQuery = "SELECT * FROM books WHERE id = ? AND is_available = TRUE";
        String issueBookQuery = "INSERT INTO borrowed_books (user_id, book_id) VALUES (?, ?)";
        String updateBookQuery = "UPDATE books SET is_available = FALSE WHERE id = ?";
        try (PreparedStatement checkBookStmt = connection.prepareStatement(checkBookQuery);
             PreparedStatement issueBookStmt = connection.prepareStatement(issueBookQuery);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {

            checkBookStmt.setInt(1, bookId);
            ResultSet rs = checkBookStmt.executeQuery();

            if (rs.next()) {
                issueBookStmt.setInt(1, userId);
                issueBookStmt.setInt(2, bookId);
                issueBookStmt.executeUpdate();

                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
                System.out.println("Book issued successfully.");
            } else {
                System.out.println("Book cannot be issued.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(int bookId, int userId) {
        String returnBookQuery = "DELETE FROM borrowed_books WHERE user_id = ? AND book_id = ?";
        String updateBookQuery = "UPDATE books SET is_available = TRUE WHERE id = ?";
        try (PreparedStatement returnBookStmt = connection.prepareStatement(returnBookQuery);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {

            returnBookStmt.setInt(1, userId);
            returnBookStmt.setInt(2, bookId);
            returnBookStmt.executeUpdate();

            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();
            System.out.println("Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
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

            if (choice == 1) {
                System.out.print("Enter book ID: ");
                int bookId = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter book title: ");
                String title = scanner.nextLine();
                System.out.print("Enter book author: ");
                String author = scanner.nextLine();
                library.addBook(new Book(bookId, title, author));
            } else if (choice == 2) {
                System.out.print("Enter book ID to remove: ");
                int bookId = scanner.nextInt();
                library.removeBook(bookId);
            } else if (choice == 3) {
                System.out.print("Enter book title or author to search: ");
                scanner.nextLine();
                String query = scanner.nextLine();
                library.searchBook(query);
            } else if (choice == 4) {
                library.displayBooks();
            } else if (choice == 5) {
                System.out.print("Enter user ID: ");
                int userId = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter user name: ");
                String name = scanner.nextLine();
                library.addUser(new User(userId, name));
            } else if (choice == 6) {
                System.out.print("Enter user ID to remove: ");
                int userId = scanner.nextInt();
                library.removeUser(userId);
            } else if (choice == 7) {
                library.displayUsers();
            } else if (choice == 8) {
                System.out.print("Enter book ID to issue: ");
                int bookId = scanner.nextInt();
                System.out.print("Enter user ID: ");
                int userId = scanner.nextInt();
                library.issueBook(bookId, userId);
            } else if (choice == 9) {
                System.out.print("Enter book ID to return: ");
                int bookId = scanner.nextInt();
                System.out.print("Enter user ID: ");
                int userId = scanner.nextInt();
                library.returnBook(bookId, userId);
            } else if (choice == 10) {
                System.out.println("Exiting...");
                scanner.close();
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
