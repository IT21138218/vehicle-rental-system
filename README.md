# RentRide: Vehicle Rental Service Platform

SE1020 Object Oriented Programming, individual project.

A Java web application for renting cars, bikes and vans. It's built with **JSP and Servlets**
(Jakarta Servlet 6 on Tomcat 10), **JSTL**, **Bootstrap 5** and **plain text files** for storage
(no database). The code is organised in layers to show encapsulation, inheritance, polymorphism
and abstraction clearly.

## Modules

| # | Module | What you can do | Data file |
|---|--------|-----------------|-----------|
| 1 | User Management | register, log in/out, search users, edit profile, delete accounts | `users.txt` |
| 2 | Vehicle Management | add, list/search/filter by type and availability, edit, delete | `vehicles.txt` |
| 3 | Rental Booking | book with date range and overlap check, view, change dates, cancel, mark returned | `rentals.txt` |
| 4 | Payment and Billing | generate a bill from a rental, view/print, update status, delete | `payments.txt` |
| 5 | Feedback and Reviews | submit, view per vehicle, edit own, delete (owner or admin) | `reviews.txt` |
| 6 | Driver/Staff Management | add, view, update, remove drivers and mechanics | `drivers.txt` |

**Roles**
- **Admin** has full access to every module.
- **Customer** can browse and search vehicles, create and cancel their own bookings, view their own bills, and write and edit their own reviews.

## Demo accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Admin | `manager` | `manager123` |
| Customer | `nimal` | `pass123` |
| Customer | `kasun`, `dilini`, `tharindu`, ... | `pass123` |

## Technology

- Java 17, Maven 3.9 (WAR packaging)
- Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0 (the only libraries)
- Apache Tomcat **10.1** or newer (Tomcat 9 will **not** work: it uses `javax.*`, not `jakarta.*`)
- Bootstrap 5.3 and Bootstrap Icons, loaded from a CDN

## Project structure

```
vehicle-rental-system/
├── pom.xml
├── data/                         sample data files (copied into WEB-INF/seed-data by Maven)
├── docs/
│   ├── class-diagram.puml        PlantUML: domain model + layers
│   ├── VIVA_NOTES.md             OOP explanations, file I/O walkthrough, demo script, 30 Q&A
│   └── REPORT_OUTLINE.md         report skeleton, screenshot list, test table
└── src/main/
    ├── java/com/rental/
    │   ├── model/        User, AdminUser, Customer, Vehicle, Car, Bike, Van, Rental, RentalStatus,
    │   │                 Payment, CashPayment, CardPayment, PaymentStatus, Staff, Driver, Mechanic,
    │   │                 Review, PublicReview, VerifiedReview, Storable
    │   ├── repository/   Repository<T>, AbstractFileRepository<T>, FileHandler, DataPaths,
    │   │                 one repository per entity (the ONLY code that touches files)
    │   ├── service/      business rules: AuthService, UserService, VehicleService, RentalService,
    │   │                 PaymentService, ReviewService, StaffService
    │   ├── servlet/      BaseServlet + one servlet per action, AppContextListener
    │   ├── filter/       AuthFilter (login + role checks)
    │   └── util/         IdGenerator, ValidationUtil, FlashUtil
    └── webapp/
        ├── css/style.css, js/app.js
        ├── index.jsp                   redirects to /login
        └── WEB-INF/
            ├── web.xml                 UTF-8, error pages, JSTL locale
            └── views/                  JSPs (not reachable directly by URL)
                ├── includes/           header, navbar (changes by role), footer, badges, stars
                ├── auth/ users/ dashboard/ vehicles/ rentals/ payments/ reviews/ staff/
                └── error/              403, 404, 500
```

## Running in IntelliJ IDEA

### Prerequisites
1. **JDK 17** (e.g. Eclipse Temurin 17)
2. **Apache Tomcat 10.1.x**: download the zip from https://tomcat.apache.org and unzip it, e.g. to `C:\tomcat\apache-tomcat-10.1.52`
3. **IntelliJ IDEA**: *Ultimate* has built-in Tomcat support. With *Community*, install the **Smart Tomcat** plugin (see Option B).

### Step 1: Open the project
1. *File → Open* and select the `vehicle-rental-system` folder (the one containing `pom.xml`).
2. Choose **Open as Project** and trust the Maven project. Wait for Maven to download the dependencies.
3. *File → Project Structure → Project*: set **SDK = 17** and **Language level = 17**.

