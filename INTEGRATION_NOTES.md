# Travel & Tourism Management System — Frontend Integration Notes

## Architecture found in this project

- Java 26 / Maven project with an existing Java Swing application.
- MySQL access is implemented through JDBC DAOs and the existing service layer.
- The shared interface is HTML/CSS/vanilla JavaScript in `src/main/resources/web/`.
- `WebDesktopFrame` embeds that interface in JavaFX WebView hosted by Swing (`JFXPanel`).
- The JavaScript-to-Java bridge delegates login, listing reads, booking cancellation, and ticket opening to existing Java services/DAOs or existing Swing screens. The browser does not connect to MySQL directly.
- Customer booking, payment, ticket, and admin CRUD workflows continue to use their existing Java screens where those workflows have not been migrated to the shared HTML interface.

## Changes in this integration pass

1. `Main.java` now starts `WebDesktopFrame`, so the integrated HTML/CSS UI is the actual application entry point rather than an unused resource.
2. Admin navigation for Packages, Hotels, and Destinations now opens the existing admin CRUD frames. Customer navigation continues to show database-backed web listings.
3. Customer dashboard cards now reference image assets that exist in `src/main/resources/images/`, and the dashboard has six cards. The Tickets & Receipts card opens My Bookings, where the existing ticket action is available.
4. Generated Maven `target/` output was excluded from the delivered source ZIP; it can be rebuilt locally.

## Windows setup and launch

1. Install/configure JDK 26 and Maven, then verify:
   ```powershell
   java -version
   javac -version
   mvn -version
   ```
2. Configure MySQL and ensure the database schema in `database/schema.sql` has been applied to the intended database. **Back up the database before applying any schema script.** Do not drop or recreate a database containing data.
3. Check `src/main/java/com/travel/database/DBConnection.java` and set the local connection URL, username, and password for your own MySQL setup. Do not commit real credentials.
4. Open PowerShell in the folder containing `pom.xml`, then run:
   ```powershell
   mvn clean package
   mvn exec:java
   ```
5. The application opens as a desktop window with the embedded HTML interface; it is not a browser-hosted HTTP website.

## Verification status and limits

- `node --check src/main/resources/web/app.js`: passed in the available environment.
- Referenced dashboard image assets: checked and all referenced assets exist.
- Full Maven compilation, JavaFX desktop startup, database-backed workflow tests, and visual UI testing were **not run** in the available environment because Maven is not installed here and the available JDK is 21 while this project is configured for Java 26.
- This integration pass does not claim live bus/train/flight provider connectivity or real payment-gateway processing. The existing application logic remains authoritative for booking/payment behavior.
- The current registration and other legacy screens remain Java Swing windows. They were retained to avoid replacing working business logic with unverified web forms.


## Follow-up HTML screen migration

- Navigation actions for packages, hotels, destinations, routes, users, admin bookings, customer booking creation, customer bookings and ticket display now stay inside the embedded HTML/CSS interface rather than opening the corresponding legacy Swing module frames.
- Added HTML booking form connected to `BookingService.createBooking`, using Java service-side validation and total calculation.
- Added HTML tables for admin routes, users and bookings, sourced from the database.
- Added admin HTML create/update/delete forms for packages, hotels and destinations. Delete may correctly fail when foreign-key references exist.
- Added HTML ticket rendering from the signed-in customer's saved booking data.
- Added `getTransportOptions`, `saveAdminRecord`, and `deleteAdminRecord` bridge methods.

Verification: `node --check src/main/resources/web/app.js` passed after these changes. Full Maven compilation and runtime/database tests were not run in this environment; do not treat the project as fully verified until `mvn clean package` and the workflows are tested on the configured JDK 26 + MySQL environment. User management, route editing, admin booking actions, and payment processing still need end-to-end verification/implementation as applicable.
