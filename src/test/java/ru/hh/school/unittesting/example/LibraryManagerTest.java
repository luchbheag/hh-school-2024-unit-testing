package ru.hh.school.unittesting.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hh.school.unittesting.homework.LibraryManager;
import ru.hh.school.unittesting.homework.NotificationService;
import ru.hh.school.unittesting.homework.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LibraryManagerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager(notificationService, userService);
    }

    @Test
    void getAvalableCopiesWhenExists() {
        final String bookName = "test_book";
        libraryManager.addBook(bookName, 5);

        assertEquals(5, libraryManager.getAvailableCopies(bookName));
    }

    @Test
    void getAvalableCopiesWhenDoesNotExist() {
        final String bookName = "test_book";
        assertEquals(0, libraryManager.getAvailableCopies(bookName));
    }




    /*
    - void addBook(String bookId, int quantity)
    - boolean borrowBook(String bookId, String userId)
    - boolean returnBook(String bookId, String userId)
    - int getAvailableCopies(String bookId)
    - double calculateDynamicLateFee(int overdueDays,
                                    boolean isBestseller,
                                    boolean isPremiumMember)

     */
}
