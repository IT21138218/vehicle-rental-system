# Report Outline: RentRide Vehicle Rental Service Platform

SE1020 Object Oriented Programming, individual project. Use this as the skeleton of the final
report. Replace every *[bracketed note]* with your own text.

---

## Cover page
- Project title: *RentRide: Vehicle Rental Service Platform*
- Module: SE1020 Object Oriented Programming
- Student name, registration number, batch / group
- Lecturer / supervisor
- Submission date
- GitHub repository: https://github.com/IT21138218/vehicle-rental-system

## Table of contents

---

## 1. Introduction
1.1 **Background:** why vehicle rental shops need a simple digital booking system *[2-3 sentences]*.
1.2 **Problem statement:** manual bookings cause double bookings, lost bills and no feedback history.
1.3 **Objectives**
- Manage users, vehicles, bookings, bills, reviews and staff with full CRUD
- Prevent overlapping bookings automatically
- Apply object-oriented principles clearly (encapsulation, inheritance, polymorphism, abstraction)
- Store all data in plain text files without a database

1.4 **Scope:** the six modules, the two roles (Admin, Customer), and what is out of scope (online card processing, e-mail notifications).
1.5 **Technologies:** Java 17, Jakarta Servlet 6 / JSP / JSTL 3, Apache Tomcat 10.1, Maven, Bootstrap 5, IntelliJ IDEA, Git/GitHub.

## 2. Requirements
2.1 **Functional requirements:** one table per module (Create / Read / Update / Delete + business rules).
*[Copy from VIVA_NOTES.md section 4.]*

2.2 **Non-functional requirements**
- Usability: responsive Bootstrap UI, friendly error messages, confirmation before deletes
- Reliability: safe writes (temp file + atomic move), malformed lines skipped
- Security: login required, role-based access through `AuthFilter`, output escaping
- Maintainability: layered architecture, one repository pattern for every entity

2.3 **User roles:** a table of what Admin and Customer can do.

## 3. System design
3.1 **Architecture:** the layered diagram (Browser → Filter → Servlet → Service → Repository → FileHandler → files) and one paragraph per layer.

3.2 **Class diagram**
- Insert *Diagram 1: Domain model* and *Diagram 2: Layers*, rendered from `docs/class-diagram.puml`
  (IntelliJ PlantUML Integration plugin → right-click → *Export to PNG*).
- One paragraph describing each inheritance hierarchy.

3.3 **OOP concepts applied:** one sub-section each for Encapsulation, Inheritance, Polymorphism and Abstraction. Include a short code snippet and the class/method name for each (see VIVA_NOTES.md section 2).

3.4 **Data file design:** a table of every file with its line format and one example line (see README.md, *Data files*). Explain why the review comment is the last field.

3.5 **File handling design:** the read and write sequence (VIVA_NOTES.md section 3), plus a simple sequence diagram for "Add vehicle".

3.6 **Business rules:** overlap check, pricing rules, billing rules, verified reviews, delete restrictions.

## 4. Implementation
4.1 **Project structure:** the folder tree (copy from README.md).
4.2 **Key classes:** short explanations with code snippets for:
- `AbstractFileRepository.findAll()` and `FileHandler.writeLines()`
- `Vehicle.create()` (factory) and `Car.calculateDailyRate()` (polymorphism)
- `RentalService.checkNoOverlap()` and `Rental.overlaps()`
- `CardPayment.calculateTotal()`
- `AuthFilter.doFilter()`

4.3 **Web layer:** servlet URL mapping table, Post-Redirect-Get, flash messages, shared JSP includes.
4.4 **Validation:** client side (HTML5 + `app.js`) vs server side (setters + services).

## 5. User interface (screenshots to take)
Take each screenshot at about 1366 px wide. Add 1-2 sentences under each.

| # | Screen | How to get there |
|---|---|---|
| 1 | Login page | `/login` |
| 2 | Registration with a validation error | Register → mismatched passwords |
| 3 | Admin dashboard | admin / admin123 |
| 4 | Customer dashboard | nimal / pass123 |
| 5 | 403 Access denied | as customer open `/admin/users` |
| 6 | User list with search | Users → search "perera" |
| 7 | Edit user / profile | Users → Edit, and My profile |
| 8 | Vehicle fleet with filters | Vehicles → Type = Bike |
| 9 | Add vehicle form (Van selected, cargo label) | Vehicles → Add vehicle |
| 10 | Server-side validation error (rate 0) | Add vehicle with rate 0 |
| 11 | Delete confirmation page | any Delete button |
| 12 | Customer browse cards with star ratings | Browse vehicles |
| 13 | Vehicle details with booked dates and reviews | Browse → Toyota Corolla |
| 14 | Booking form with live cost estimate | Book this vehicle |
| 15 | Overlap rejected message | book V001 22-24 Oct |
| 16 | My rentals list with status tabs | My rentals |
| 17 | Rental details with "Mark returned" (admin) | Rentals → R010 |
| 18 | Payments list with totals | Payments (admin) |
| 19 | Card bill showing surcharge | Payments → P002 |
| 20 | Generate bill form | Payments → Generate bill |
| 21 | Reviews with Verified / Public badges | Corolla → All reviews |
| 22 | Write review form | Write a review |
| 23 | Staff list with monthly pay | Staff |
| 24 | Add staff form (driver licence) | Staff → Add |
| 25 | A data file before and after an edit | `data/vehicles.txt` in IntelliJ |
| 26 | Tomcat log showing a skipped malformed line | IntelliJ *Run* window |

