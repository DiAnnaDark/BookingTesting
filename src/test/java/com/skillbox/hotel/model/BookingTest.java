package com.skillbox.hotel.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для модельного класса {@link Booking}.
 */
class BookingTest {

    private final Long BOOKING_ID = 1L;
    private final Long ROOM_ID = 101L;
    private final Long CUSTOMER_ID = 500L;
    private final LocalDate START_DATE = LocalDate.of(2023, 10, 20);
    private final LocalDate END_DATE = LocalDate.of(2023, 10, 25);

    @Test
    @DisplayName("Конструктор и геттеры должны корректно устанавливать значения")
    void constructorAndGetters_shouldReturnCorrectValues() {
        Booking booking = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        assertEquals(BOOKING_ID, booking.getBookingId());
        assertEquals(ROOM_ID, booking.getRoomId());
        assertEquals(CUSTOMER_ID, booking.getCustomerId());
        assertEquals(START_DATE, booking.getStartDate());
        assertEquals(END_DATE, booking.getEndDate());
    }

    @Test
    @DisplayName("Сеттеры должны изменять значения полей")
    void setters_shouldChangeValuesCorrectly() {
        Booking booking = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        Long newBookingId = 2L;
        Long newRoomId = 102L;
        Long newCustomerId = 501L;
        LocalDate newStartDate = LocalDate.of(2024, 1, 1);
        LocalDate newEndDate = LocalDate.of(2024, 1, 5);

        booking.setBookingId(newBookingId);
        booking.setRoomId(newRoomId);
        booking.setCustomerId(newCustomerId);
        booking.setStartDate(newStartDate);
        booking.setEndDate(newEndDate);

        assertEquals(newBookingId, booking.getBookingId());
        assertEquals(newRoomId, booking.getRoomId());
        assertEquals(newCustomerId, booking.getCustomerId());
        assertEquals(newStartDate, booking.getStartDate());
        assertEquals(newEndDate, booking.getEndDate());
    }

    @Test
    @DisplayName("equals(): одинаковые поля — объекты равны")
    void equals_sameFieldValues_shouldBeEqual() {
        Booking booking1 = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);
        Booking booking2 = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    @DisplayName("equals(): разные поля — объекты не равны")
    void equals_differentFieldValues_shouldNotBeEqual() {
        Booking booking1 = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);
        Booking booking2 = new Booking(2L, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        assertNotEquals(booking1, booking2);
    }

    @Test
    @DisplayName("equals(): сравнение с null должно возвращать false")
    void equals_withNull_shouldBeFalse() {
        Booking booking = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        assertNotEquals(null, booking);
    }

    @Test
    @DisplayName("equals(): сравнение с объектом другого типа должно возвращать false")
    void equals_withDifferentClass_shouldBeFalse() {
        Booking booking = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);

        assertNotEquals(booking, new Object());
    }

    @Test
    @DisplayName("toString() должен содержать значения всех полей")
    void toString_shouldContainAllFields() {
        Booking booking = new Booking(BOOKING_ID, ROOM_ID, CUSTOMER_ID, START_DATE, END_DATE);
        String s = booking.toString();

        assertNotNull(s);
        assertFalse(s.isEmpty());
        assertTrue(s.contains("bookingId=" + BOOKING_ID));
        assertTrue(s.contains("roomId=" + ROOM_ID));
        assertTrue(s.contains("customerId=" + CUSTOMER_ID));
        assertTrue(s.contains("startDate=" + START_DATE));
        assertTrue(s.contains("endDate=" + END_DATE));
    }
}
