package Library.backend.Request;

import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OverdueRequestHandler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final RequestDAO requestDAO = RequestDAOImpl.getInstance();

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                requestDAO.handleOverdueRequests();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS); // Adjust the interval as needed
    }

    public void stop() {
        scheduler.shutdown();
    }
}