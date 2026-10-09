package com.rental.servlet;

import com.rental.repository.DataPaths;
import com.rental.repository.PaymentRepository;
import com.rental.repository.RentalRepository;
import com.rental.repository.UserRepository;
import com.rental.repository.VehicleRepository;
import com.rental.service.AuthService;
import com.rental.service.PaymentService;
import com.rental.service.RentalService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Runs once when Tomcat starts the application.
 *
 * <ol>
 *   <li>Makes sure the data folder exists and copies the sample files into it if needed.</li>
 *   <li>Creates one object of every repository and service.</li>
 *   <li>Stores each service in the ServletContext so every servlet can use the same instance.</li>
 * </ol>
 *
 * <p>Services are stored under their class name, e.g. "com.rental.service.UserService".
 * {@link BaseServlet#getService(Class)} reads them back.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    /** Builds repositories and services when the application starts. */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        String seedFolder = context.getRealPath("/WEB-INF/seed-data");
        DataPaths.seedIfMissing(seedFolder == null ? null : Paths.get(seedFolder));
        LOG.info("Using data folder " + DataPaths.getDataDir());

        // Repositories: the only objects that read/write files
        UserRepository userRepository = new UserRepository(dataFile(UserRepository.FILE_NAME));
        VehicleRepository vehicleRepository = new VehicleRepository(dataFile(VehicleRepository.FILE_NAME));
        RentalRepository rentalRepository = new RentalRepository(dataFile(RentalRepository.FILE_NAME));
        PaymentRepository paymentRepository = new PaymentRepository(dataFile(PaymentRepository.FILE_NAME));

        // Services: business rules, used by the servlets
        register(context, new AuthService(userRepository));
        register(context, new UserService(userRepository, rentalRepository));
        register(context, new VehicleService(vehicleRepository, rentalRepository));
        register(context, new RentalService(rentalRepository, vehicleRepository));
        register(context, new PaymentService(paymentRepository, rentalRepository, vehicleRepository));
    }

    private static Path dataFile(String fileName) {
        return DataPaths.file(fileName);
    }

    private static void register(ServletContext context, Object service) {
        context.setAttribute(service.getClass().getName(), service);
    }
}
