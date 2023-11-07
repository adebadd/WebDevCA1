import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (!password.equals(confirmPassword)) {
            out.println("<html><body>");
            out.println("<p>Passwords do not match. Please try again.</p>");
            out.println("<a href=\"register.html\">Back to Registration</a>");
            out.println("</body></html>");
            return;
        }

        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/loyalty_card_app?serverTimezone=UTC", "root", "root");

            PreparedStatement psCheck = con.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                out.println("<html><body>");
                out.println("<p>User already exists. Please try another username.</p>");
                out.println("<a href=\"register.html\">Back to Registration</a>");
                out.println("</body></html>");
            } else {
                PreparedStatement psInsert = con.prepareStatement("INSERT INTO users (username, password, loyalty_points) VALUES (?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setInt(3, 100);
                psInsert.executeUpdate();

                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("loyalty_points", 100);
                response.sendRedirect("points.html");
            }

            con.close();
        } catch (Exception e) {
            out.println("<html><body>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("<a href=\"register.html\">Back to Registration</a>");
            out.println("</body></html>");
        } finally {
            out.close();
        }
    }
}
