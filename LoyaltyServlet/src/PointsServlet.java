import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class PointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        int pointsToUpdate = Integer.parseInt(request.getParameter("points"));
        String operation = request.getParameter("operation");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/loyalty_card_app?serverTimezone=UTC", "root", "root");
            
            PreparedStatement psCheck = con.prepareStatement("SELECT loyalty_points FROM users WHERE username = ?");
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int currentPoints = rs.getInt("loyalty_points");
                int newPoints = "add".equals(operation) ? currentPoints + pointsToUpdate : currentPoints - pointsToUpdate;
                
                if (newPoints < 0) {
                    out.println("<html><body>");
                    out.println("You cannot have a negative balance.");
                    out.println("<br/><a href='points.html'>Go back</a>");
                    out.println("</body></html>");
                    return;
                }
                
                PreparedStatement psUpdate = con.prepareStatement("UPDATE users SET loyalty_points = ? WHERE username = ?");
                psUpdate.setInt(1, newPoints);
                psUpdate.setString(2, username);
                psUpdate.executeUpdate();
                session.setAttribute("loyalty_points", newPoints);

                out.println("<html><body>");
                out.println("Your loyalty points have been updated. Your new balance is: " + newPoints);
                out.println("<br/><a href='points.html'>Go back</a>");
                out.println("</body></html>");
            } else {
                out.println("<html><body>");
                out.println("User not found.");
                out.println("<br/><a href='points.html'>Go back</a>");
                out.println("</body></html>");
            }
            
            con.close();
        } catch (Exception e) {
            out.println("<html><body>");
            out.println("Error: " + e.getMessage());
            out.println("<br/><a href='points.html'>Go back</a>");
            out.println("</body></html>");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}