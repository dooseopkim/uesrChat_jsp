package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.SHA256;

@WebServlet("/UserEmailCheckServlet")
public class UserEmailCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String userEmail = request.getParameter("userEmail");
		if(userEmail == null || userEmail.equals("")) response.getWriter().write("-1");
		response.getWriter().write(new UserDAO().emailCheck(userEmail)+"");
	}

}
