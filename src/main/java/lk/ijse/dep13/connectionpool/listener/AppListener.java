package lk.ijse.dep13.connectionpool.listener;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lk.ijse.dep13.connectionpool.db.CP;

import java.util.Set;

public class AppListener implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        CP cp = new CP();
        servletContext.setAttribute("datasource", cp);
    }
}
