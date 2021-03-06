package DAO;

import java.sql.Connection;
import model.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class DBConnection {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public DBConnection() {
		try {
			String dbURL = "jdbc:mysql://localhost:3306/accountBook?serverTimezone=UTC";
			String dbID = "root";
			String dbPassword = "1234";

			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			System.out.println(">>>DBConnection Consumer Err<<<");
			System.out.println(e.getMessage());
		}

	}

	public User login(String id, String pw) { // 로그인 메소드

		String SQL = "SELECT * FROM user where id = ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			System.out.println(" >>> SQL : " + SQL + "<<<");
			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getString("pw").equals(pw)) {
					return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
							rs.getString(6), rs.getString(7), rs.getString(8));
				} else {
					System.out.println("Password erro");
					return null;
				}
			}
			System.out.println(">>>Login False <<<");
			return null;
		} catch (Exception e) {
			System.out.println(">>>DBConnection login method<<<");
			System.out.println(e.getMessage());
		}
		System.out.println("db err");
		return null;
	}


	public boolean editLine(AutoAccountList aac) { // 가계부 라인 수정 메소드

		String SQL = "UPDATE list SET todate = ?, contents = ?, cost = ?, mileage = ? where line_no = ?";

		try {
			pstmt = conn.prepareStatement(SQL);

			pstmt.setString(1, aac.getTodate());
			pstmt.setString(2, aac.getContent());
			pstmt.setInt(3, aac.getCost());
			pstmt.setInt(4, aac.getMileage());
			pstmt.setString(5, aac.getLine_no());

			System.out.println("Edit SQL >>>> " + SQL);
			System.out.println(aac.getTodate() + "\t" + aac.getContent() + "\t" + aac.getCost() + "\t"
					+ aac.getMileage() + "\t line_no> " + aac.getLine_no());
			int result = pstmt.executeUpdate();

			if (result != 0) return true;
			
		} catch (Exception e) {
			System.out.println(">>>DBConnection EditLine Methode Err<<<");
			System.out.println(e.getMessage());
		}
		System.out.println("db err");
		return false;
	}

	public AutoAccountList findLine(String line_no) { // 차계부 라인 찾는 메소드

		String SQL = "SELECT * FROM list where line_no = ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, line_no);
			System.out.println(" >>> SQL : " + SQL + "<<<");
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return new AutoAccountList(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
						rs.getInt(5), rs.getString(6));
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection FindLine Method Err<<<");
			System.out.println(e.getMessage());
		}
		System.out.println("db err");
		return null;
	}

	public List<AutoAccountList> getHousekeepingList(String id) { // 차계부 아이디에 맞게 내용 가져오는 메소드
		String SQL = "select * from list where writer = ?";

		ArrayList<AutoAccountList> list = new ArrayList<>();

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			System.out.println(" >>> id : " + id + "<<<");
			System.out.println(" >>> SQL : " + SQL + "<<<");
			rs = pstmt.executeQuery();

			AutoAccountList item = null;

			while (rs.next()) {
				item = new AutoAccountList(rs.getString("line_no"), rs.getString("todate"), rs.getString("contents"),
						rs.getInt("cost"), rs.getInt("mileage"), rs.getString("writer"));
				list.add(item);

				System.out.println("생성 >>> " + item.getLine_no() + "|" + item.getContent());
			}
			System.out.println("list size = " + list.size() + "\t");
			return list;
		} catch (Exception e) {
			System.out.println(">>>DBConnection getHousekeepingList Err<<<");
			System.out.println(e.getMessage());
		}
		System.out.println("db err");
		return list;
	}

	public int register(String name, String id, String pw, String phone, String email, String career, String addr,
			String gender) {
		pstmt = null;
		ResultSet re = null;
		String SQL = "INSERT INTO user VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, name);
			pstmt.setString(2, id);
			pstmt.setString(3, pw);
			pstmt.setString(4, phone);
			pstmt.setString(5, email);
			pstmt.setString(6, career);
			pstmt.setString(7, addr);
			pstmt.setString(8, gender);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(">>>DBConnection register Methode Err<<<");
			System.out.println(e.getMessage());
		} finally {
			
		}
		return -1; // 오류
	}

	public String findId(String name, String phone) {

		boolean findSuccess = false;
		String id = null;
		String SQL = "select id from user where name= ? and phone = ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, name);
			pstmt.setString(2, phone);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				String id_db = rs.getString("id");
				id = rs.getString(1);
				findSuccess = (id != null) ? true : false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		if (findSuccess)
			return id;
		else
			return null;
	}

	public String findPw(String id, String phone) {

		boolean findSuccess = false;
		String pw = null;
		String SQL = "select pw from user where id= ? and phone = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			pstmt.setString(2, phone);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				String pw_db = rs.getString("pw");
				pw = rs.getString(1);
				findSuccess = (pw != null) ? true : false;
			}
		} catch (SQLException e) {
			System.out.println(">>>DBConnection FindPassword Methode Err<<<");
			System.out.println(e.getMessage());
		}

		if (findSuccess)
			return pw;
		else
			return null;
	}

	public int registerCheck(String id) {
		pstmt = null;
		ResultSet re = null;
		String SQL = "SELECT * FROM USER WHERE id = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			re = pstmt.executeQuery();
			if (re.next() || id.equals("")) {
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection registerCheck Methode Err<<<");
			System.out.println(e.getMessage());
		} 
		return -1; // 오류
	}
	
	public boolean Add_line(User user, AutoAccountList aal) {
		int num = 0;
		String SQL = "INSERT INTO list VALUES(?,?,?,?,?,?)";
		String SQL2 = "Select MAX(line_no) from list";
		try {
			pstmt = conn.prepareStatement(SQL2);
			System.out.println(" >>> SQL : " + SQL2 + "<<<");
			rs = pstmt.executeQuery();

			String result = null;
			int line_no = 0;

			if (rs.next())	line_no = rs.getInt(1);
			
			System.out.println("Max(line_no) : " + line_no++);

			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, line_no);
			pstmt.setString(2, aal.getTodate());
			pstmt.setString(3, aal.getContent());
			pstmt.setInt(4, aal.getCost());
			pstmt.setInt(5, aal.getMileage());
			pstmt.setString(6, user.getId());
			System.out.println(" >>> SQL : " + SQL2 + "<<<");
			int result2 = pstmt.executeUpdate();
			if (result2 >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection Add_line Methode Err<<<");
			System.out.println(e.getMessage());
		}
		return false;
	}

	public boolean deleteLine(String line_no) {

		String SQL = "DELETE FROM list WHERE line_no = ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, line_no);
			int result = pstmt.executeUpdate();
			return result > 1 ? true : false;
		} catch (Exception e) {
			System.out.println(">>>DBConnection deleteLine Methode Err<<<");
			System.out.println(e.getMessage());
		}
		return false;
	}

	public List<RepairCheck> getRepairList(User user) { // 차계부 아이디에 맞게 내용 가져오는 메소드
		String SQL = "select * from repair_info where driver_id = ? or technician_id  = ?";

		ArrayList<RepairCheck> list = new ArrayList<>();

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getId());
			System.out.println(" >>> SQL : " + SQL + "<<<");
			rs = pstmt.executeQuery();

			RepairCheck item = null;

			while (rs.next()) {
				item = new RepairCheck(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getInt(10));
				list.add(item);

				System.out.println("생성 >>> " + item.getRepair_no());
			}
			System.out.println("list size = " + list.size() + "\t");
			return list;
		} catch (Exception e) {
			System.out.println(">>>DBConnection getRepairList Methode Err<<<");
			System.out.println("exception" + e.getMessage());
		}
		System.out.println("db err");
		return list;
	}

	public boolean Add_Repairline(RepairCheck rc) {
		String SQL = "insert into repair_info values( ?,?,?,?,?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, rc.getRepair_no());
			pstmt.setString(2, rc.getDriver_id());
			pstmt.setString(3, rc.getDriver_name());
			pstmt.setString(4, rc.getTechnician_id());
			pstmt.setString(5, rc.getTechnician_name());
			pstmt.setString(6, rc.getContents());
			pstmt.setString(7, rc.getStart_date());
			pstmt.setString(8, rc.getExpect_date());
			pstmt.setString(9, rc.getFinish_date());
			pstmt.setInt(10, rc.getCost());
			System.out.println(" >>> SQL : " + SQL + "<<<");
			int result2 = pstmt.executeUpdate();
			if (result2 >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection Add_Repairline Methode Err<<<");
			System.out.println(e.getMessage());
		}
		return false;
	}

	public RepairCheck createRepairCheck(String d_id, String con, String t_id, String t_name, String s_time,
			String e_time, String f_time, int cost) {
		String SQL = "select * from user where id = ?";
		String SQL2 = "Select MAX(repair_no) from repair_info";
		String d_name = null;
		int re_no = 0;
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, d_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				d_name = rs.getString("name");
			}
			System.out.println(" >>> SQL : " + SQL + "<<<");

			pstmt = conn.prepareStatement(SQL2);
			rs = pstmt.executeQuery();
			System.out.println(" >>> SQL : " + SQL2 + "<<<");
			if (rs.next()) {
				re_no = rs.getInt("MAX(repair_no)");
			}
			return new RepairCheck(++re_no, d_id, d_name, t_id, t_name, con, s_time, e_time, f_time, cost);
		} catch (Exception e) {
			System.out.println(">>>DBConnection createRepairCheck Methode Err<<<");
			System.out.println(e.getMessage());
		}

		return null;
	}

	public List<Repair> getRepairProgressList(String repair_no) { // 차계부 아이디에 맞게 내용 가져오는 메소드
		String SQL = "select * from repair where repair_no = ?";

		ArrayList<Repair> list = new ArrayList<>();

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, repair_no);
			System.out.println(" >>> SQL : " + SQL + "<<<");
			rs = pstmt.executeQuery();

			Repair item = null;

			while (rs.next()) {
				item = new Repair(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
				list.add(item);

				System.out.println("생성 >>> " + item.getRepair_no() + " | " + item.getContents() + " | "
						+ item.getDoday() + " | " + item.getWriter_id() + " | " + item.getImg() + " | ");
			}
			System.out.println("list size = " + list.size() + "\t");
			return list;
		} catch (Exception e) {
			System.out.println(">>>DBConnection getRepairProgressList Methode Err<<<");
			System.out.println(e.getMessage());
		}
		System.out.println("db err");
		return list;
	}

	public boolean Add_RepairProgressline(int repair_no, String contents, String doday, String writer_id,
			String FileName) {
		String SQL = "insert into repair values( ?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, repair_no);
			pstmt.setString(2, contents);
			pstmt.setString(3, doday);
			pstmt.setString(4, writer_id);
			pstmt.setString(5, FileName);

			System.out.println(" >>> SQL : " + SQL + "<<<");
			int result2 = pstmt.executeUpdate();
			if (result2 >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection Add_RepairProgressline Methode Err<<<");
			System.out.println(e.getMessage());
		}
		return false;
	}

	public String findData(String userid) {
		String result = "";

		String SQL = "select * from list where writer= ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();
			int cnt = 0;
			while (rs.next()) {
				if (cnt == 0) {
					result += rs.getString(3);
				} else {
					result = result + " " + rs.getString(3);
				}
				cnt++;
			}
			System.out.println("findData >> " + result);
			return result;
		} catch (Exception e) {
			System.out.println(">>>DBConnection findData Methode Err<<<");
			System.out.println(e.getMessage());
		}

		return result;
	}

	public int findDataCost(String userid) {
		int result = 0;

		String SQL = "select * from list where writer= ?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				result += rs.getInt(4);
			}
			System.out.println("findData >> " + result);
			return result;
		} catch (Exception e) {
			System.out.println(">>>DBConnection findDataCost Methode Err<<<");
			System.out.println(e.getMessage());
		}

		return result;
	}

	public int[] findDataCostChart(String userid, String nYear, String nMonth) {
		String SQL = "select * from list where writer= ?";
		int[] costCnt = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				String[] date = rs.getString("todate").split("-");

				if (date[0].equals(nYear)) {
					int month = Integer.parseInt(date[1]);
					costCnt[month-1] += rs.getInt("cost");
				}
			}
		} catch (Exception e) {
			System.out.println(">>>DBConnection findDataCostChart Methode Err<<<");
			System.out.println(e.getMessage());
		}
		return costCnt;
	}
	
}