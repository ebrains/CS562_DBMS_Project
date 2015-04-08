import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Assignment1 {

	class FaiStruct { 
		String cust;
		String prod;
		long sum_quant_1;
		int cnt_quant_1;
		int avg_quant_1;
		int max_quant_1;

		FaiStruct() {
			cust = null;
			prod = null;
			sum_quant_1 = 0;
			cnt_quant_1 = 0;
			avg_quant_1 = 0;
			max_quant_1 = 0;
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
			
        	//each time caculate one [F] (aggregate function)
        	//first time build HashMap and fill grouping variable and caculate F0, next n time caculate F2 to Fn 
        	//read the EMF paper page 6 will help you to understand the algorithm
        	for (i = 0; i < numGV_N + 1; i++) {
    			rs = ps.executeQuery();  //ResultSet object gets the set of values retrieved from the database
            	
                // define the database
            	Map<String, FaiStruct> map = new HashMap<String, FaiStruct>();
            	
                String key = null;	
            	
            	more=rs.next();                         //checking if more rows available
            	
                //F0 initial 
                sum_quant_0 = cnt = max = 0;
                min = rs.getString("quant");
                
                while(more) {
            		if (i==0) {
            			if (such that [0] = where clause) {
            				//initial group attribute
            				key = rs.getString("cust") + rs.getString("prod");
        					if(map.containsKey(key)){
        	
        					}else {
        						FaiStruct fs = new FaiStruct();
        						fs.cust = rs.getString("cust");
        						fs.prod = rs.getString("prod");
        						map.put(key, fs);
        					}
            			}
            				
            			//update F0
            			sum_quant_0 += rs.getInt("quant");
            			cnt++
            			if max
            			if min
            			avg = sum / cnt
            			//waste many useless loop
            			Iterator iter = map.entrySet().iterator();
            			while (iter.hasNext()) {
            			　　Map.Entry entry = (Map.Entry) iter.next();
            			　　Object key = entry.getKey();
            			　　map.get(key).max_quant_0 = max;
            			}	fs.sum_quant_1 += rs.getInt("quant");
            				
            		}else {
            			Iterator iter = map.entrySet().iterator();
            			while (iter.hasNext()) {
            			　　Map.Entry entry = (Map.Entry) iter.next();
            			　　Object key = entry.getKey();
            			　　FaiStruct entry = entry.getValue();
            				if (such that [i]) {
            					//update attribute in Fi
            					sum +=;
            					cnt++
            					avg;
            					max min
            					map.get(key).max_quant_1 = max;
            				}

            			}
    				}
    			}        		
        		
        		
        	}
        	
        	


			System.out.printf("%-7s  ", "cust");
			System.out.printf("%-7s  ", "prod");
			System.out.printf("%-7s  ", "sum_quant_1");
			System.out.printf("%-7s  ", "cnt_quant_1");
			System.out.printf("%-7s  ", "avg_quant_1");
			System.out.printf("%-7s  \n", "max_quant_1");
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()){
				FaiStruct fs = map.get(iter.next());
				System.out.printf("%-7s  ", fs.cust);
				System.out.printf("%-7s  ", fs.prod);
				System.out.printf("%11s  ", fs.sum_quant_1);
				System.out.printf("%11s  ", fs.cnt_quant_1);
				System.out.printf("%11s  ", fs.avg_quant_1);
				System.out.printf("%11s  \n", fs.max_quant_1);
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
