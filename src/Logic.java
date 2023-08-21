import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Logic {

	List<Integer> NUTaskList = new ArrayList<Integer>(Arrays.asList(2136, 2137, 2139, 2140));

	public static List<ChainTask> getChainTaskNotInUsed() {

		Connection conn = JDBCUtility.getInstance().getConnection();

		List<ChainTask> list = new ArrayList<ChainTask>();
		try {
			String sql = "Select * FROM empdb.chain_task where CurrentTask in (2136,2137,2139,2140) or NextTask in (2139,2140,2136,2137);";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				ChainTask chainTask = new ChainTask();
				chainTask.setChainId(rs.getInt(1));
				chainTask.setCurrentTask(rs.getInt(2));
				chainTask.setNextTask(rs.getInt(3));
				chainTask.setOrderBy(rs.getInt(4));

				list.add(chainTask);
			}
			// list.forEach(System.out::println);
			stmt.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;

	}

	public List<ChainTask> getListOfChainTaskDetails(Integer chainId) throws SQLException {
		List<ChainTask> listTask = new ArrayList<ChainTask>();
		Connection conn = JDBCUtility.getInstance().getConnection();
		String sql = "Select * FROM empdb.chain_task where ChainID =" + chainId;
		Statement stmt1 = conn.createStatement();
		ResultSet rs1 = stmt1.executeQuery(sql);
		try {
			while (rs1.next()) {
				ChainTask chainTask = new ChainTask();
				chainTask.setChainId(rs1.getInt(1));
				chainTask.setCurrentTask(rs1.getInt(2));
				chainTask.setNextTask(rs1.getInt(3));
				chainTask.setOrderBy(rs1.getInt(4));

				listTask.add(chainTask);
			}

			stmt1.close();
			rs1.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("getListOfChainTaskDetails :: " + listTask);
		return listTask;
	}

	public List<ChainTask> updateTheOrder(Integer chainId) throws SQLException {
		List<ChainTask> listTask = new ArrayList<ChainTask>();
		Connection conn2 = JDBCUtility.getInstance().getConnection();
		String sql = "Select * FROM empdb.chain_task where ChainID =" + chainId + " order by OrderBy asc";
		Statement stmt2 = conn2.createStatement();
		ResultSet rs2 = stmt2.executeQuery(sql);
		try {
			int OrderBy = 1;
			while (rs2.next()) {
				ChainTask chainTask = new ChainTask();
				chainTask.setChainId(rs2.getInt(1));
				chainTask.setCurrentTask(rs2.getInt(2));
				chainTask.setNextTask(rs2.getInt(3));
				if (rs2.getInt(4) != OrderBy) {
					Connection conn3 = JDBCUtility.getInstance().getConnection();
					String sql1 = "update empdb.chain_task set OrderBy = " + OrderBy + " where ChainID =" + chainId
							+ " AND CurrentTask = " + rs2.getInt(2) + " AND NextTask = " + rs2.getInt(3);
					System.out.println("SQL Update Query :: " + sql1);
					Statement stmt3 = conn3.createStatement();
					stmt3.executeUpdate(sql1);
					conn3.close();
					chainTask.setOrderBy(OrderBy);
				} else {
					chainTask.setOrderBy(rs2.getInt(4));
				}

				listTask.add(chainTask);
				OrderBy++;
			}
			stmt2.close();
			rs2.close();
			conn2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("getListOfChainTaskDetails :: "+listTask);
		return listTask;

	}

	public void updateAndDelete() {
		deleteChainWhichCurrentAndNextTaskNotInUsed();

		List<ChainTask> list = getChainTaskNotInUsed();

		Set<Integer> chineIdList = list.stream().map(e -> e.getChainId()).collect(Collectors.toSet());

		// System.out.println("Chain Id which contain task Not in used ::
		// "+chineIdList);

		Connection conn = JDBCUtility.getInstance().getConnection();

		try {
			List<ChainTask> listtask ;
			for (Integer chainId : chineIdList) {
				listtask = updateTheOrder(chainId);

				List<ChainTask> NoUsedList = list.stream().filter(e -> e.getChainId() == chainId).toList();
				ChainTask previousTask = null;
				int index = 0; // This will hold the current index
				for (ChainTask CTL : listtask) {
					if (index > 0) { // Check to avoid accessing out of bounds
						previousTask = listtask.get(index - 1);
}
					for (ChainTask NUL : NoUsedList) {
						if (CTL.getCurrentTask() == NUL.getCurrentTask() || CTL.getNextTask() == NUL.getNextTask()) {
							
							if (NUTaskList.contains(previousTask.getNextTask()) && NUTaskList.contains(CTL.getCurrentTask())) {
								
								Connection conn3 = JDBCUtility.getInstance().getConnection();
								String sql1 = "update empdb.chain_task set NextTask = " + CTL.getNextTask() + " where ChainID =" + chainId
										+ " AND CurrentTask = " +previousTask.getCurrentTask() + " AND NextTask = " +previousTask.getNextTask();
								Statement stmt3 = conn3.createStatement();
								stmt3.executeUpdate(sql1);
																
								String DeleteSql = "Delete FROM empdb.chain_task where ChainID =" + chainId+" AND CurrentTask = " +CTL.getCurrentTask() +" AND NextTask = " +CTL.getNextTask();
								Statement stmt4 = conn3.createStatement();
								stmt4.executeUpdate(DeleteSql);
								conn3.close();
								listtask.clear();
								listtask = updateTheOrder(chainId);
								
							}
							
							if(!NUTaskList.contains(previousTask.getNextTask()) && !NUTaskList.contains(CTL.getCurrentTask()) && NUTaskList.contains(CTL.getNextTask()))
							{
								Connection conn3 = JDBCUtility.getInstance().getConnection();
								String sql1 = "update empdb.chain_task set NextTask = 0 where ChainID =" + chainId
										+ " AND CurrentTask = " +CTL.getCurrentTask() + " AND NextTask = " +CTL.getNextTask();
								Statement stmt3 = conn3.createStatement();
								stmt3.executeUpdate(sql1);
								
								listtask.clear();
								listtask = updateTheOrder(chainId);
							}
							
						}
					}
					index++; // Increment the index at the end of the loop iteration

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void deleteChainWhichCurrentAndNextTaskNotInUsed() {
		Connection conn = JDBCUtility.getInstance().getConnection();
		try {
			String sql = "Delete FROM empdb.chain_task where CurrentTask in (2136,2137,2139,2140) AND NextTask in (2139,2140,2136,2137);";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {			e.printStackTrace();
		} finally {
			try {				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Logic l = new Logic();
		l.updateAndDelete();
	}
}
