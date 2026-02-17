package com.skillbox.hotel.service;

import com.skillbox.hotel.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class RoomServiceTest {

    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomService();
    }

    // addRoom

    @Test
    @DisplayName("Добавление корректного номера")
    void addRoom_validRoom_shouldAdd() {
        Room room = new Room(1L, "Standard", 100.0, true);

        roomService.addRoom(room);

        List<Room> allRooms = roomService.getAllRooms();
        assertEquals(1, allRooms.size());
        assertTrue(allRooms.contains(room));
    }

    @Test
    @DisplayName("Попытка добавить null не изменяет список")
    void addRoom_null_shouldNotAdd() {
        roomService.addRoom(null);

        assertTrue(roomService.getAllRooms().isEmpty());
    }

    // getAvailableRooms

    @Test
    @DisplayName("Фильтрация по кастомному фильтру — работает корректно")
    void getAvailableRooms_customFilter_shouldReturnMatching() {
        Room room1 = new Room(1L, "Standard", 100, true);
        Room room2 = new Room(2L, "Deluxe", 200, false);
        Room room3 = new Room(3L, "Standard", 300, true);

        roomService.addRoom(room1);
        roomService.addRoom(room2);
        roomService.addRoom(room3);

        Predicate<Room> standardFilter = r -> "Standard".equals(r.getType());

        List<Room> result = roomService.getAvailableRooms(standardFilter);

        assertEquals(2, result.size());
        assertTrue(result.contains(room1));
        assertTrue(result.contains(room3));
        assertFalse(result.contains(room2));
    }

    @Test
    @DisplayName("Фильтр, который не подходит ни одному номеру → пустой список")
    void getAvailableRooms_noMatch_shouldReturnEmpty() {
        Room room = new Room(1L, "Standard", 100, true);
        roomService.addRoom(room);

        Predicate<Room> suiteOnly = r -> "Suite".equals(r.getType());

        List<Room> result = roomService.getAvailableRooms(suiteOnly);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Передан null-фильтр → пустой список")
    void getAvailableRooms_nullFilter_shouldReturnEmpty() {
        Room room = new Room(1L, "Standard", 100, true);
        roomService.addRoom(room);

        List<Room> result = roomService.getAvailableRooms(null);

        assertTrue(result.isEmpty());
    }

    // findRoomById

    @Test
    @DisplayName("Поиск существующего номера возвращает Optional с номером")
    void findRoomById_existing_shouldReturnRoom() {
        Room room = new Room(1L, "Standard", 100, true);
        roomService.addRoom(room);

        Optional<Room> found = roomService.findRoomById(1L);

        assertTrue(found.isPresent());
        assertEquals(room, found.get());
    }

    @Test
    @DisplayName("Поиск несуществующего ID → пустой Optional")
    void findRoomById_nonExisting_shouldReturnEmpty() {
        assertTrue(roomService.findRoomById(99L).isEmpty());
    }

    @Test
    @DisplayName("Поиск с null ID → пустой Optional")
    void findRoomById_nullId_shouldReturnEmpty() {
        assertTrue(roomService.findRoomById(null).isEmpty());
    }

    // updateRoomAvailability

    @Test
    @DisplayName("Обновление доступности существующего номера")
    void updateRoomAvailability_existing_shouldUpdate() {
        Room room = new Room(1L, "Standard", 100, true);
        roomService.addRoom(room);

        roomService.updateRoomAvailability(1L, false);

        assertFalse(roomService.findRoomById(1L).get().isAvailable());
    }

    @Test
    @DisplayName("Обновление доступности несуществующего номера не вызывает исключений")
    void updateRoomAvailability_nonExisting_shouldNotThrow() {
        assertDoesNotThrow(() -> roomService.updateRoomAvailability(999L, false));
    }

    // getAllRooms

    @Test
    @DisplayName("Пустой сервис → возвращается пустой список")
    void getAllRooms_empty_shouldReturnEmpty() {
        assertTrue(roomService.getAllRooms().isEmpty());
    }

    @Test
    @DisplayName("Список содержит все добавленные номера")
    void getAllRooms_shouldReturnAll() {
        Room room1 = new Room(1L, "Standard", 100, true);
        Room room2 = new Room(2L, "Deluxe", 200, false);

        roomService.addRoom(room1);
        roomService.addRoom(room2);

        List<Room> rooms = roomService.getAllRooms();

        assertEquals(2, rooms.size());
        assertTrue(rooms.contains(room1));
        assertTrue(rooms.contains(room2));
    }

    @Test
    @DisplayName("Список неизменяемый")
    void getAllRooms_shouldReturnImmutable() {
        Room room = new Room(1L, "Standard", 100, true);
        roomService.addRoom(room);

        List<Room> rooms = roomService.getAllRooms();

        assertThrows(UnsupportedOperationException.class, () ->
                rooms.add(new Room(2L, "Suite", 500, true))
        );
    }
}
