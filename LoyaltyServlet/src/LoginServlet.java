import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/loyalty_card_app?serverTimezone=UTC", "root", "root");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                int points = rs.getInt("loyalty_points");
                session.setAttribute("loyalty_points", points);
                response.sendRedirect("points.html");
            } else {
                out.println("<html><body>");
                out.println("<p>Username or password is incorrect. Please try again.</p>");
                out.println("<a href=\"login.html\">Back to Login</a>");
                out.println("</body></html>");
            }

            con.close();
        } catch (Exception e) {
            out.println("<html><body>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("<a href=\"login.html\">Back to Login</a>");
            out.println("</body></html>");
        } finally {
            out.close();
        }
    }
}
