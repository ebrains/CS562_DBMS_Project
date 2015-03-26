import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class TestClass {
	static String[] arr;
	String usr ="postgres";
	String pwd ="a";
	String url ="jdbc:postgresql://localhost:5432/postgres";
	
	public static void main(String[] args) {
		
		TestClass dbmsass1 = new TestClass();
	    dbmsass1.connect();
	    
		
		File file = new File("File.txt");
		try {
			FileReader fr = new FileReader(file);
			List<String> lineList = new ArrayList<String>();
			BufferedReader bf = new BufferedReader(fr);
			String line;
			while((line = bf.readLine()) != null){
				lineList.add(line);
			}
			arr = lineList.get(1).split(", ");
			
			for(String attribute : arr){
				System.out.println(attribute);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dbmsass1.retreive();
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
	//Function to generate the code for run query
	 void generator_code(){
		 try{
			 File file = new File("D:/generator_code.java");
			 if(!file.exists()) 
				 file.createNewFile(); 
		 } catch(Exception e) {
			 System.out.print("Fail to create");
		 }
	 } 
	 //Function to print the String 
	 void print_str(String content){
		 //File file = new File("D:/generator_code.java");
		 try{
			 FileWriter fw = new FileWriter("D:/generator_code.java", true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write(content);
			 bw.close();
		 } catch(Exception e) {
			 System.out.print("Fail to write");
		 }
	 }
	 
	 //Function to transfer the database variable type to java type
	 void judge(String str){
		 switch (str){
		 	case "integer":
		 		print_str("	int ");
		 		break;
		 	case "character varying":
		 		print_str("	String ");
		 		break;
		 	case "character":
		 		print_str("	char ");
		 		break; 
		 	default:
		 		break;
		 }
	 }
	 //Function to retreive data from database
	 void retreive(){
		 generator_code();
	     String content = "public class FaiStruct {\r\n";
	     print_str(content);
		 try {
			Connection con = DriverManager.getConnection(url, usr, pwd);    //connect to the database using the password and username
	        System.out.println("Success connecting server!");
	        ResultSet rs;          			 //resultset object gets the set of values retreived from the database
	        boolean more;
	        
	        int i=1,j=0;
	        Statement st = con.createStatement();   //statement created to execute the query
	        String ret = "select * from Information_schema.columns where table_name = 'sales'";
	        rs = st.executeQuery(ret);              //executing the query 
	    
	        more=rs.next();                         //checking if more rows available
	        while(more)
	        {
	        	for(String attribute : arr){
	        		String [] arr1 = attribute.split("_");
	        		if (arr1.length > 1){
	        			if (rs.getString(4).equals(arr1[2])){
							judge(rs.getString(8)); 
							print_str(attribute+";\r\n"); 
						}
	        		} else {
	        			if (rs.getString(4).equals(attribute)){
							judge(rs.getString(8)); 
							print_str(attribute+";\r\n"); 
						}	
	        		}
	        		//System.out.printf("%s ",rs.getString(4)); 
					
				}
	      
	        	more=rs.next();
	        }
	        
		
		 } catch(SQLException e) {
	         System.out.println("Connection URL or username or password errors!");
	        e.printStackTrace();
	     }
		 content = "}\r\n"
				 +	"public class TestClass {\r\n"
				 +	"	public static void main(String[] args) {\r\n"
				 +	"		Statement st = con.createStatement();\r\n"
				 +  "		String ret = \"select * from sales\";\r\n" 
		         +	"		rs = st.executeQuery(ret);\r\n"
		         +	"		more=rs.next();\r\n" 
				 +	"	}\r\n"
				 +	"}\r\n";
		 print_str(content);
	 }
	 
}
