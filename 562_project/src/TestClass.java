import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Arrays;

public class TestClass {
	
	public static void main(String[] args) {
		RunJdbc rj = new RunJdbc();
		rj.generatorCode();
		rj.printSegment(0, 6);
		rj.readInput();
		rj.connect();
		rj.printAttributes();
	    rj.printConstructor();
	    rj.printSegment(7, 1000);
	}
	 
}

class RunJdbc{
	
	private PreparedStatement ps = null;
	private Connection conn = null;
	private ResultSet rs = null;
	private FaiStruct Fai = new FaiStruct();
	private List<String> con_sen = new ArrayList<String>();	//record strings for sentences in Fai constructor
	
	public FaiStruct getFai() {
		return Fai;
	}

	public void setFai(FaiStruct fai) {
		Fai = fai;
	}

	public List<String> getCon_sen() {
		return con_sen;
	}

	public void setCon_sen(List<String> con_sen) {
		this.con_sen = con_sen;
	}

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
	
	public void printAttributes() {
		List<String> list = retrieveType(Fai.getGa_V());
		for (String str : list)
			printStr(str);
		for (String agg : Fai.getAf_F()) {
			String[] arr = agg.split("_");
			printStr(judge(arr[0]) + agg + ";");
			con_sen.add(agg + " = 0;");
		}
	}
	
	public void printConstructor(){
		printStr("\r\n		FaiStruct() {");
		for (String sen : con_sen)
			printStr("			" + sen);
		printStr("		}\r\n");
	}
	
	//Function to print some fixed code segments
	public void printSegment(int begin, int end){
		File file = new File("code segment for generator.txt");
		try {
			//read all line into lineList
			FileReader fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String line;
			int count = 0;
			while((line = bf.readLine()) != null){
				if (begin <= count && count <= end) 
					printStr(line);
				count++;
			}
			bf.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Function to read input file and initialize FaiStruct
	public void readInput(){
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
			
			bf.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    //Function to connect to the database
	public void connect(){
		 try {
	        Class.forName("org.postgresql.Driver");     //Loads the required driver
	        System.out.println("Success loading Driver!");
	     } catch(Exception exception) {
	        System.out.println("Fail loading Driver!");
	        exception.printStackTrace();
	     }
	 }
	
	//Function to generate the code for run query
	public void generatorCode(){
		 try{
			 File file = new File("Assignment1.java");
			 if(!file.exists()) 
				 file.createNewFile(); 
			 //initial code
			 FileWriter fw = new FileWriter("Assignment1.java");
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write("");
			 bw.close();
			 //print_str(content);
		 } catch(Exception e) {
			 System.out.print("Fail to create");
		 }
	 } 
	
	 //Function to print the String 
	public void printStr(String content){
		 //File file = new File("D:/generator_code.java");
		 try{
			 FileWriter fw = new FileWriter("Assignment1.java", true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write(content+"\r\n");
			 bw.close();
		 } catch(Exception e) {
			 System.out.print("Fail to write");
		 }
	 }
	
	//Function to transfer the database variable type to java type
	public String judge(String str){
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
	public List<String> retrieveType(List<String> attribute_set){
		List<String> list = new ArrayList<String>(); 
		try {
			conn = this.getConn();    //connect to the database using the password and username
			ps = conn.prepareStatement("select * from Information_schema.columns where table_name = 'sales'"); 
			rs = ps.executeQuery();  //ResultSet object gets the set of values retrieved from the database
	        System.out.println("Success connecting server!");    
	    
	        while(rs.next()){
	        	// if length = 1, it means this is a element is database, find the type in Information_schema
	        	for (String attribute : attribute_set) {
	        		if (rs.getString("column_name").equals(attribute)){
	        			list.add(judge(rs.getString("data_type")) + attribute + ";");
		        		//print_str(judge(rs.getString("data_type")) + attribute + ";");
		        		con_sen.add(attribute + (judge(rs.getString("data_type")) == "int"?" = 0":" = null") + ";");
		        	}
	        	}
        		//System.out.printf("%s ",rs.getString(4)); 
	        }
		 } catch(SQLException e) {
	         System.out.println("Connection URL or username or password errors!");
	        e.printStackTrace();
	     } catch (ClassNotFoundException e) {
			e.printStackTrace();
	     } finally {
			this.close(conn, ps, rs); //close the connection
	     }
		return list;
	}
}
