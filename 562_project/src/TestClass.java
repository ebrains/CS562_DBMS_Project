import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Arrays;

public class TestClass {
	static final FaiStruct Fai = new FaiStruct();
	String usr ="postgres";
	String pwd ="a";
	String url ="jdbc:postgresql://localhost:5432/postgres";
	
	public static void main(String[] args) {

		read_input();
		generator_code();
		TestClass dbmsass1 = new TestClass();
	    dbmsass1.connect();
	    dbmsass1.retrieve_type(Fai.getGa_V());
	    dbmsass1.retrieve_type(Fai.getAf_F());
	    String content;
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
	//Function to read input file
	static void read_input(){
		File file = new File("File.txt");
		try {
			//read all line into lineList
			FileReader fr = new FileReader(file);
			List<String> lineList = new ArrayList<String>();
			BufferedReader bf = new BufferedReader(fr);
			String line;
			while((line = bf.readLine()) != null){
				lineList.add(line);
			}
			//initialize all members in FaiStruct
			String[] arr = lineList.get(1).split(", ");
			List<String> attribute_set = Arrays.asList(arr);
			Fai.setProjAttri_S(attribute_set);
			System.out.println(Fai.getProjAttri_S());
			
			String str = lineList.get(3);
			//System.out.println(str);
			Fai.setNumGV_N(Integer.parseInt(str));
			System.out.println(Fai.getNumGV_N());
			
			arr = lineList.get(5).split(", ");
			attribute_set = Arrays.asList(arr);
			Fai.setGa_V(attribute_set);
			System.out.println(Fai.getGa_V());
			
			arr = lineList.get(7).split(", ");
			attribute_set = Arrays.asList(arr);
			Fai.setAf_F(attribute_set);
			System.out.println(Fai.getAf_F());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 static void generator_code(){
		 try{
			 File file = new File("generator_code.java");
			 if(!file.exists()) 
				 file.createNewFile(); 
			 //initial code
			 String content = "public class FaiStruct {\r\n";
			 print_str(content);
		 } catch(Exception e) {
			 System.out.print("Fail to create");
		 }
	 } 
	 //Function to print the String 
	 static void print_str(String content){
		 //File file = new File("D:/generator_code.java");
		 try{
			 FileWriter fw = new FileWriter("generator_code.java", true);
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
	 //Function to retrieve data from database
	 void retrieve_type(List<String> attribute_set){		
		 
		 try {
			Connection con = DriverManager.getConnection(url, usr, pwd);    //connect to the database using the password and username
	        System.out.println("Success connecting server!");
	        ResultSet rs;          			 //ResultSet object gets the set of values retreived from the database
	        boolean more;
	        
	        int i=1,j=0;
	        Statement st = con.createStatement();   //statement created to execute the query
	        String ret = "select * from Information_schema.columns where table_name = 'sales'";
	        rs = st.executeQuery(ret);              //executing the query 
	    
	        more=rs.next();                         //checking if more rows available
	        while(more)
	        {
	        	for(String attribute : attribute_set){
	        		String [] arr1 = attribute.split("_");
	        		if (arr1.length > 1){
	        			if (rs.getString(4).equals(arr1[1])){
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
		 
	 }
	 
}
