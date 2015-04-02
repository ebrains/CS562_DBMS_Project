import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Arrays;

public class TestClass {
	static final FaiStruct Fai = new FaiStruct();
	final static List<String> con_sen = new ArrayList<String>();	//record strings for sentences in Fai constructor
	String usr ="postgres";
	String pwd ="a";
	String url ="jdbc:postgresql://localhost:5432/postgres";
	
	public static void main(String[] args) {
		generator_code();
		print_segment(0, 6);
		read_input();
		TestClass dbmsass1 = new TestClass();
	    dbmsass1.connect();
	    dbmsass1.retrieve_type(Fai.getGa_V());
	    for (String attribute : Fai.getAf_F()) {
	    	String [] arr1 = attribute.split("_");
	    	print_str(judge(arr1[0])+attribute+";"); 
			con_sen.add(attribute+" = 0;");
	    }
	  
	    print_constructor();
		print_segment(7, 1000);
	}

	static void print_constructor(){
		print_str("\r\n		FaiStruct() {");
		for (String sen : con_sen)
			print_str("			"+sen);
		print_str("		}\r\n");
	}
	//Function to print some fixed code segments
	static void print_segment(int begin, int end){
		File file = new File("code segment for generator.txt");
		try {
			//read all line into lineList
			FileReader fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String line;
			int count = 0;
			while((line = bf.readLine()) != null){
				if (begin <= count && count <= end) print_str(line);
				count++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Function to read input file and initialize FaiStruct
	static void read_input(){
		File file = new File("File1.txt");
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
			 FileWriter fw = new FileWriter("generator_code.java");
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write("");
			 bw.close();
			 //print_str(content);
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
			 bw.write(content+"\r\n");
			 bw.close();
		 } catch(Exception e) {
			 System.out.print("Fail to write");
		 }
	 }
	//Function to transfer the database variable type to java type
	 static String judge(String str){
		switch (str){
	 	case "sum":
	 		return "		long ";
	 	case "integer":
	 		return "		int ";
	 	case "character varying":
	 		return "		String ";
	 	case "character":
	 		return "		char ";
	 	default:
	 		return "		int ";
		}			
	 }
	 //Function to retrieve data type from database
	 void retrieve_type(List<String> attribute_set){		
		 
		 try {
			Connection con = DriverManager.getConnection(url, usr, pwd);    //connect to the database using the password and username
	        System.out.println("Success connecting server!");
	        ResultSet rs;          			 //ResultSet object gets the set of values retrieved from the database
	        boolean more;
	        
	        Statement st = con.createStatement();   //statement created to execute the query
	        String ret = "select * from Information_schema.columns where table_name = 'sales'";
	        rs = st.executeQuery(ret);              //executing the query 
	    
	        more=rs.next();                         //checking if more rows available
	        while(more)
	        {
	        	// if length = 1, it means this is a element is database, find the type in Information_schema
	        	for (String attribute : attribute_set) {
	        		if (rs.getString(4).equals(attribute)){
		        		print_str(judge(rs.getString(8))+attribute+";");
		        		con_sen.add(attribute+(judge(rs.getString(8))=="int"?" = 0":" = null")+";");
		        	}
	        	}
        		//System.out.printf("%s ",rs.getString(4)); 
	        	more=rs.next();
	        }
		 } catch(SQLException e) {
	         System.out.println("Connection URL or username or password errors!");
	        e.printStackTrace();
	     }
		 
	 }
	 
}
