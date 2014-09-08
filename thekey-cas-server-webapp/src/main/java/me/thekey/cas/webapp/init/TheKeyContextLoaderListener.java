package me.thekey.cas.webapp.init;

import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class TheKeyContextLoaderListener extends ContextLoaderListener {
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        super.contextDestroyed(event);

        // deregister any registered SQL drivers
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            } catch (final SQLException ignored) {
            }
        }

        // shutdown MySQL AbandonedConnectionCleanupThread, use reflection to prevent compile time conflicts
        try {
            final Class<?> clazz = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
            final Method method = clazz.getMethod("shutdown");
            method.invoke(null);
        } catch (final ReflectiveOperationException ignored) {
        }
    }
}
