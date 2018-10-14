package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.SHA256;

@WebServlet("/UserEmailAuthServlet")
public class UserEmailAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String code = request.getParameter("code");
		String userID = null;
		String userEmail = null;
		UserDAO userDAO = new UserDAO();
		if(request.getSession().getAttribute("userID") != null) {
			userID = (String) request.getSession().getAttribute("userID");
		}
		if(request.getSession().getAttribute("userID") == null) {
			request.getSession().setAttribute("messageType", "오류 메세지");
			request.getSession().setAttribute("messageContent", "로그인한 상태에서 메일을 확인해주세요.");
			response.sendRedirect("login.jsp");
			return;
		}
		userEmail = userDAO.getUser(userID).getUserEmail();
		boolean isRight = (SHA256.getSHA256(userEmail).equals(code))? true:false;
		if(isRight) {
			userDAO.emailAuthCheck(userID);
			request.getSession().setAttribute("messageType", "성공 메세지");
			request.getSession().setAttribute("messageContent", "이메일 인증에 성공했습니다.");
			response.sendRedirect("index.jsp");
			return;
		}else {
			request.getSession().setAttribute("messageType", "오류 메세지");
			request.getSession().setAttribute("messageContent", "유효하지 않은 코드입니다.");
			response.sendRedirect("index.jsp");
			return;
		}
	}

}