## 6. Testing
6.1 **Approach:** manual functional testing of every CRUD operation and business rule, plus
negative tests (invalid input, wrong role, malformed file lines).

6.2 **Test cases**

| ID | Module | Test case | Input / steps | Expected result | Actual result | Pass/Fail |
|---|---|---|---|---|---|---|
| TC01 | Auth | Valid admin login | admin / admin123 | Admin dashboard | | |
| TC02 | Auth | Wrong password | admin / wrong | "Invalid username or password" | | |
| TC03 | Auth | Customer opens admin page | nimal → `/admin/users` | 403 Access denied | | |
| TC04 | Auth | Guest opens a protected page | `/rentals` logged out | Redirect to login | | |
| TC05 | Users | Register new customer | valid form | Saved as next U-id, redirect to login | | |
| TC06 | Users | Duplicate username | username "kasun" | "already taken" | | |
| TC07 | Users | Name with a comma | "Bad, Name" | "cannot contain commas" | | |
| TC08 | Users | Delete own account | admin deletes U001 | Refused | | |
| TC09 | Users | Delete customer with active booking | delete U004 | Refused (R008) | | |
| TC10 | Vehicles | Add van | Van, 1500 kg | V013 saved, daily rate = base + 3000 | | |
| TC11 | Vehicles | Rate = 0 | rate 0 | "Daily rate must be greater than 0" | | |
| TC12 | Vehicles | Car with 12 seats | seats 12 | "Seats must be between 2 and 9" | | |
| TC13 | Vehicles | Filter Bike + Unavailable | filters | Only Ninja 650 | | |
| TC14 | Vehicles | Polymorphic rate | view Montero | 17,600 (16,000 + 10%) | | |
| TC15 | Vehicles | Delete vehicle with active booking | delete V001 | Refused | | |
| TC16 | Rentals | Overlapping booking | V001 22-24 Oct | "already booked" | | |
| TC17 | Rentals | Back-to-back booking | V001 25-27 Oct | Accepted | | |
| TC18 | Rentals | Start date in the past | yesterday | "cannot be in the past" | | |
| TC19 | Rentals | More than 30 days | 40 days | "at most 30 days" | | |
| TC20 | Rentals | Cancel another customer's booking | nimal → R008 | Refused | | |
| TC21 | Rentals | Return a future rental | admin → R012 | "has not started yet" | | |
| TC22 | Payments | Card bill total | view P002 | 12,669.00 | | |
| TC23 | Payments | Generate bill | R012, card | Base 70,400, total 72,512 | | |
| TC24 | Payments | Second bill for same rental | R012 again | "already has bill" | | |
| TC25 | Payments | Bill a cancelled rental | R007 | Refused | | |
| TC26 | Payments | Invalid card digits | "12ab" | "exactly 4 numbers" | | |
| TC27 | Reviews | Comment with commas | "Good, clean, fast" | Saved and shown intact | | |
| TC28 | Reviews | Second review same vehicle | RV001 owner again | Redirected to edit | | |
| TC29 | Reviews | Verified vs public | returned vs not returned | Correct badge | | |
| TC30 | Reviews | Edit another's review | nimal → RV002 | Refused | | |
| TC31 | Staff | Add driver | licence b7654321 | Saved as B7654321 | | |
| TC32 | Staff | Duplicate phone | 0771234567 | "already used by Kamal Silva" | | |
| TC33 | Staff | Monthly pay | Kamal Silva | 88,000.00 | | |
| TC34 | Files | Malformed line | add a bad line | Line skipped, warning logged, page works | | |
| TC35 | Files | Missing data file | delete `drivers.txt`, restart | File recreated, no crash | | |

6.3 **Test summary:** number passed and failed; bugs found and how they were fixed.

## 7. Discussion
7.1 **Challenges and solutions:** e.g. keeping commas in comments (split limit), safe concurrent writes (synchronized + temp file), avoiding `instanceof` (polymorphic permission methods).
7.2 **Limitations:** plain-text passwords, whole-file rewrite on every save, no CSRF tokens, no e-mail notifications, single server only.
7.3 **Future improvements:** password hashing, a database through a new `Repository` implementation, online payments, driver assignment to rentals, availability calendar.

## 8. Conclusion
*[One paragraph: what was built, which OOP concepts were demonstrated, and what you learned.]*

## 9. References
- Oracle, *The Java Tutorials: Object-Oriented Programming Concepts*
- Jakarta EE, *Jakarta Servlet 6.0 Specification*
- Apache Tomcat 10.1 documentation
- Bootstrap 5.3 documentation
- PlantUML class diagram guide

## Appendix A: Git commit history
Paste the output of:

```
git log --oneline --reverse
```

*[Paste here.]*

## Appendix B: Data file formats
*[Copy the "Data files" section of README.md.]*

## Appendix C: How to run
*[Copy the "Running in IntelliJ IDEA" section of README.md.]*
