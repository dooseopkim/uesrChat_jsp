package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class UserDAO {

		DataSource dataSource;
		
		public UserDAO() {
			try {
				InitialContext initContext = new InitialContext();
				Context envContext = (Context) initContext.lookup("java:/comp/env");
				dataSource = (DataSource) envContext.lookup("jdbc/UserChat");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 회원 로그인 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userID : 회원 아이디,
		 * 		  userPassword : 회원 비밀번호
		 * 
		 * */
		public int login(String userID, String userPassword) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT * FROM USER userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if(rs.getString("userPassword").equals(userPassword)) {
						return 1;	//로그인에 성공
					}
					return 2;	//비밀번호가 틀림
				}else {
					return 0;	//해당 사용자가 존재하지 않음
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
			return -1;	//데이터 베이스 오류
		}
		
		/**
		 * 아이디 중복체크 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userID : 회원 아이디
		 * 
		 * */
		public int registerCheck(String userID) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT * FROM USER userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery();
				if (rs.next() || userID.equals("")) {
					return 0;	//이미 존재하는 회원
				}else {
					return 1;	//가입 가능한 회원 아이디
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
			return -1;	//데이터 베이스 오류
		}		
		
		/**
		 * 회원가입 함수
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
		public int register(String userID,String userPassword, String userName, String userAge, String userGender, String userEmail, String userProfile) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "INSERT INTO USER VALUES(?,?,?,?,?,?,?)";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				pstmt.setString(2, userPassword);
				pstmt.setString(3, userName);
				pstmt.setInt(4, Integer.parseInt(userAge));
				pstmt.setString(5, userGender);
				pstmt.setString(6, userEmail);
				pstmt.setString(7, userProfile);
				rs = pstmt.executeQuery();
				if (rs.next() || userID.equals("")) {
					return 0;	//이미 존재하는 회원
				}else {
					return 1;	//가입 가능한 회원 아이디
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
			return -1;	//데이터 베이스 오류
		}				
}
