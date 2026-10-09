# Viva Notes: RentRide Vehicle Rental Platform

Plain-English notes for explaining the project in the SE1020 viva. Every concept points to the
exact class and method, so you can open the file and show it.

---

## 1. The project in one minute

RentRide is a Java web application (JSP + Servlets on Tomcat 10) for renting cars, bikes and vans.
It has **six modules**, each with full CRUD and its own text file:

| Module | Main classes | Data file |
|---|---|---|
| User Management | `User` → `AdminUser`, `Customer` | `users.txt` |
| Vehicle Management | `Vehicle` → `Car`, `Bike`, `Van` | `vehicles.txt` |
| Rental Booking | `Rental`, `RentalStatus` | `rentals.txt` |
| Payment and Billing | `Payment` → `CashPayment`, `CardPayment` | `payments.txt` |
| Feedback and Reviews | `Review` → `PublicReview`, `VerifiedReview` | `reviews.txt` |
| Driver/Staff Management | `Staff` → `Driver`, `Mechanic` | `drivers.txt` |

The code is split into **layers**. Each layer only talks to the one below it:

```
Browser ──► AuthFilter ──► Servlet ──► Service ──► Repository ──► FileHandler ──► data/*.txt
                              │
                              └──► JSP page (HTML shown to the user)
```

