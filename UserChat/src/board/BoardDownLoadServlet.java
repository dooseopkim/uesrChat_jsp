package board;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BoardDownLoadServlet
 */
@WebServlet("/BoardDownLoadServlet")
public class BoardDownLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String boardID = request.getParameter("boardID");
		if(boardID == null || boardID.equals("")){
			request.getSession().setAttribute("messageType", "오류 메세지");
			request.getSession().setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		String root = request.getSession().getServletContext().getRealPath("/");	//실제 서버의 물리적인 경로
		String savePath = root + "upload";	//업로드 폴더
		String fileName = "";
		String realFile = "";
		BoardDAO boardDAO = new BoardDAO();
		fileName = boardDAO.getFile(boardID);	
		realFile = boardDAO.getRealFile(boardID);
		if(fileName.equals("") || realFile.equals("")){	//파일이 존재하지 않는 경우
			request.getSession().setAttribute("messageType", "오류 메세지");
			request.getSession().setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		/* 파일 다운로드 */
		InputStream in = null;
		ServletOutputStream os = null;
		File file = null;
		boolean skip = false;
		String client = "";
		try{
			try{
				file = new File(savePath, realFile);	//실제 저장된 파일 불러오기
				in = new FileInputStream(file);			//Input스트림에 넣어주기
			} catch (FileNotFoundException e){
				skip = true;
			}
			client = request.getHeader("User-Agent");	//클라이언트 브라우저 정보
			response.reset();
			response.setContentType("application/octet-stream");	
			response.setHeader("Content-Description", "JSP Generated Data");	//JSP로 생성한 데이터입니다.
			if(!skip){
				if(client.indexOf("MSIE") != -1){	//익스플로러, 크롬부라우저 확인
					response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("K5C5601"),"ISO8859_1"));
				}else{
					fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					response.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
				}
				response.setHeader("Content-Length", "" + file.length());	//클라이언트로 전송한 데이터의 길이
				
				os =  response.getOutputStream();
				byte b[] = new byte[(int)file.length()];	//버퍼 역할
				int leng = 0;
				while((leng = in.read(b)) > 0){
					os.write(b, 0, leng);
				}
			}else{
				response.setContentType("text/html; charset=UTF-8");
				request.getSession().setAttribute("messageType", "오류 메세지");
				request.getSession().setAttribute("messageContent", "파일을 찾을 수 없습니다.");
				response.sendRedirect("index.jsp");
				return;
			}
			os.flush();
			os.close();
			in.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
