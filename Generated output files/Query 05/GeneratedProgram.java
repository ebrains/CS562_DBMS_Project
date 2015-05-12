package edu.stevens.cs562.emf.target;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GeneratedProgram {

	class FaiStruct { 
		String cust;
		String prod;
		long sum_quant_1;
		int cnt_quant_1;
		int avg_quant_1;
		long sum_quant_2;
		int cnt_quant_2;
		int avg_quant_2;

		FaiStruct() {
			cust = null;
			prod = null;
			sum_quant_1 = 0;
			cnt_quant_1 = 0;
			avg_quant_1 = 0;
			sum_quant_2 = 0;
			cnt_quant_2 = 0;
			avg_quant_2 = 0;
		}

    }

    public static void main(String[] args) {
    	GeneratedProgram gp = new GeneratedProgram();
    	gp.retrieve();
    }
    
    private static final String USER = "postgres";
	private static final String PWD = "ericwang9079";
	private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String SQLDRIVER = "org.postgresql.Driver";
    private PreparedStatement ps = null;
	private Connection conn = null;
	private ResultSet rs = null;

	/**
	 * Get database connection
	 * @return 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
    public Connection getConn() throws SQLException, ClassNotFoundException {
		Class.forName(SQLDRIVER);
		System.out.println("Success loading Driver!");
		conn = DriverManager.getConnection(URL, USER, PWD);
		System.out.println("Success connecting server!");
		return conn;
	}
	
	/**
	 * Close database connection
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    /**
	 * Function to retrieve from the database and process on the result set received
	 */
    public void retrieve(){
		
        try {
        	conn = this.getConn();   
			System.out.println("Success connecting server!");
			ps = conn.prepareStatement("select * from sales"); 
	        Map<String, FaiStruct> map = new HashMap<String, FaiStruct>();
			for (int i = 0; i < 2; i++) {
				rs = ps.executeQuery(); 
            	boolean more;
            	more=rs.next();                         
            	String key = null;
				while(more) {
					if(i==0) {
						 {
							key = rs.getString("cust") + rs.getString("prod");
							if(map.containsKey(key)){
							} else {
								FaiStruct fs = new FaiStruct();
								fs.cust = rs.getString("cust");
								fs.prod = rs.getString("prod");
								map.put(key, fs);
							}
						}

					}else {
	        			Iterator<String> iter = map.keySet().iterator();
						while(iter.hasNext()){
							key = iter.next();
							FaiStruct fs = map.get(key);
	        				switch (i) {
							case 1:
								if (fs.cust.equals(rs.getString("cust")) && fs.prod.equals(rs.getString("prod"))) {
									map.get(key).sum_quant_1 += rs.getInt("quant");
									map.get(key).cnt_quant_1 ++;
									map.get(key).avg_quant_1 = (int) (map.get(key).sum_quant_1/map.get(key).cnt_quant_1);
								}
								if (!fs.cust.equals(rs.getString("cust")) && fs.prod.equals(rs.getString("prod"))) {
									map.get(key).sum_quant_2 += rs.getInt("quant");
									map.get(key).cnt_quant_2 ++;
									map.get(key).avg_quant_2 = (int) (map.get(key).sum_quant_2/map.get(key).cnt_quant_2);
								}
								break;
	        				default:
	            				break;
	        				}
						}
					}
					more = rs.next();
				}
			}
			System.out.printf("%-7s  ", "cust");
			System.out.printf("%-7s  ", "prod");
			System.out.printf("%-7s  ", "avg_quant_1");
			System.out.printf("%-7s  \n", "avg_quant_2");
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()){
				FaiStruct fs = map.get(iter.next());
				 {
					System.out.printf("%-7s  ", fs.cust);
					System.out.printf("%-7s  ", fs.prod);
					System.out.printf("%11s  ", fs.avg_quant_1);
					System.out.printf("%11s  \n", fs.avg_quant_2);
				}
			}
        } catch(SQLException e) {
        	System.out.println("Connection URL or username or password errors!");
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			this.close(conn, ps, rs); 
	    }
    }    
}