- **model/** holds the objects (`Vehicle`, `Rental` ...). They know their own rules.
- **repository/** is the ONLY code that reads or writes files.
- **service/** holds the business rules (no overlapping bookings, one bill per rental ...).
- **servlet/** has one class per action. Each one reads the form, calls a service and shows a JSP.
- **filter/** holds `AuthFilter`, which blocks pages you are not allowed to see.

---

## 2. The four OOP concepts (with exact places to show)

### 2.1 Encapsulation: "hide the data, protect it with methods"

Every field is `private`. The only way to change a field is a setter, and the setter checks the value.
An invalid object can never exist, because the **constructors call the same setters**.

| Rule | Class.method |
|---|---|
| Brand/model cannot be blank or contain commas | `Vehicle.setBrand()`, `Vehicle.setModel()` → `ValidationUtil.requireText()` |
| Daily rate must be greater than 0 | `Vehicle.setBaseDailyRate()` |
| Year between 1990 and next year | `Vehicle.setYear()` |
| Seats 2-9 / engine 50-2000cc / cargo 100-5000kg | `Car.setSeats()`, `Bike.setEngineCC()`, `Van.setCargoCapacity()` |
| End date must be after start date | `Rental.setDates()` |
| Rating 1-5, comment 3-500 characters | `Review.setRating()`, `Review.setComment()` |
| Card digits exactly 4 numbers | `CardPayment.setCardLast4()` |
| Phone is 10 digits starting with 0 | `Staff.setPhone()` |
| Password cannot be read from outside | `User` has **no** `getPassword()`; other classes call `User.checkPassword()` |

**What to say:** "The `id` fields are `final`, so they never change after creation. Everything
else is private and goes through a validating setter. For example, `new Car(..., 12)` throws
'Seats must be between 2 and 9' from `Car.setSeats()`."

### 2.2 Inheritance: "write common code once, reuse it in subclasses"

| Parent (abstract) | Children | What the children inherit |
|---|---|---|
| `User` | `AdminUser`, `Customer` | id, username, password, name, email, validation, `toFileString()` |
| `Vehicle` | `Car`, `Bike`, `Van` | id, brand, model, year, rate, available, `commonFileFields()` |
| `Payment` | `CashPayment`, `CardPayment` | ids, issue date, base amount, late days, status |
| `Staff` | `Driver`, `Mechanic` | id, name, phone, daily wage |
| `Review` | `PublicReview`, `VerifiedReview` | ids, rating, date, comment |
| `AbstractFileRepository<T>` | `UserRepository`, `VehicleRepository`, ... (6) | all CRUD + safe file writing |
| `BaseServlet` (extends `HttpServlet`) | all 34 servlets | `getService()`, `render()`, `redirect()`, flash messages |

**What to say:** "`Car` only adds `seats` and its own pricing rule. Everything else, including
validation of the brand and rate, comes from `Vehicle` through `super(...)` in the constructor."

### 2.3 Polymorphism: "same method call, different behaviour depending on the real object"

The code holds objects in **parent-type references** (`Vehicle v`, `List<Payment>`, `User user`),
and Java runs the subclass's version at runtime (dynamic binding). There are **no `instanceof`
checks** in the project.

| Method | Different behaviour | Where it is called through the parent type |
|---|---|---|
| `Vehicle.calculateDailyRate()` | Car +10% if >5 seats; Bike ×1.2 if >500cc; Van + Rs. 2/kg | `RentalService.estimateCost()`, `PaymentService.generate()`, every vehicle JSP |
| `Vehicle.displayDetails()` | "Car with 5 seats" / "689cc Bike" / "Van carrying 1200 kg" | vehicle browse, details, rental pages |
| `Vehicle.setSpecValue()` | sets seats / engineCC / cargoCapacity | `VehicleService.update()` |
| `Payment.calculateTotal()` | Cash: base + Rs. 1,000/late day; Card: (base + Rs. 1,500/late day) + 3% | `PaymentService.totalByStatus()`, `bill.jsp` |
| `Payment.getSurcharge()` | 0 for cash (inherited), 3% for card (overridden) | `bill.jsp` |
| `User.getRole()`, `canModifyVehicles()`, `canAccessAdminPages()`, `getDashboardPath()` | admin = full access, customer = limited | `AuthFilter.doFilter()`, `LoginServlet.doPost()`, `RentalService.findForUser()`, `navbar.jspf` |
| `Staff.displayRole()`, `calculateMonthlyPay()` | driver: + Rs. 500/day meal allowance; mechanic: + Rs. 5,000 tool allowance | `staff-list.jsp` |
| `Review.displayLabel()`, `isVerified()` | "Verified renter" vs "Public review" | `review-list.jsp`, `vehicle-details.jsp` |
| `Storable.toFileString()` | each class writes its own line format | `AbstractFileRepository.saveAll()` |

**What to say (best demo):** open `AuthFilter.doFilter()`:
```java
if (path.startsWith("/admin/") && !user.canAccessAdminPages()) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
```
"The filter doesn't know or care whether `user` is an `AdminUser` or a `Customer`. It asks the
object, and the object answers for itself. Adding a new role, say `Manager`, needs no change here."

### 2.4 Abstraction: "show what an object does, hide how"

| Abstraction | Type | Why |
|---|---|---|
| `Storable` | interface | anything that can be one line in a file: `getId()`, `toFileString()` |
| `Repository<T extends Storable>` | generic interface | the 5 CRUD operations: `add`, `findById`, `findAll`, `update`, `delete` |
| `User`, `Vehicle`, `Payment`, `Staff`, `Review` | abstract classes | you cannot create a "plain vehicle", only a Car/Bike/Van |
| `AbstractFileRepository.parse()` | abstract method | each repository says only how to read ONE line |
| `BaseServlet` | abstract class | shared servlet helpers |

**What to say:** "`Vehicle` is abstract because a vehicle in our fleet is always a specific kind.
`calculateDailyRate()` is abstract because there's no single correct rule; each subclass must
provide its own, or the code won't compile."

### 2.5 Information hiding

- Servlets **never** touch files. Example: `VehicleAddServlet.doPost()` calls
  `VehicleService.add(...)`. It has no idea `vehicles.txt` exists.
- File paths are known only to `DataPaths` and `AppContextListener`.
- Line parsing lives only in each repository's `parse()` method.
- If we replaced text files with a database, **only the repository package would change**.

### 2.6 Bonus design patterns you can mention

| Pattern | Where |
|---|---|
| **Factory method** | `Vehicle.create()`, `User.create()`, `Payment.create()`, `Staff.create()`, `Review.create()`: the type string (e.g. `"CAR"`) decides which subclass is built |
| **Template method** | `AbstractFileRepository.findAll()` runs fixed steps and calls the abstract `parse()` |
| **Post-Redirect-Get** | every `doPost()` ends with `redirect(...)` |
| **Front filter** | `AuthFilter` checks every request (`@WebFilter("/*")`) |
| **Singleton-like shared services** | `AppContextListener` creates ONE instance of each service at startup |

---

## 3. How file reading and writing works (step by step)

All six files work the same way, because all repositories extend `AbstractFileRepository`.

### 3.1 Start-up (`AppContextListener.contextInitialized`)
1. Tomcat starts the app and calls `contextInitialized()`.
2. `DataPaths.seedIfMissing()` checks the data folder. If a file like `vehicles.txt` is missing,
   the sample copy is copied in (from `WEB-INF/seed-data`, bundled by Maven). Existing files are never overwritten.
3. One repository is created per file, e.g. `new VehicleRepository(DataPaths.file("vehicles.txt"))`.
4. The services are created and stored in the `ServletContext`. Servlets get them with
   `getService(VehicleService.class)`.

### 3.2 Reading: e.g. the vehicle list page
1. `VehicleListServlet.doGet()` → `vehicleService.search(q, type, availability)`
2. → `vehicleRepository.findAll()` (inherited from `AbstractFileRepository`)
3. → `fileHandler.readLines()`:
   - creates the file if missing (`ensureFileExists()`)
   - `Files.readAllLines(path, UTF_8)`
   - **skips blank lines**
4. Back in `findAll()`, for each line:
   - `line.split(",", splitLimit())` → `String[] fields`
   - `parse(fields)` → `VehicleRepository.parse()` checks there are 8 fields and calls
     **`Vehicle.create(fields[0], ...)`**, the factory. `"CAR"` builds a `Car`, `"BIKE"` a `Bike`, `"VAN"` a `Van`.
   - if anything is wrong (missing field, `"abc"` as year, `"TRUCK"` type, `"maybe"` instead of
     true/false), an exception is thrown, **caught**, a warning is logged, and the line is **skipped**.
     One bad line never crashes the page.
5. The service filters the `List<Vehicle>` and the servlet forwards it to `vehicle-list.jsp`.

### 3.3 Writing: e.g. adding a vehicle
1. `VehicleAddServlet.doPost()` → `vehicleService.add(type, brand, ...)`
2. Service: `IdGenerator.next("V", allIds())` finds the highest id (V012) and returns `V013`.
3. `Vehicle.create(...)` builds a `Van`. **Setters validate** and throw `IllegalArgumentException` if bad.
4. `vehicleRepository.add(van)` (synchronized):
   - `findAll()` loads the current list
   - checks the id is not a duplicate
   - adds the new object
   - `saveAll(list)` turns every object into `toFileString()` (polymorphic) and calls
     `fileHandler.writeLines(lines)`
5. `FileHandler.writeLines()` (synchronized) does the **safe write**:
   - writes all lines to `vehicles.txt.tmp`
   - `Files.move(tmp, vehicles.txt, REPLACE_EXISTING, ATOMIC_MOVE)`
   - if the computer crashes halfway, the real file is still the old, complete version
6. The servlet sets a flash message ("Vehicle V013 added") and **redirects** to `/admin/vehicles`
   (Post-Redirect-Get, so refreshing the page does not add the vehicle twice).

### 3.4 Update and delete
- `update(item)`: load all, replace the item with the same id, save all.
- `delete(id)`: load all, `removeIf(id matches)`, save all.
- Both return `true`/`false` so the service knows whether the id existed.

### 3.5 Why commas in review comments don't break the file
Review lines are `type,id,vehicleId,customerId,rating,date,comment`. `ReviewRepository` overrides
`splitLimit()` to return 7, so `split(",", 7)` makes **at most 7 pieces**. Everything after the 6th
comma, commas included, becomes the comment. Every other text field rejects commas in
`ValidationUtil.requireText()`.

### 3.6 Thread safety
Two users can click "Save" at the same moment. `FileHandler` methods and all
`AbstractFileRepository` CRUD methods are `synchronized`, so one request finishes its
load-change-save before the next one starts. No update is lost.

---

## 4. Key business rules (and where they live)

| Rule | Where |
|---|---|
| Customers can't open `/admin/*` pages (HTTP 403) | `AuthFilter.doFilter()` |
| Not logged in → redirected to `/login` | `AuthFilter.doFilter()` |
| Username and e-mail must be unique | `UserService.checkUnique()` |
| You can't delete yourself or the last admin | `UserService.delete()` |
| A vehicle/customer with an ACTIVE booking can't be deleted | `VehicleService.delete()`, `UserService.delete()` |
| No overlapping ACTIVE bookings of the same vehicle | `RentalService.checkNoOverlap()` + `Rental.overlaps()` |
| Start date not in the past; max 30 days | `RentalService.checkDateRules()` |
| Customers can change only their own bookings | `RentalService.getManageable()` |
| Only ACTIVE rentals can be returned, and only once they've started | `RentalService.markReturned()` |
| One bill per rental; cancelled rentals can't be billed | `PaymentService.generate()` |
| Base amount = days × `calculateDailyRate()` | `PaymentService.generate()` |
| Verified review only if the customer has a RETURNED rental of that vehicle | `ReviewService.typeFor()` |
| One review per customer per vehicle | `ReviewService.submit()` |
| Unique phone numbers and licence numbers for staff | `StaffService.checkUnique()` |

**Overlap rule explained:** two date ranges overlap when *each starts before the other ends*:
`newStart < existingEnd && newEnd > existingStart`. A booking may start on the day another ends
(the car is returned that morning), so back-to-back bookings are allowed.

---

## 5. Demo script (what to click and what to say)

Log-ins: **admin / admin123** (admin) and **nimal / pass123** (customer). Today's seeded data has
an ACTIVE booking **R008: V001 Toyota Corolla, 20-25 Oct 2026**, ready for the overlap demo.

> Tip: open `data/vehicles.txt` (etc.) in IntelliJ next to the browser and show the line change after each save.
>
> **Viva after 25 Oct 2026?** Booking dates can't be in the past, so use a later ACTIVE booking for the overlap demo:
> **R011, V010 Toyota HiAce, 20-27 Dec 2026** (try 22-24 Dec) or **R012, V004 Montero, 1-5 Feb 2027**.
> To reset all demo data, stop Tomcat and run `git checkout -- data/`.

### 5.1 Login and security (2 min)
1. Open `http://localhost:8080/vehicle-rental-system/` → login page. *"Every page except login and register is protected by `AuthFilter`."*
2. Log in as **nimal** → customer dashboard. *"`LoginServlet` redirects to `user.getDashboardPath()`, which is polymorphic."*
3. Type `/admin/users` in the address bar → **403 Access denied**. *"The filter calls `canAccessAdminPages()`; `Customer` returns false."*
4. Log out → press the browser **Back** button → you're sent to login. *"Cache-Control: no-store plus the filter."*

### 5.2 User Management (CRUD)
- **Create:** Register → fill the form → try mismatched passwords (client-side error) → submit valid → "Account created". Show the new `U013,CUSTOMER,...` line in `users.txt`.
- **Read/Search:** as admin, Users → search `perera`, filter *Customer*.
- **Update:** Edit a user → change the name → save. Then **My profile** → change your own e-mail.
- **Delete:** delete the new user (confirmation page → Yes). Try deleting **Kasun (U004)** → refused: *"has active booking R008"*. Try deleting yourself → refused.

### 5.3 Vehicle Management (CRUD)
- **Create:** Vehicles → Add vehicle → choose **Van**. *"The extra field label changes to Cargo (kg) via JavaScript."* Try rate `0` → error from `Vehicle.setBaseDailyRate()`. Enter valid values → V013 added.
- **Read/Search:** filter Type = Bike, Availability = Unavailable. Point to the **Daily rate** column: *"Montero base 16,000 shows 17,600 because `Car.calculateDailyRate()` adds 10% for 7 seats; MT-07 is ×1.2 because it's 689cc."*
- **Update:** Edit V007 → untick *Available* → save → it disappears from the customer **Browse** page.
- **Delete:** delete V013 → confirmation → gone from the file. Try deleting **V001** → refused (active booking R008).

### 5.4 Rental Booking (CRUD)
- As **nimal**: Browse → Toyota Corolla → *Booked dates* shows 20→25 Oct → **Book this vehicle**.
- **Overlap demo:** choose 22 Oct → 24 Oct → *"Vehicle is already booked from 2026-10-20 to 2026-10-25 (R008)"*. *"That's `RentalService.checkNoOverlap()` calling `Rental.overlaps()`."*
- Choose 25 Oct → 27 Oct → **accepted** (back-to-back). Point to the live cost estimate (JavaScript) and the server-calculated cost in the flash message.
- **Read:** My rentals → only Nimal's rentals. *"`findForUser()` uses `canAccessAdminPages()` to decide all vs own."*
- **Update:** Change dates → 26 → 29 Oct → saved.
- **Cancel (delete):** Cancel → confirmation → status CANCELLED (kept for history).
- As **admin**: Rentals → R010 (current rental) → **Mark returned**.

### 5.5 Payment and Billing (CRUD)
- **Read:** as admin, Payments → totals at the top. *"`totalByStatus()` adds `calculateTotal()` from each bill, and cash and card compute differently."*
- Open **P002** (card): base 10,800 + late fee 1,500 + 3% surcharge 369 = **12,669**. Open **P004** (cash): 6,000 + 1,000 = 7,000, no surcharge line. *"Same JSP code, different results: polymorphism."*
- **Create:** Generate bill → choose R012 → Card, 4 digits `1234` → total 72,512 (4 days × 17,600 + 3%). Try generating again for R012 → *"already has bill"*. Try R007 (cancelled) → not listed / refused.
- **Update:** Update → status PAID, late days 2 → the total is recalculated.
- **Delete:** delete the bill → R012 becomes billable again.
- As **nimal**: My bills → only own bills; Print button prints a clean invoice.

### 5.6 Feedback and Reviews (CRUD)
- As **nimal**: Corolla → reviews: average stars, *Verified renter* and *Public review* badges. Point out the comma inside *"Clean car, smooth drive"*: *"`ReviewRepository.splitLimit()` is 7."*
- **Create:** Mitsubishi Montero → Write a review. The form says **Public** (Nimal's Montero rental R012 is not returned yet) → post.
- **Update:** My reviews → Edit RV001 → change the rating.
- **Delete:** delete your Montero review. As admin, delete any review (moderation).
- **Bonus (verified upgrade):** as admin, return a rental of the vehicle, then edit the review as the customer: it becomes *Verified renter* (`ReviewService.update()` re-checks `typeFor()`).

### 5.7 Driver/Staff Management (CRUD)
- **Read:** Staff → *Monthly pay (22 days)* column: Kamal (driver) (3,500 + 500) × 22 = 88,000; Ruwan (mechanic) 4,000 × 22 + 5,000 = 93,000. *"`calculateMonthlyPay()` is overridden."*
- **Create:** Add staff → Driver → licence `b7654321` → saved as `B7654321`. Try an existing licence or phone → refused.
- **Update:** Edit a mechanic's specialization.
- **Delete:** Remove → confirmation.

### 5.8 Robustness (30 s)
Add a line `CAR,V099,Bad,Line,abc,100,true,4` to `vehicles.txt` → refresh the list → the page still
works, and the Tomcat log shows *"Skipping malformed record ..."*.

---

## 6. Thirty likely viva questions (with short answers)

1. **What is encapsulation in your project?** Private fields plus validating setters, e.g. `Vehicle.setBaseDailyRate()` rejects rates ≤ 0. Constructors call the setters, so invalid objects can't be created.
2. **Why is `Vehicle` abstract?** A vehicle is always a Car, Bike or Van, and `calculateDailyRate()` has no general rule, so it must be implemented by each subclass.
3. **Abstract class vs interface: which did you use and why?** Both. `Storable` and `Repository<T>` are interfaces (pure contracts, no state). `Vehicle`, `User` etc. are abstract classes because they share fields and code.
4. **Show me polymorphism.** `AuthFilter` calls `user.canAccessAdminPages()` on a `User` reference; `AdminUser` returns true and `Customer` returns false. Also `vehicle.calculateDailyRate()` in `PaymentService.generate()`.
5. **Overloading vs overriding?** Overriding = same signature in a subclass (`Car.calculateDailyRate()`), resolved at run time. Overloading = same name, different parameters, resolved at compile time.
6. **Why no `instanceof`?** Asking the object (`canAccessAdminPages()`) is open for extension. A new role or vehicle type works without editing every `if`.
7. **What does `super(...)` do in `Car`'s constructor?** Calls the `Vehicle` constructor, which validates and sets the common fields, before `Car` sets `seats`.
8. **What is the factory method?** `Vehicle.create(type, ...)` uses a switch on `"CAR"/"BIKE"/"VAN"` to build the right subclass. It's used both when reading the file and when the admin adds a vehicle.
9. **How is a line turned into an object?** `AbstractFileRepository.findAll()` splits the line on commas and calls the subclass's `parse()`, which calls the factory.
10. **What happens with a malformed line?** `parse()` throws, `findAll()` catches it, logs a warning and skips the line. The rest of the file still loads.
11. **How do you make writing safe?** `FileHandler.writeLines()` writes to a `.tmp` file, then atomically moves it over the real file. A crash leaves the old file intact.
12. **What does `synchronized` do here?** Only one thread at a time can run the method on that object, so two simultaneous saves can't overwrite each other's changes.
13. **What if the data file doesn't exist?** `FileHandler.ensureFileExists()` creates the folder and an empty file. On first start, `DataPaths.seedIfMissing()` copies the sample data.
14. **How are ids generated?** `IdGenerator.next("V", existingIds)` takes the highest number with that prefix and adds 1 (V012 → V013).
15. **What is Post-Redirect-Get and why use it?** After a POST, the servlet redirects to a GET page. Refreshing the browser then repeats only the GET, not the save.
16. **What is a flash message?** A message saved in the session before a redirect and shown once on the next page (`FlashUtil`, removed in `header.jspf`).
17. **How does the login work?** `AuthService.login()` finds the user by username and calls `checkPassword()`. `LoginServlet` stores the `User` in the session, changes the session id, and redirects to `getDashboardPath()`.
18. **How do you stop customers opening admin pages?** `AuthFilter` (mapped to `/*`) returns 403 for `/admin/*` unless `canAccessAdminPages()` is true. Vehicle servlets also check `canModifyVehicles()`.
19. **How do you prevent double booking?** `RentalService.checkNoOverlap()` loops over the vehicle's rentals and calls `Rental.overlaps(start, end)`: `start < otherEnd && end > otherStart` for ACTIVE rentals only.
20. **How is a bill calculated?** Base = `rental.getDays() × vehicle.calculateDailyRate()`. The total is `payment.calculateTotal()`: cash adds Rs. 1,000 per late day; card adds Rs. 1,500 per late day, then 3%.
21. **What makes a review "verified"?** `ReviewService.typeFor()` checks `RentalService.hasReturnedRental(customer, vehicle)`. If true, the factory creates a `VerifiedReview`.
22. **How can review comments contain commas?** The comment is the last field and `ReviewRepository.splitLimit()` returns 7, so `split(",", 7)` keeps the rest of the line together.
23. **Why are JSPs inside `WEB-INF`?** Files under `WEB-INF` can't be opened directly by URL. Users must go through a servlet, so the security checks and data loading always run.
24. **What is `BaseServlet`?** An abstract parent of all servlets with shared helpers (`getService`, `render`, `redirect`, `refuseUnless`). It's inheritance applied to the web layer.
25. **Where is validation done: client or server?** Both. HTML5/Bootstrap and `app.js` give quick feedback, but the server always validates again in the setters and services, because client checks can be bypassed.
26. **How do you prevent XSS?** All user text is printed with `<c:out>` or `fn:escapeXml()`, which turn `<` into `&lt;`.
27. **Why use an enum for `RentalStatus`?** Only the three valid values can exist. `RentalStatus.valueOf("LOST")` throws, so a bad file line is skipped instead of creating a wrong status.
28. **What is `Optional` and why use it?** `findById()` returns `Optional<T>` instead of `null`, which forces the caller to handle "not found" (`orElseThrow`, `isEmpty`).
29. **What are the limitations of your design?** Plain-text passwords, the whole file is rewritten on each save (fine for hundreds of records, slow for millions), and there's no CSRF token. A database and hashing would fix these.
30. **What would you change to use a database?** Only the repository layer: write `JdbcVehicleRepository implements Repository<Vehicle>`. Services and servlets stay the same, thanks to the interface and information hiding.

---

## 7. Numbers to remember

- 6 modules, 6 data files, 12+ sample records in most files (10 bills, 10 staff)
- 5 abstract model classes, 11 concrete subclasses, 2 enums, 2 interfaces
- 34 servlets, 1 filter, 1 listener, 32 JSP pages + 7 shared fragments (.jspf)
- Rate rules: Car +10% (>5 seats) · Bike ×1.2 (>500cc) · Van + Rs. 2/kg
- Bill rules: Cash + Rs. 1,000/late day · Card + Rs. 1,500/late day + 3%
- Pay rules: Driver (wage + Rs. 500) × days · Mechanic wage × days + Rs. 5,000
