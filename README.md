# Travel & Tourism Management System

A desktop Travel & Tourism Management System built with **Java 26, Maven, Java Swing, JavaFX WebView, HTML, CSS, JavaScript, MySQL, and JDBC**.

The application launches a desktop window. Its shared HTML/CSS/JavaScript interface is embedded in JavaFX WebView hosted inside Swing; this is **not a browser-hosted website**.

## Features

Features represented in the source project include:

- Customer registration and login.
- Customer dashboard and navigation.
- Travel packages, hotels, and destinations.
- Transport options for bus, train, and flight.
- Booking creation and My Bookings.
- Payment and ticket/receipt screens.
- Admin dashboard and management screens for packages, hotels, destinations, transport/routes, bookings, and users.
- MySQL persistence through JDBC DAOs and Java service classes.
- HTML/CSS/JavaScript interface integrated with Java using a JavaScript-to-Java bridge.

**Important:** The presence of a screen or source file does not by itself prove every workflow is fully tested. See [Integration Notes](INTEGRATION_NOTES.md) for the project's recorded verification status and known limitations.

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 26 |
| Build tool | Apache Maven |
| Desktop UI | Java Swing |
| Embedded web UI | JavaFX WebView / JFXPanel |
| Frontend | HTML5, CSS3, vanilla JavaScript |
| Database | MySQL |
| Database access | JDBC / MySQL Connector/J |
| UI theme | FlatLaf |

## Project Structure

```text
TravelTourismManagementSystem/
├── database/
│   └── schema.sql
├── src/
│   └── main/
│       ├── java/com/travel/
│       │   ├── dao/          # Database access objects
│       │   ├── database/     # MySQL connection
│       │   ├── exception/    # Application exceptions
│       │   ├── gui/          # Swing screens and embedded web frame
│       │   ├── interfaces/   # Application interfaces
│       │   ├── model/        # Domain models
│       │   ├── service/      # Business logic
│       │   └── Main.java     # Application entry point
│       └── resources/
│           ├── images/       # UI images
│           └── web/          # HTML, CSS and JavaScript
├── pom.xml
├── INTEGRATION_NOTES.md
└── README.md
```

## Prerequisites

Install and configure:

1. **JDK 26** — the Maven configuration uses Java release 26.
2. **Apache Maven** compatible with your JDK.
3. **MySQL Server** and a MySQL account with permission to use the project database.
4. A desktop environment capable of displaying the JavaFX/Swing application.

Check the installations in PowerShell:

```powershell
java -version
javac -version
mvn -version
```

The Java version reported by `java`, `javac`, and Maven should be compatible with Java 26.

## Database Setup

The project database is named `travel_tourism`. The SQL schema is located at:

```text
database/schema.sql
```

### Important database warning

**`database/schema.sql` is a fresh-install script. It drops and recreates project tables and inserts sample data. Running it against an existing database can delete existing project data.**

Before running it:

1. Back up your database.
2. Confirm you intend to create/rebuild the sample database.
3. Run the script using MySQL Workbench or the MySQL command-line client.

Example command (adjust the path to your actual project folder):

```powershell
mysql -u root -p < database/schema.sql
```

Enter your MySQL password when prompted. Do not put the password directly into the command.

### Configure the database connection

Open:

```text
src/main/java/com/travel/database/DBConnection.java
```

Check the connection URL, MySQL username, and password for your local MySQL setup. The current source file contains machine-specific connection settings; update them for your own environment before running the application.

**Do not commit or share real database passwords.** For a shared or production deployment, move credentials to environment variables or a local configuration file excluded from version control.

## Build and Run on Windows

Open PowerShell and change to the folder that contains `pom.xml`:

```powershell
cd "C:\path\to\TravelTourismManagementSystem"
```

Build the project:

```powershell
mvn clean package
```

Start the desktop application:

```powershell
mvn exec:java
```

The Maven `exec` configuration starts `com.travel.Main`, which opens the desktop frame containing the embedded web interface.

If the build fails, check that:

- You are in the folder containing `pom.xml`.
- JDK 26 is installed and selected in `JAVA_HOME`/`PATH`.
- `mvn -version` reports the expected Java version.
- MySQL is running and the database connection settings are correct.
- Maven can download the declared dependencies.

## Main Source Locations

| Purpose | Location |
|---|---|
| Application entry point | `src/main/java/com/travel/Main.java` |
| Embedded HTML interface | `src/main/java/com/travel/gui/WebDesktopFrame.java` |
| Database connection | `src/main/java/com/travel/database/DBConnection.java` |
| SQL schema/sample data | `database/schema.sql` |
| HTML page | `src/main/resources/web/index.html` |
| Frontend behavior | `src/main/resources/web/app.js` |
| Styles | `src/main/resources/web/styles.css` |
| Project dependencies/build | `pom.xml` |
| Recorded integration status | `INTEGRATION_NOTES.md` |

## Login / Sample Accounts

The fresh-install SQL script defines these sample accounts:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Customer | `customer` | `1234` |

These accounts are inserted only when the fresh-install schema script is run. They may not exist in a database that was set up differently. Change sample credentials before any real deployment. Do not use these demo passwords for production.

## Verification Status and Limitations

Consult `INTEGRATION_NOTES.md` for the detailed status recorded with this project.

That note states that JavaScript syntax and referenced dashboard image assets were checked, while full Maven compilation, JavaFX desktop startup, and complete database-backed end-to-end workflows were not verified in the environment where the notes were written. It also notes that live transport-provider connectivity and real payment-gateway processing are not established by the project notes.

Run the project in your own configured JDK 26 + MySQL environment and test each customer/admin workflow before relying on it as a fully verified application.

## Security Notes

- Keep database credentials private.
- Do not use sample credentials in a deployed application.
- Validate authentication and authorization on the backend.
- Never treat a UI-only success message as proof that a booking or payment was actually processed.
- Back up your database before running SQL scripts that modify or delete tables.

## License

No license information was identified in the project documentation included with this ZIP. Add a license file if you plan to distribute the project.
