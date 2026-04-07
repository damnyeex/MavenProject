package listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.DatabaseInitializer;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Application Starting ===");
        DatabaseInitializer.init();
        System.out.println("=== Application Ready ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Application Shutting Down ===");
    }
}