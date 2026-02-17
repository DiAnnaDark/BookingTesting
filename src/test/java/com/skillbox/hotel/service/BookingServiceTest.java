package com.skillbox.hotel.service;

import com.skillbox.hotel.model.Booking;
import com.skillbox.hotel.model.Room;
import com.skillbox.hotel.service.external.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    // param проверяет нулевые значения

    @ParameterizedTest
    @MethodSource("provideNullParamsForCreateBooking")
    @DisplayName("Создание бронирования с null-полями → IllegalArgumentException")
    void createBooking_nullParams_shouldThrowException(
            Long bookingId, Long roomId, Long customerId, LocalDate start, LocalDate end
    ) {
        assertThatThrownBy(() -> bookingService.createBooking(bookingId, roomId, customerId, start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Недопустимые параметры бронирования");

        verify(roomService, never()).findRoomById(any());
        verify(notificationService, never()).sendNotification(any(), any());
    }

    static Stream<Arguments> provideNullParamsForCreateBooking() {
        return Stream.of(
                Arguments.of(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1)),
                Arguments.of(1L, null, 1L, LocalDate.now(), LocalDate.now().plusDays(1)),
                Arguments.of(1L, 1L, null, LocalDate.now(), LocalDate.now().plusDays(1)),
                Arguments.of(1L, 1L, 1L, null, LocalDate.now().plusDays(1)),
                Arguments.of(1L, 1L, 1L, LocalDate.now(), null)
        );
    }

    // createBooking тесты

    @Test
    @DisplayName("Успешное создание бронирования: номер доступен, уведомление отправлено")
    void createBooking_success() {
        Long bookingId = 1L;
        Long roomId = 10L;
        Long customerId = 100L;
        LocalDate start = LocalDate.of(2023, 11, 1);
        LocalDate end = LocalDate.of(2023, 11, 5);

        Room availableRoom = new Room(roomId, "Standard", 120, true);
        when(roomService.findRoomById(roomId)).thenReturn(Optional.of(availableRoom));

        Booking booking = bookingService.createBooking(bookingId, roomId, customerId, start, end);

        assertThat(booking)
                .isNotNull()
                .extracting(Booking::getBookingId, Booking::getRoomId, Booking::getCustomerId)
                .containsExactly(bookingId, roomId, customerId);

        verify(roomService).updateRoomAvailability(roomId, false);
        verify(notificationService).sendNotification(eq(customerId), contains("подтверждено"));

        assertThat(bookingService.getAllBookings())
                .hasSize(1)
                .contains(booking);
    }

    @Test
    @DisplayName("Дата начала позже даты конца → IllegalArgumentException")
    void createBooking_wrongDates_shouldThrow() {
        assertThatThrownBy(() ->
                bookingService.createBooking(1L, 2L, 3L,
                        LocalDate.of(2023, 10, 20),
                        LocalDate.of(2023, 10, 10))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Дата начала бронирования должна быть раньше даты окончания");

        verify(roomService, never()).findRoomById(anyLong());
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    @DisplayName("Номер не найден → IllegalArgumentException")
    void createBooking_roomNotFound() {
        when(roomService.findRoomById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.createBooking(1L, 100L, 10L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("недоступен");

        verify(roomService).findRoomById(100L);
        verify(roomService, never()).updateRoomAvailability(anyLong(), anyBoolean());
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    @DisplayName("Номер найден, но недоступен → IllegalArgumentException")
    void createBooking_roomUnavailable() {
        Room room = new Room(5L, "Standard", 100, false);
        when(roomService.findRoomById(5L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() ->
                bookingService.createBooking(1L, 5L, 20L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(2))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("недоступен");

        verify(roomService).findRoomById(5L);
        verify(notificationService, never()).sendNotification(any(), any());
    }


    // cancelBooking тесты

    @Test
    @DisplayName("Отмена существующей брони → номер становится свободным, уведомление отправлено")
    void cancelBooking_success() {
        Booking booking = new Booking(1L, 10L, 100L,
                LocalDate.now(), LocalDate.now().plusDays(3));

        // Добавляем бронь вручную
        bookingService.getAllBookings();
        // Доступ через рефлексию не нужен — можно через createBooking, но проще мокнуть
        // Делаем через createBooking, чтобы список реально содержал бронь
        when(roomService.findRoomById(10L)).thenReturn(Optional.of(new Room(10L, "Deluxe", 150, true)));
        bookingService.createBooking(
                booking.getBookingId(),
                booking.getRoomId(),
                booking.getCustomerId(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        bookingService.cancelBooking(1L);

        verify(roomService).updateRoomAvailability(10L, true);
        verify(notificationService).sendNotification(eq(100L), contains("отменено"));

        assertThat(bookingService.getAllBookings()).isEmpty();
    }

    @Test
    @DisplayName("Отмена несуществующей брони → IllegalArgumentException")
    void cancelBooking_notFound() {
        assertThatThrownBy(() -> bookingService.cancelBooking(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Бронирование с ID 999 не найдено.");

        verify(roomService, never()).updateRoomAvailability(anyLong(), anyBoolean());
        verify(notificationService, never()).sendNotification(any(), any());
    }

    // immutability тесты

    @Test
    @DisplayName("getAllBookings возвращает неизменяемый список")
    void getAllBookings_shouldReturnImmutableList() {
        when(roomService.findRoomById(1L))
                .thenReturn(Optional.of(new Room(1L, "Standard", 110, true)));

        bookingService.createBooking(
                10L, 1L, 2L,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        List<Booking> bookings = bookingService.getAllBookings();

        assertThatThrownBy(() -> bookings.add(
                new Booking(999L, 1L, 1L, LocalDate.now(), LocalDate.now())))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
