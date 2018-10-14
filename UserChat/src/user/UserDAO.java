package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import util.SHA256;


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
			String SQL = "SELECT * FROM USER WHERE userID = ?";
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
			String SQL = "SELECT * FROM USER WHERE userID = ?";
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
			String SQL = "INSERT INTO USER VALUES(?,?,?,?,?,?,?,?,'0')";
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
				pstmt.setString(8, SHA256.getSHA256(userEmail));
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
		 * 회원정보 불러오기 함수
		 * 
		 * @author kds
		 * @since 2018.10.13
		 * @param userID : 회원 아이디
		 * 
		 * */
		public UserDTO getUser(String userID) {
			UserDTO user = new UserDTO();
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT * FROM USER WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					user.setUserID(userID);
					user.setUserPassword(rs.getString("userPassword"));
					user.setUserName(rs.getString("userName"));
					user.setUserAge(rs.getInt("userAge"));
					user.setUserGender(rs.getString("userGender"));
					user.setUserEmail(rs.getString("userEmail"));
					user.setUserProfile(rs.getString("userProfile"));
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
			return user;	
		}	
		
		/**
		 * 회원정보 수정 함수
		 * 
		 * @author kds
		 * @since 2018.10.13
		 * @param userID : 회원 아이디,
		 *		  userPassword : 비밀번호,
		 *		  userName : 이름,
		 *		  userAge : 나이,
		 *		  userGender : 성별,
		 *		  userEmail : 이메일,
		 *		  userProfile : 프로필(사진)
		 * 
		 * */
		public int update(String userID,String userPassword, String userName, String userAge, String userGender, String userEmail) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			String SQL = "UPDATE user SET userPassword = ?, userName = ?, userAge = ?, userGender = ?, userEmail = ? WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userPassword);
				pstmt.setString(2, userName);
				pstmt.setInt(3, Integer.parseInt(userAge));
				pstmt.setString(4, userGender);
				pstmt.setString(5, userEmail);
				pstmt.setString(6, userID);
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
		 * 프로필정보 수정 함수
		 * 
		 * @author kds
		 * @since 2018.10.13
		 * @param userID : 회원 아이디,
		 *		  userPassword : 비밀번호,
		 *		  userName : 이름,
		 *		  userAge : 나이,
		 *		  userGender : 성별,
		 *		  userEmail : 이메일,
		 *		  userProfile : 프로필(사진)
		 * 
		 * */
		public int profile(String userID, String userProfile) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			String SQL = "UPDATE user SET userProfile = ? WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userProfile);
				pstmt.setString(2, userID);
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
		 * 특정한 사용자의 프로필 경로 출력 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userID : 회원 아이디
		 * 
		 * */
		public String getProfile(String userID) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT userProfile FROM USER WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if(rs.getString("userProfile").equals("")) {
						return "http://localhost:9001/UserChat/images/icon.jpg";
					}
					return "http://localhost:9001/UserChat/upload/" + rs.getString("userProfile");
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
			return "http://localhost:9001/UserChat/images/icon.jpg";
		}		

		/**
		 * 이메일 중복체크 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userEmail : 회원 이메일
		 * 
		 * */
		public int emailCheck(String userEmail) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT * FROM USER WHERE userEmail = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userEmail);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					return 0;	//이미 존재하는 이메일주소
				}else {
					return 1;	//가입 가능한 이메일주소
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
		 * 이메일 인증체크 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userID : 회원 아이디
		 * 
		 * */
		public int emailAuthCheck(String userID) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "SELECT * FROM USER WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery();
				String userEmailChecked = null;
				if (rs.next()) {
					userEmailChecked = rs.getString("userEmailChecked");
				}
				if(Integer.parseInt(userEmailChecked) == 0) {
					return 0;	//이메일 인증 X
				}else {
					return 1;	//이메일 인증 O
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
		 * 이메일 인증체크 함수
		 * 
		 * @author kds
		 * @since 2018.10.10
		 * @param userID : 회원 아이디
		 * 
		 * */
		public int emailAuthUpdate(String userID) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String SQL = "UPDATE user SET userEmailChecked = 1 WHERE userID = ?";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				return pstmt.executeUpdate();
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
