package ru.hh.school.unittesting.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hh.school.unittesting.homework.LibraryManager;
import ru.hh.school.unittesting.homework.NotificationService;
import ru.hh.school.unittesting.homework.UserService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void returnNoBookWhenThereAreNoSuchBookBorrowed() {
        final String userId = "test_user";
        final String bookId = "test_book";

        assertFalse(libraryManager.returnBook(bookId, userId));
    }

    @Test
    void returnNoBookWhenBorrowedByAnotherUser() {
        final String userId = "test_user_one";
        final String anotherUserId = "test_user_two";
        final String bookId = "test_book";
        when(userService.isUserActive(anotherUserId)).thenReturn(true);
        libraryManager.addBook(bookId, 5);
        libraryManager.borrowBook(bookId, anotherUserId);

        assertFalse(libraryManager.returnBook(bookId, userId));
        verify(notificationService).notifyUser(anotherUserId, "You have borrowed the book: " + bookId);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void returnBookProperly() {
        final String userId = "test_user";
        final String bookId = "test_book";
        when(userService.isUserActive(userId)).thenReturn(true);
        libraryManager.addBook(bookId, 5);
        libraryManager.borrowBook(bookId, userId);

        assertTrue(libraryManager.returnBook(bookId, userId));
        assertEquals(5, libraryManager.getAvailableCopies(bookId));
        verify(notificationService).notifyUser(userId, "You have borrowed the book: " + bookId);
        verify(notificationService).notifyUser(userId, "You have returned the book: " + bookId);
        verifyNoMoreInteractions(notificationService);
    }

    @ParameterizedTest
    @CsvSource({
        "0, true, true, 0.0",
        "10, true, true, 6.0",
        "10, true, false, 7.5",
        "10, false, true, 4.0",
        "10, false, false, 5.0"
    })
    void testCalculateDynamicLateFee(int overdueDays,
                                     boolean isBestseller,
                                     boolean isPremiumMember,
                                     double expectedLateFee) {
        assertEquals(expectedLateFee, libraryManager.calculateDynamicLateFee(overdueDays, isBestseller, isPremiumMember));
    }

    @Test
    void throwsExceptionIfCalculateDynamicLateFeeForNegativeDays() {
        assertThrows(IllegalArgumentException.class, () -> libraryManager.calculateDynamicLateFee(-1, false, false));
    }
}
