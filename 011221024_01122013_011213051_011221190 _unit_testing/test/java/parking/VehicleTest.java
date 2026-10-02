package parking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void constructorWithWalletKeepsIdentityAndWallet() {
        Wallet wallet = new Wallet(25.0);
        Vehicle vehicle = new Vehicle(17, VehicleType.CAR, wallet);

        assertEquals(17, vehicle.getVehicleId());
        assertEquals(VehicleType.CAR, vehicle.getVehicleType());
        assertSame(wallet, vehicle.getWallet());
        assertEquals(25.0, vehicle.getBalance());
    }

    @Test
    void constructorWithBalanceCreatesWalletWithThatBalance() {
        Vehicle vehicle = new Vehicle(4, VehicleType.BICYCLE, 12.5);

        assertEquals(4, vehicle.getVehicleId());
        assertEquals(VehicleType.BICYCLE, vehicle.getVehicleType());
        assertEquals(12.5, vehicle.getBalance());
    }

    @Test
    void balanceReflectsLaterWalletChanges() {
        Vehicle vehicle = new Vehicle(4, VehicleType.CAR, 10.0);
        vehicle.getWallet().addFunds(5.0);

        assertEquals(15.0, vehicle.getBalance());
    }

    @Test
    void toStringReturnsCorrectFormattedString() {
        Vehicle vehicle = new Vehicle(3, VehicleType.BUS, 200.0);
        
        String expectedString = "Vehicle{vehicleId=3, vehicleType=BUS, walletBalance=200.0}";
        assertEquals(expectedString, vehicle.toString(), "toString() format must match exactly.");
    }
}