# Focused fixes: confirmation actions and route form layout

## Root cause fixed
The shared HTML UI uses JavaScript `confirm()` before cancelling bookings and deleting records. The embedded JavaFX WebView did not have a confirm handler configured, so confirmation prompts could default to cancellation and the Java backend methods were never called. `WebDesktopFrame` now displays a JavaFX confirmation dialog and returns the user's actual choice.

## UI fix
The Manage Travel Routes editor has a wider left column and its heading/form fields now flow vertically rather than being squeezed into the generic CRUD form grid.

## Data integrity
- Booking cancellation delegates to the existing `BookingService` / `BookingDAO` transaction, which restores package seats, hotel rooms, and transport seats when those inventory records exist.
- Delete actions call the existing Java backend. Records referenced by booking history may still be rejected by database foreign-key constraints; this protects historical records rather than deleting bookings silently.
- No schema reset or destructive migration was added.

## Build and verification
Build and live database tests must be run on the user's Windows setup with the configured JDK 26, Maven, and MySQL database.