### Step 2, option A: IntelliJ IDEA Ultimate (built-in Tomcat)
1. *Run → Edit Configurations… → + → Tomcat Server → Local*.
2. **Server** tab:
   - *Application server*: **Configure…** → select your Tomcat folder.
   - *VM options*: `-Drental.data.dir=C:\path\to\vehicle-rental-system\data` (use your real project path).
   - *URL*: `http://localhost:8080/vehicle-rental-system/`
3. **Deployment** tab: **+ → Artifact… → vehicle-rental-system:war exploded**.
   Set *Application context* to `/vehicle-rental-system`.
4. Click **Apply**, then **Run** (green ▶). The browser opens at the login page.

### Step 2, option B: IntelliJ IDEA Community (Smart Tomcat plugin)
1. *Settings → Plugins → Marketplace* → install **Smart Tomcat** → restart IntelliJ.
2. *Run → Edit Configurations… → + → Smart Tomcat*:
   - *Tomcat server*: select your Tomcat 10.1 folder
   - *Deployment directory*: `…\vehicle-rental-system\src\main\webapp`
   - *Context path*: `/vehicle-rental-system`
   - *VM options*: `-Drental.data.dir=C:\path\to\vehicle-rental-system\data`
3. Run it and open `http://localhost:8080/vehicle-rental-system/`.

### Option C: command line (no IDE)
```
mvn clean package
copy target\vehicle-rental-system.war  C:\tomcat\apache-tomcat-10.1.52\webapps\
set CATALINA_OPTS=-Drental.data.dir=C:\path\to\vehicle-rental-system\data
C:\tomcat\apache-tomcat-10.1.52\bin\startup.bat
```
Then open `http://localhost:8080/vehicle-rental-system/`.

## Where the data is stored

On start-up, the Tomcat log prints `Using data folder …`. The folder is chosen like this:

1. If the VM option **`-Drental.data.dir=…`** is set, that folder is used. **Recommended:** point it at
   the project's `data/` folder so you can open the files in IntelliJ and watch them change.
2. Otherwise `<your home folder>/vehicle-rental-data` is used.

Any data file that is missing from that folder is copied from the bundled sample data on start-up.
Existing files are never overwritten. **To reset the demo data:** stop Tomcat and run
`git checkout -- data/` (option 1), or delete the `vehicle-rental-data` folder (option 2).

File handling rules (all in the `repository` package):
- Missing files and folders are created automatically.
- Blank lines are ignored. Malformed lines are **skipped with a warning** in the log (and dropped on the next save).
- Every save is `synchronized` and writes to `<file>.tmp` first, then atomically replaces the real file.

## Data files

One record per line, fields separated by commas. Text fields may not contain commas,
**except the review comment**, which is always the last field. Dates use `yyyy-MM-dd`.
Amounts are in Sri Lankan rupees (Rs.).

### `users.txt`
```
id,role,username,password,name,email
U001,ADMIN,admin,admin123,System Administrator,admin@rentride.lk
U003,CUSTOMER,nimal,pass123,Nimal Perera,nimal.perera@gmail.com
```
`role` is `ADMIN` or `CUSTOMER`. Username: 4-20 letters, digits, `.` or `_`. Password: 6-30 characters.

### `vehicles.txt`
```
type,id,brand,model,year,baseDailyRate,available,extra
CAR,V001,Toyota,Corolla,2021,8500.00,true,5
BIKE,V007,Yamaha,MT-07,2022,4500.00,true,689
VAN,V010,Toyota,HiAce,2020,12000.00,true,1200
```
| type | `extra` means | Range | Daily rate rule (`calculateDailyRate()`) |
|------|---------------|-------|-------------------------------------------|
| `CAR` | seats | 2-9 | base, +10% if more than 5 seats |
| `BIKE` | engine CC | 50-2000 | base, ×1.2 if above 500cc |
| `VAN` | cargo capacity (kg) | 100-5000 | base + Rs. 2 per kg |

`available` is `true` (in service, can be booked) or `false`.

### `rentals.txt`
```
id,customerId,vehicleId,startDate,endDate,status
R008,U004,V001,2026-10-20,2026-10-25,ACTIVE
```
`status` is `ACTIVE`, `RETURNED` or `CANCELLED`. Days = endDate − startDate. Two ACTIVE rentals of the
same vehicle may not overlap, but a booking may start on the day another ends.

