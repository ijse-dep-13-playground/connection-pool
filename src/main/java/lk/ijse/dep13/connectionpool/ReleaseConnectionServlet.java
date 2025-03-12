package lk.ijse.dep13.connectionpool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "release-connection-servlet", urlPatterns = "/connections/*")
public class ReleaseConnectionServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            releaseAllConnections(resp);
        }else{
            Matcher matcher = Pattern.compile("^/(?<id>[^/]+)/?(?<rest>.*)$").matcher(req.getPathInfo());
            if (matcher.find()){
                String id = matcher.group("id");
                String rest = matcher.group("rest");
                if (rest.isBlank()){
                    releaseConnection(id, resp);
                }else{
                    resp.sendError(404);
                }
            }
        }
    }
    private void releaseConnection(String id, HttpServletResponse resp) throws IOException {
        System.out.println("ReleaseConnectionServlet");
    }

    private void releaseAllConnections(HttpServletResponse resp) throws IOException {
        System.out.println("ReleaseAllConnectionServlet");
    }
}
