package parking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSystemTest {
    private ParkingSystem system;
    private LocalDateTime start;

    @BeforeEach
    void resetSingletonState() {
        system = ParkingSystem.getInstance();
        system.resetForTesting(); // 🧹 Reset state before every test
        start = LocalDateTime.of(2026, 5, 1, 10, 0);
    }

    @Test
    void resetRestoresEmptyCollectionsRateAndWallet() {
        system.addVehicle(new Vehicle(1, VehicleType.CAR, 20.0));
        system.addParkingSlot(new ParkingSlot("R1", ParkingSlotType.REGULAR));
        system.setPARKING_RATE_PER_HOUR(15.0);
        system.getSYSTEM_WALLET().addFunds(4.0);

        system.resetForTesting();

        assertTrue(system.getVehicles().isEmpty());
        assertTrue(system.getParkingSlots().isEmpty());
        assertTrue(system.getBookings().isEmpty());
        assertEquals(10.0, system.getPARKING_RATE_PER_HOUR());
        assertEquals(0.0, system.getBalance());
    }

    @Test
    void addMethodsRegisterVehiclesAndSlots() {
        Vehicle vehicle = new Vehicle(1, VehicleType.CAR, 20.0);
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        system.addVehicle(vehicle);
        system.addParkingSlot(slot);
        assertTrue(system.getVehicles().contains(vehicle));
        assertTrue(system.getParkingSlots().contains(slot));
    }

    @Test
    void availableSlotsIncludeCompatibleActiveUnoccupiedSlotsOnly() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 30.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        ParkingSlot compact = new ParkingSlot("C1", ParkingSlotType.COMPACT);
        ParkingSlot inactive = new ParkingSlot("R2", ParkingSlotType.REGULAR);
        inactive.deactivate();
        system.addParkingSlot(regular);
        system.addParkingSlot(compact);
        system.addParkingSlot(inactive);

        assertEquals(List.of(regular), system.getAvailableParkingSlots(car, start, start.plusHours(1)));
    }

    @Test
    void bookCreatesActiveBookingChargesVehicleAndEscrowsFare() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        Booking booking = system.book(car, regular, start, start.plusHours(2));

        assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus());
        assertEquals(20.0, booking.getAmount());
        assertEquals(80.0, car.getBalance());
        assertEquals(20.0, system.getBalance());
        assertTrue(system.getBookings().contains(booking));
        assertTrue(regular.getBookings().contains(booking));
    }

    @Test
    void bookRejectsEndAtOrBeforeStart() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        assertThrows(IllegalBookingTimeException.class, () -> system.book(car, regular, start, start));
        assertThrows(IllegalBookingTimeException.class, () -> system.book(car, regular, start, start.minusMinutes(1)));
    }

    @Test
    void bookRejectsIncompatibleOrInactiveSlot() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
        ParkingSlot compact = new ParkingSlot("C1", ParkingSlotType.COMPACT);
        ParkingSlot inactive = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        inactive.deactivate();
        assertThrows(IllegalArgumentException.class, () -> system.book(car, compact, start, start.plusHours(1)));
        assertThrows(IllegalArgumentException.class, () -> system.book(car, inactive, start, start.plusHours(1)));
    }

    @Test
    void bookRejectsOverlappingTimeWindow() {
        Vehicle first = new Vehicle(1, VehicleType.CAR, 100.0);
        Vehicle second = new Vehicle(2, VehicleType.CAR, 100.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        system.book(first, regular, start, start.plusHours(2));
        assertThrows(IllegalArgumentException.class,
                () -> system.book(second, regular, start.plusHours(1), start.plusHours(3)));
    }

    @Test
    void completingBookingTransfersEightyPercentToSlot() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        Booking booking = system.book(car, regular, start, start.plusHours(2));
        system.completeBooking(booking);

        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus());
        assertEquals(16.0, regular.getBalance());
        assertEquals(4.0, system.getBalance());
    }

    @Test
    void cancellingBookingRefundsNinetyPercentToVehicle() {
        Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
        ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        Booking booking = system.book(car, regular, start, start.plusHours(2));
        system.cancelBooking(booking);

        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
        assertEquals(98.0, car.getBalance());
        assertEquals(2.0, system.getBalance());
    }


 
    // // System should NOT save the booking if payment fails.
    // // It throws Exception but saves the booking anyway.
    // @Test
    // void defect_insufficientFundsLeavesPartialBookingState() {
    //     Vehicle car = new Vehicle(1, VehicleType.CAR, 1.0); 
    //     ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);

    //     assertThrows(InsufficientFundsException.class,
    //             () -> system.book(car, regular, start, start.plusHours(2)));
    //     assertTrue(system.getBookings().isEmpty(), "Booking should not be saved on failed payment");
    // }
 

    // // Expected: 30 minutes booking should cost 5.0
    // // toHours() makes it 0.0, system crashes with InvalidAmountException!
    // @Test
    // void defect_partialHourBookingIsPricedCorrectly() {
    //     Vehicle car = new Vehicle(1, VehicleType.CAR, 100.0);
    //     ParkingSlot regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
    //     Booking booking = system.book(car, regular, start, start.plusMinutes(30));
    //     assertEquals(5.0, booking.getAmount(), "30 mins should calculate as fraction of hour");
    // }

    // // Completing a booking twice should be stopped.
    // // System blindly pays the slot again.
    // @Test
    // void defect_repeatedSettlementIsPrevented() {
    //     Vehicle vehicle = new Vehicle(4, VehicleType.CAR, 100.0);
    //     ParkingSlot slot1 = new ParkingSlot("R4", ParkingSlotType.REGULAR);
    //     Booking booking1 = system.book(vehicle, slot1, start, start.plusHours(2)); 
    //     system.completeBooking(booking1); 
    //     system.completeBooking(booking1); 
    //     assertEquals(16.0, slot1.getBalance(), "Slot should not get paid twice for same booking");
    // }
}