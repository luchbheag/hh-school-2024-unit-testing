package ru.hh.school.unittesting.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hh.school.unittesting.homework.LibraryManager;
import ru.hh.school.unittesting.homework.NotificationService;
import ru.hh.school.unittesting.homework.UserService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void getAvailableCopiesWhenExists() {
        final String bookId = "test_book";
        libraryManager.addBook(bookId, 5);

        assertEquals(5, libraryManager.getAvailableCopies(bookId));
    }

    @Test
    void getAvailableCopiesWhenDoesNotExist() {
        final String bookId = "test_book";
        assertEquals(0, libraryManager.getAvailableCopies(bookId));
    }

    @Test
    void borrowBookWhenUserIsNotActive() {
        final String userId = "test_user";
        final String bookId = "test_book";
        libraryManager.addBook(bookId, 5);
        when(userService.isUserActive(userId)).thenReturn(false);

        assertFalse(libraryManager.borrowBook(bookId, userId));
        verify(notificationService).notifyUser(userId, "Your account is not active.");
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void borrowBookWhenThereIsNoAvailableCopies() {
        final String userId = "test_user";
        final String bookId = "test_book";
        libraryManager.addBook(bookId, 0);
        when(userService.isUserActive(userId)).thenReturn(true);

        assertFalse(libraryManager.borrowBook(bookId, userId));
        verifyNoInteractions(notificationService);
    }

    @Test
    void borrowBookWhenThereAreAvailableCopies() {
        final String userId = "test_user";
        final String bookId = "test_book";
        libraryManager.addBook(bookId, 5);
        when(userService.isUserActive(userId)).thenReturn(true);

        assertTrue(libraryManager.borrowBook(bookId, userId));
        assertEquals(4, libraryManager.getAvailableCopies(bookId));
        verify(notificationService).notifyUser(userId, "You have borrowed the book: " + bookId);
        verifyNoMoreInteractions(notificationService);
    }


    /*
    - boolean borrowBook(String bookId, String userId)
    - boolean returnBook(String bookId, String userId)
    - double calculateDynamicLateFee(int overdueDays,
                                    boolean isBestseller,
                                    boolean isPremiumMember)

    ****
    - void addBook(String bookId, int quantity)
    - int getAvailableCopies(String bookId)

     */
}
