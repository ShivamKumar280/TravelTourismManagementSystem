# Focused UI and booking-cancellation patch

This patch is based on the uploaded `TravelTourismManagementSystem1(1).zip` project. Existing packages, services, models, database schema, screens, and other frontend files were retained.

## Changes
- Login state: the sidebar is hidden before authentication; the login page expands across the available window. Successful login removes login mode and renders the correct customer/admin navigation.
- Booking cancellation: booking row is locked while cancelling; the booking status and package/hotel/transport inventory changes run in one transaction. Missing inventory records or failed updates roll back the cancellation. Duplicate cancellation is rejected.
- Customer UI: cancellation feedback is clearer and bookings reload only after the Java bridge reports success.

## Files changed
- `src/main/resources/web/app.js`
- `src/main/resources/web/styles.css`
- `src/main/java/com/travel/dao/BookingDAO.java`

## Verification
- `node --check src/main/resources/web/app.js`: passed.
- Maven compilation and live MySQL cancellation tests were not run in this environment. Please test against a backup or test database before using real booking data.
