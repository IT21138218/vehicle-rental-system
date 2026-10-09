# Vehicle Rental Service Platform

SE1020 Object Oriented Programming - individual project.

A Java web application for renting vehicles, built with **JSP and Servlets** (Jakarta Servlet 6,
Tomcat 10), **JSTL**, **Bootstrap 5** and **plain text files** for storage (no database).

> This README is a skeleton. It is completed in Phase 7 with full setup steps and every data file format.

## Modules

| # | Module | Data file | Status |
|---|--------|-----------|--------|
| 0 | Project setup (file layer, repository base) | - | Done |
| 1 | User Management, login/logout, role dashboards | `users.txt` | Planned |
| 2 | Vehicle Management | `vehicles.txt` | Planned |
| 3 | Rental Booking | `rentals.txt` | Planned |
| 4 | Payment and Billing | `payments.txt` | Planned |
| 5 | Feedback and Reviews | `reviews.txt` | Planned |
| 6 | Driver/Staff Management | `drivers.txt` | Planned |
| 7 | Documentation | `docs/` | Planned |

## Technology

- Java 17, Maven (WAR packaging)
- Jakarta Servlet 6.0, JSP, JSTL 3.0
- Apache Tomcat 10.1 or newer
- Bootstrap 5 (loaded from a CDN)

## Project structure

```
src/main/java/com/rental/
    model/        domain classes (User, Vehicle, Rental, Payment, Staff, Review ...)
    repository/   the ONLY code that reads/writes the data files
    service/      validation, ID generation, search and business rules
    servlet/      one servlet per action (Post-Redirect-Get)
    filter/       AuthFilter (login and role checks)
    util/         small helpers (IdGenerator ...)
src/main/webapp/  JSP pages, CSS, JavaScript
data/             sample data files (one record per line, comma-separated)
docs/             class diagram, viva notes, report outline
```

## Build

```
mvn clean package
```

This creates `target/vehicle-rental-system.war`.

## Where the data is stored

The app reads its data folder from the JVM option `-Drental.data.dir`:

- **Recommended:** in the IntelliJ Tomcat run configuration, set **VM options** to
  `-Drental.data.dir=<path to this project>\data` so you can open the files and watch them change.
- If the option is not set, the app uses `<your home folder>/vehicle-rental-data` and copies the
  sample files there on first run. Delete that folder to reset the demo data.

Missing files are created automatically. Blank or malformed lines are skipped with a warning in the log.

## Running in IntelliJ IDEA

_Full step-by-step instructions are added in Phase 7._

## Known limitations

- Passwords are stored in plain text (acceptable for this coursework; a real system would hash them).
