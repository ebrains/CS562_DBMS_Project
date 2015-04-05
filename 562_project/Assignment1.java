import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
			ps = conn.prepareStatement("select * from sales"); 
			rs = ps.executeQuery();  //ResultSet object gets the set of values retrieved from the database
	        System.out.println("Success connecting server!");
        	
            // define the database
        	FaiStruct[] sale_rec = new FaiStruct[500];
            for (int i = 0; i < 500; i++)
            	sale_rec[i] = new FaiStruct();

            int dataSize = 0;
        	
        	while(rs.next()) {
        		if (rs.getInt("year") == 1990) {
        			int flag = 0;
            		for (int k = 0; k < dataSize; k++) {
        				if (rs.getString("cust").equals(sale_rec[k].cust) && rs.getString("prod").equals(sale_rec[k].prod)) {
        					flag = 1;
        					sale_rec[k].sum_quant_1 += rs.getInt("quant");
        					sale_rec[k].cnt_quant_1++;
        					sale_rec[k].avg_quant_1 = (int) (sale_rec[k].sum_quant_1/sale_rec[k].cnt_quant_1);
        					if (sale_rec[k].max_quant_1 < rs.getInt("quant"))
        						sale_rec[k].max_quant_1 = rs.getInt("quant");
        				}
        			}
        			if (flag == 0){
        				sale_rec[dataSize].cust = rs.getString("cust");
        				sale_rec[dataSize].prod = rs.getString("prod");
        				sale_rec[dataSize].sum_quant_1 += rs.getInt("quant");
    					sale_rec[dataSize].cnt_quant_1++;
    					sale_rec[dataSize].avg_quant_1 = (int) (sale_rec[dataSize].sum_quant_1/sale_rec[dataSize].cnt_quant_1);
    					sale_rec[dataSize].max_quant_1 = rs.getInt("quant");
    					dataSize++;
        			}
        		}  		       
        	}
        
        	for (int k = 0; k < dataSize; k++) {
        		System.out.printf("%-7s  ", sale_rec[k].cust);            //left aligned
        		System.out.printf("%-7s  ", sale_rec[k].prod);            //left aligned
        		System.out.printf("%-7s  ", sale_rec[k].avg_quant_1);            //left aligned
        		System.out.printf("%-7s  \n", sale_rec[k].max_quant_1);
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
