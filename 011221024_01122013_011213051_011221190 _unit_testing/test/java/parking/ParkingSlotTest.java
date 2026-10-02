package parking;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSlotTest {
    private final LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
    private final LocalDateTime end = start.plusHours(1);

    @Test
    void EmptyBookingsAndWalletwhileActive() {
        ParkingSlot slot = new ParkingSlot("C1", ParkingSlotType.COMPACT);
        assertEquals("C1", slot.getSlotId());
        assertEquals(ParkingSlotType.COMPACT, slot.getSlotType());
        assertTrue(slot.isActive());
        assertTrue(slot.getBookings().isEmpty());
        assertEquals(0.0, slot.getBalance());
    }

    @Test
    void activationAndDeactivationChangeActiveState() {
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        slot.deactivate();
        assertFalse(slot.isActive());
        slot.activate();
        assertTrue(slot.isActive());
    }

    @Test
    void compatibilityFollowsDocumentedVehicleAndSlotMatrix() {
        assertTrue(new ParkingSlot("1", ParkingSlotType.COMPACT).isCompatible(VehicleType.MOTORCYCLE, start, end));
        assertFalse(new ParkingSlot("2", ParkingSlotType.COMPACT).isCompatible(VehicleType.CAR, start, end));
        assertTrue(new ParkingSlot("3", ParkingSlotType.LARGE).isCompatible(VehicleType.BUS, start, end));
        assertTrue(new ParkingSlot("4", ParkingSlotType.HANDICAPPED).isCompatible(VehicleType.BICYCLE, start, end));
        assertFalse(new ParkingSlot("5", ParkingSlotType.LARGE).isCompatible(VehicleType.MICROCAR, start, end));
    }

    @Test
    void inactiveSlotIsNotCompatible() {
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        slot.deactivate();
        assertFalse(slot.isCompatible(VehicleType.CAR, start, end));
    }

    @Test
    void emptySlotIsAvailable() {
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        assertTrue(slot.isAvailable(start, end));
    }

    @Test
    void overlappingBooking() {
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        slot.getBookings().add(new Booking(1, new Vehicle(1, VehicleType.CAR, 10.0), slot,
                start.plusMinutes(30), end.plusMinutes(30), 10.0));
        assertFalse(slot.isAvailable(start, end));
        assertFalse(slot.isCompatible(VehicleType.CAR, start, end));
    }

    @Test
    void backToBackBookings() {
        ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        slot.getBookings().add(new Booking(1, new Vehicle(1, VehicleType.CAR, 10.0), slot,
                start, end, 10.0));
        assertTrue(slot.isAvailable(end, end.plusHours(1)));
    }

    @Test
    void defect_truckIsNeverCompatibleWithAnySlot() {
        ParkingSlot large = new ParkingSlot("L1", ParkingSlotType.LARGE);
        assertFalse(large.isCompatible(VehicleType.TRUCK, start, end),
                "TRUCK is completely missing in the statement.");
    }
}
