# Focused fixes for TravelTourismManagementSystem1(2).zip

## Changes
- Admin Manage Bookings now shows a Cancel Booking action for non-cancelled records. It invokes the Java bridge and existing BookingService cancellation transaction, then refreshes the table.
- The customer cancellation flow remains connected to the existing `cancelMyBooking` bridge and BookingService transaction.
- Existing package, hotel, and destination CRUD forms/actions were preserved. Their delete operations use the existing authenticated `deleteAdminRecord` bridge and database foreign-key rules; related records may correctly prevent deletion.
- Manage Travel Routes now has an Add/Update form plus a separate All Travel Routes table with Edit and Delete actions.
- Added administrator-only bridge methods for route save/delete and admin booking cancellation.
- No database drop/recreate or schema migration was added.

## Modified files
- `src/main/java/com/travel/gui/WebDesktopFrame.java`
- `src/main/resources/web/app.js`
- `src/main/resources/web/styles.css`

## Verification
- `node --check src/main/resources/web/app.js`: PASS.
- Maven build, desktop runtime, and live MySQL CRUD/cancellation tests were not run in this environment. Run `mvn clean package` from the directory containing `pom.xml`, then verify actions against a backed-up/test database.
- Deleting packages/hotels/destinations/routes that are referenced by bookings can be rejected by MySQL foreign keys. This is intentional protection against orphaning booking history.