### `payments.txt`
```
method,id,rentalId,customerId,issueDate,baseAmount,lateDays,status,extra
CASH,P001,R001,U003,2026-08-05,34000.00,0,PAID,Front Desk
CARD,P002,R002,U004,2026-08-12,10800.00,1,PAID,4242
```
| method | `extra` means | Total (`calculateTotal()`) |
|--------|---------------|----------------------------|
| `CASH` | received by (staff/desk) | base + Rs. 1,000 × lateDays |
| `CARD` | last 4 card digits | (base + Rs. 1,500 × lateDays) × 1.03 |

`baseAmount` = rental days × vehicle daily rate, fixed when the bill is generated. `status` is `PENDING`, `PAID` or `OVERDUE`.

### `reviews.txt`
```
type,id,vehicleId,customerId,rating,date,comment
VERIFIED,RV001,V001,U003,5,2026-08-06,Clean car, smooth drive and very economical on fuel.
PUBLIC,RV009,V001,U010,4,2026-09-28,A friend rented this one and recommended it highly.
```
`type` is `VERIFIED` (the customer has a RETURNED rental of that vehicle) or `PUBLIC`. Rating is 1-5.
The comment (3-500 characters) is the last field and may contain commas.

### `drivers.txt`
```
type,id,name,phone,dailyWage,extra
DRIVER,S001,Kamal Silva,0771234567,3500.00,B1234567
MECHANIC,S007,Ruwan Fernando,0719876543,4000.00,Engine
```
| type | `extra` means | Monthly pay (`calculateMonthlyPay(days)`) |
|------|---------------|-------------------------------------------|
| `DRIVER` | licence number (1 letter + 7 digits) | (wage + Rs. 500) × days |
| `MECHANIC` | specialization | wage × days + Rs. 5,000 |

Phone: 10 digits starting with 0.

## Main URLs

| URL | Who | Purpose |
|-----|-----|---------|
| `/login`, `/register`, `/logout` (POST), `/profile` | all | authentication and own profile |
| `/admin/dashboard`, `/customer/dashboard` | by role | dashboards |
| `/admin/users` (+ `/edit`, `/delete`) | admin | user management |
| `/admin/vehicles` (+ `/add`, `/edit`, `/delete`) | admin | fleet management |
| `/vehicles`, `/vehicles/view?id=` | all | browse and vehicle details |
| `/rentals` (+ `/new`, `/edit`, `/cancel`, `/view`) | all (own) / admin (all) | bookings |
| `/admin/rentals/return` (POST) | admin | mark a rental returned |
| `/payments`, `/payments/view?id=` | all (own) / admin (all) | bills |
| `/admin/payments/generate`, `/edit`, `/delete` | admin | billing |
| `/reviews?vehicleId=`, `/reviews` (+ `/new`, `/edit`, `/delete`) | all | reviews |
| `/admin/staff` (+ `/add`, `/edit`, `/delete`) | admin | drivers and mechanics |

Each GET shows a page. Each POST performs the action and then redirects (Post-Redirect-Get).

## Troubleshooting

| Problem | Fix |
|---------|-----|
| 404 at `http://localhost:8080/` | Include the context path: `/vehicle-rental-system/` |
| `ClassNotFoundException: jakarta.servlet…` | You're using Tomcat 9; use Tomcat 10.1+ |
| Changes don't appear in `data/` | Check the `Using data folder …` log line; set `-Drental.data.dir` |
| Port 8080 already in use | Change the HTTP port in the run configuration, or stop the other server |
| Bookings rejected as "in the past" | The sample bookings are for Oct 2026-Feb 2027; choose future dates |

## Known limitations

- **Passwords are stored in plain text**, which is acceptable for this coursework. A real system would store salted hashes (e.g. PBKDF2/bcrypt).
- Every save rewrites the whole file. That's fine for hundreds of records, but a database would be needed at scale.
- No CSRF tokens on forms; logout and all changes use POST, which reduces the risk.
- Deleting a user or vehicle keeps their old rentals, bills and reviews, which show the id instead of a name.

## Documentation

- [docs/class-diagram.puml](docs/class-diagram.puml): class diagrams (open with the IntelliJ *PlantUML Integration* plugin)
- [docs/VIVA_NOTES.md](docs/VIVA_NOTES.md): OOP concept map, file I/O walkthrough, demo script, 30 viva questions
- [docs/REPORT_OUTLINE.md](docs/REPORT_OUTLINE.md): report structure, screenshots to take, testing table
