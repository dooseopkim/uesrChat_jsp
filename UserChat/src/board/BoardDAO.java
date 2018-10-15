package board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import chat.ChatDTO;
import user.UserDTO;


public class BoardDAO {
	
	DataSource dataSource;
	
	public BoardDAO() {
		try {
			InitialContext initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/UserChat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 게시글 등록 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param userID : 회원 아이디,
	 *		  userPassword : 비밀번호,
	 *		  userName : 이름,
	 *		  userAge : 나이,
	 *		  userGender : 성별,
	 *		  userEmail : 이메일,
	 *		  userProfile : 프로필(사진)
	 * 
	 * */
	public int write(String userID,String boardTitle, String boardContent, String boardFile, String boardRealFile) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "INSERT INTO board SELECT ?, IFNULL((SELECT MAX(boardID) + 1 FROM board),1), ?, ?, NOW(), 0, ?, ?, IFNULL((SELECT MAX(boardGroup) + 1 FROM board), 0), 0, 0";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.setString(2, boardTitle);
			pstmt.setString(3, boardContent);
			pstmt.setString(4, boardFile);
			pstmt.setString(5, boardRealFile);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}		
	
	/**
	 * 게시글 불러오기 함수
	 * 
	 * @author kds
	 * @since 2018.10.13
	 * @param boardID : 게시글 ID
	 * 
	 * */
	public BoardDTO getBoard(String boardID) {
		BoardDTO board = new BoardDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM board WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, boardID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardDate(rs.getString("boardDate").substring(0, 11));
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setBoardGroup(rs.getInt("boardGroup"));
				board.setBoardSequence(rs.getInt("boardSequence"));
				board.setBoardLevel(rs.getInt("boardLevel"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return board;	
	}
	
	/**
	 * 게시글 불러오기 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param fromID : 보내는 회원ID,
	 * 		  toID : 받는 회원ID,
	 * 		   -fromID와 toID로 채팅의 전체 메세지를 불러오기 위해 두 가지 경우의수
	 *         -A가 B에게 보낸 메세지, B가 A에게 보낸 메세지를 전부 불러온다.
	 * 		  number : 가져올 건수(몇 개의 메세지를 불러올 것인지)
	 * 
	 * */		
	public ArrayList<BoardDTO> getBoardList(){
		ArrayList<BoardDTO> boardList = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM board ORDER BY boardGroup DESC, boardSequence ASC";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			boardList = new ArrayList<BoardDTO>();
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardDate(rs.getString("boardDate").substring(0, 11));
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setBoardGroup(rs.getInt("boardGroup"));
				board.setBoardSequence(rs.getInt("boardSequence"));
				board.setBoardLevel(rs.getInt("boardLevel"));
				boardList.add(board);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			} 
		}
		return boardList;
	}
	
	/**
	 * 조회수 올림 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param userID : 회원 아이디,
	 *		  userPassword : 비밀번호,
	 *		  userName : 이름,
	 *		  userAge : 나이,
	 *		  userGender : 성별,
	 *		  userEmail : 이메일,
	 *		  userProfile : 프로필(사진)
	 * 
	 * */
	public int hit(String boardID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "UPDATE board SET boardHit = boardHit + 1 WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, boardID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}
	
	/**
	 * 게시글 첨부파일 불러오기 함수
	 * 
	 * @author kds
	 * @since 2018.10.13
	 * @param boardID : 게시글 ID
	 * 
	 * */
	public String getFile(String boardID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT boardFile FROM board WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, boardID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("boardFile");
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";	
	}
	
	/**
	 * 게시글 첨부파일 불러오기 함수
	 * 
	 * @author kds
	 * @since 2018.10.13
	 * @param boardID : 게시글 ID
	 * 
	 * */
	public String getRealFile(String boardID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT boardRealFile FROM board WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, boardID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("boardRealFile");
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";	
	}
	
	/**
	 * 게시글 수정 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param userID : 회원 아이디,
	 *		  userPassword : 비밀번호,
	 *		  userName : 이름,
	 *		  userAge : 나이,
	 *		  userGender : 성별,
	 *		  userEmail : 이메일,
	 *		  userProfile : 프로필(사진)
	 * 
	 * */
	public int update(String boardID,String boardTitle, String boardContent, String boardFile, String boardRealFile) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "UPDATE board SET boardTitle = ?, boardContent = ?, boardFile = ?, boardRealFile = ? WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, boardTitle);
			pstmt.setString(2, boardContent);
			pstmt.setString(3, boardFile);
			pstmt.setString(4, boardRealFile);
			pstmt.setInt(5, Integer.parseInt(boardID));
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}	
	
	/**
	 * 게시글 등록 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param boardID : 게시글 아이디
	 * 
	 * */
	public int delete(String boardID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "DELETE FROM board WHERE boardID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(boardID));
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}
	
	/**
	 * 답변 등록 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param userID : 회원 아이디,
	 *		  userPassword : 비밀번호,
	 *		  userName : 이름,
	 *		  userAge : 나이,
	 *		  userGender : 성별,
	 *		  userEmail : 이메일,
	 *		  userProfile : 프로필(사진)
	 * 
	 * */
	public int reply(String userID,String boardTitle, String boardContent, String boardFile, String boardRealFile, BoardDTO parent) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "INSERT INTO board SELECT ?, IFNULL((SELECT MAX(boardID) + 1 FROM board),1), ?, ?, NOW(), 0, ?, ?, ?, ?, ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.setString(2, boardTitle);
			pstmt.setString(3, boardContent);
			pstmt.setString(4, boardFile);
			pstmt.setString(5, boardRealFile);
			pstmt.setInt(6, parent.getBoardGroup());
			pstmt.setInt(7, parent.getBoardSequence() + 1);
			pstmt.setInt(8, parent.getBoardLevel() + 1);
			
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}	
	
	/**
	 * 답변 등록 함수
	 * 
	 * @author kds
	 * @since 2018.10.10
	 * @param userID : 회원 아이디,
	 *		  userPassword : 비밀번호,
	 *		  userName : 이름,
	 *		  userAge : 나이,
	 *		  userGender : 성별,
	 *		  userEmail : 이메일,
	 *		  userProfile : 프로필(사진)
	 * 
	 * */
	public int replyUpdate(BoardDTO parent) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "UPDATE board SET boardSequence = boardSequence + 1 WHERE boardGroup = ? AND boardSequence > ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, parent.getBoardGroup());
			pstmt.setInt(2, parent.getBoardSequence());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;	//데이터 베이스 오류
	}
}
