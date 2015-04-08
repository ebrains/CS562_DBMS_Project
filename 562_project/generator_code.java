import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class assignment1 {

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
    String usr ="postgres";
    String pwd ="a";
    String url ="jdbc:postgresql://localhost:5432/postgres";

    public static void main(String[] args) {
        
        assignment1 dbmsass1 = new assignment1();
        dbmsass1.connect();
        dbmsass1.retrieve();
    }

    //Function to connect to the database

    void connect(){
        try {
            Class.forName("org.postgresql.Driver");     //Loads the required driver
        	System.out.println("Success loading Driver!");
        } catch(Exception exception) {
        	System.out.println("Fail loading Driver!");
        	exception.printStackTrace();
        }
    }

    //Function to retrieve from the database and process on the result set received
    
    void retrieve(){

        try {
        	Connection con = DriverManager.getConnection(url, usr, pwd);    //connect to the database using the password and username
        	System.out.println("Success connecting server!");
        	ResultSet rs, rs1, rs2;          			 //resultset object gets the set of values retreived from the database
        	boolean more;
        	
        	Statement st = con.createStatement();   //statement created to execute the query
        	String ret = "select * from sales";
        	rs = st.executeQuery(ret);              //executing the query 
        	more=rs.next();                         //checking if more rows available
        	
            // define the database
        	FaiStruct[] sale_rec = new FaiStruct[500];
            for (int i = 0; i < 500; i++)
            	sale_rec[i] = new FaiStruct();

            int dataSize = 0;
        	
        	while(more)
        	{
        		if (rs.getInt(5) == 1990) {
        			int flag = 0;
            		for (int k = 0; k < dataSize; k++) {
        				if (rs.getString(1).equals(sale_rec[k].cust)||rs.getString(2).equals(sale_rec[k].prod)) {
        					flag = 1;
        					sale_rec[k].sum_quant_1 += rs.getInt(7);
        					sale_rec[k].cnt_quant_1++;
        					sale_rec[k].avg_quant_1 = sale_rec[k].sum_quant_1/sale_rec[k].cnt_quant_1;
        					if (sale_rec[k].max_quant_1 < rs.getInt(7))
        						sale_rec[k].max_quant_1 = rs.getInt(7);
        				}
        			}
        			if (flag == 0){
        				sale_rec[dataSize].cust = rs.getString(1);
        				sale_rec[dataSize].prod = rs.getString(2);
        				sale_rec[dataSize].sum_quant_1 += rs.getInt(7);
    					sale_rec[dataSize].cnt_quant_1++;
    					sale_rec[dataSize].avg_quant_1 = sale_rec[dataSize].sum_quant_1/sale_rec[dataSize].cnt_quant_1;
    					if (sale_rec[dataSize].max_quant_1 < rs.getInt(7))
    						sale_rec[dataSize].max_quant_1 = rs.getInt(7);
    					dataSize++;
        			}
        		}  		       
            	more=rs.next();
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
        }  
    }    
}
