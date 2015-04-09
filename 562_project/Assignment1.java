import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Assignment1 {

	class FaiStruct { 
		String cust;
		String prod;
		long sum_quant_0;
		long sum_quant_1;
		int cnt_quant_1;
		int avg_quant_1;
		int max_quant_2;

		FaiStruct() {
			cust = null;
			prod = null;
			sum_quant_0 = 0;
			sum_quant_1 = 0;
			cnt_quant_1 = 0;
			avg_quant_1 = 0;
			max_quant_2 = 0;
		}

    }

    public static void main(String[] args) {
    	Assignment1 as = new Assignment1();
    	as.retrieve();
    }
    
    private PreparedStatement ps = null;
	private Connection conn = null;
	private ResultSet rs = null, rs1 = null, rs2 = null;

    public Connection getConn() throws SQLException, ClassNotFoundException {
		// set up database's user name, password and URL
		String usr = "postgres";
		String pwd = "ericwang9079";
		String url = "jdbc:postgresql://localhost:5432/postgres";
		Class.forName("org.postgresql.Driver");
		System.out.println("Success loading Driver!");
		conn = DriverManager.getConnection(url, usr, pwd);
		System.out.println("Success connecting server!");
		return conn;
	}
	
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

    //Function to retrieve from the database and process on the result set received
    
    public void retrieve(){
		
        try {
        	conn = this.getConn();    //connect to the database using the password and username
			System.out.println("Success connecting server!");
			ps = conn.prepareStatement("select * from sales"); 
	        Map<String, FaiStruct> map = new HashMap<String, FaiStruct>();
        	//each time caculate one [F] (aggregate function)
        	//first time build HashMap and fill grouping variable and caculate F0, next n time caculate F2 to Fn 
        	//read the EMF paper page 6 will help you to understand the algorithm
			long sum_quant_0 = 0;
			for (int i = 0; i < 3; i++) {
				rs = ps.executeQuery();  //ResultSet object gets the set of values retrieved from the database
            	boolean more;
            	more=rs.next();                         //checking if more rows available

            	String key = null;
				while(more) {
					if(i==0) {
						if (rs.getInt("year") == 1990) {
							key = rs.getString("cust") + rs.getString("prod");
							if(map.containsKey(key)){
								map.get(key).sum_quant_0 += rs.getInt("quant");
							}else {
								FaiStruct fs = new FaiStruct();
								fs.cust = rs.getString("cust");
								fs.prod = rs.getString("prod");
								fs.sum_quant_0 += rs.getInt("quant");
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
								if (rs.getInt("year") == 1990 && fs.cust.equals(rs.getString("cust")) && fs.prod.equals(rs.getString("prod"))) {
									map.get(key).sum_quant_1 += rs.getInt("quant");
									map.get(key).cnt_quant_1 ++;
									map.get(key).avg_quant_1 = (int) (map.get(key).sum_quant_1/map.get(key).cnt_quant_1);
								}
								break;
							case 2:
								if (rs.getInt("year") == 1991 && fs.cust.equals(rs.getString("cust")) && fs.prod.equals(rs.getString("prod"))) {
									if (map.get(key).max_quant_2 < rs.getInt("quant"))
										map.get(key).max_quant_2 = rs.getInt("quant");
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
			System.out.printf("%-7s  ", "sum_quant_0");
			System.out.printf("%-7s  ", "sum_quant_1");
			System.out.printf("%-7s  ", "cnt_quant_1");
			System.out.printf("%-7s  ", "avg_quant_1");
			System.out.printf("%-7s  \n", "max_quant_2");
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()){
				FaiStruct fs = map.get(iter.next());
				if (fs.avg_quant_1 < fs.max_quant_2) {
					System.out.printf("%-7s  ", fs.cust);
					System.out.printf("%-7s  ", fs.prod);
					System.out.printf("%11s  ", fs.sum_quant_0);
					System.out.printf("%11s  ", fs.sum_quant_1);
					System.out.printf("%11s  ", fs.cnt_quant_1);
					System.out.printf("%11s  ", fs.avg_quant_1);
					System.out.printf("%11s  \n", fs.max_quant_2);
				}
			}
        } catch(SQLException e) {
        	System.out.println("Connection URL or username or password errors!");
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			this.close(conn, ps, rs); //close the connection
	    }
    }    
}
