package parking;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    @Test
    void constructorStoresDetailsAndStartsActive() {
        Vehicle vehicle = new Vehicle(1, VehicleType.CAR, 50.0);
        ParkingSlot slot = new ParkingSlot("L1", ParkingSlotType.LARGE);
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime end = start.plusHours(2);
        Booking booking = new Booking(3, vehicle, slot, start, end, 30.0);

        assertEquals(3, booking.getBookingId());
        assertSame(vehicle, booking.getVehicle());
        assertSame(slot, booking.getParkingSlot());
        assertEquals(start, booking.getStartTime());
        assertEquals(end, booking.getEndTime());
        assertEquals(30.0, booking.getAmount());
        assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus());
    }

    @Test
    void completingBookingSetsCompletedStatus() {
        Booking booking = createBooking();
        booking.completeBooking();
        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus());
    }

    @Test
    void cancellingBookingSetsCancelledStatus() {
        Booking booking = createBooking();
        booking.cancelBooking();
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }


// @Test
// void cancellingACompletedBooking() {
//     Booking booking = createBooking();
//     booking.completeBooking(); 
    
//     booking.cancelBooking();
//     assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), // ভ্যারিফাই ছাড়া ক্যান্সেল করলে বাগ !
//                  "System allowed a COMPLETED booking to be CANCELLED");
// }
// // Intentional Bug//

// @Test
// void bookingWithEqualStartAndEndTimeThrowsException() {
//     Vehicle vehicle = new Vehicle(1, VehicleType.CAR, 50.0);
//     ParkingSlot slot = new ParkingSlot("R1", ParkingSlotType.REGULAR);
//     LocalDateTime time = LocalDateTime.of(2026, 5, 1, 10, 0);
//     assertThrows(IllegalBookingTimeException.class, () -> {
//         new Booking(1, vehicle, slot, time, time, 10.0); //এখানে শুরু এর শেষ টাইম একই 
//     });
// }


// // Intentional Bug//




    private Booking createBooking() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
        return new Booking(1, new Vehicle(1, VehicleType.CAR, 50.0),
                new ParkingSlot("R1", ParkingSlotType.REGULAR), start, start.plusHours(1), 10.0);
    }
}
