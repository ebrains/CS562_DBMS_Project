import java.io.*; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.sql.*;
import java.util.Arrays;


public class TestClass {
	
	public static void main(String[] args) {
		RunJdbc rj = new RunJdbc();
		rj.generatorCode();
		rj.printSegment(1, 8);
		rj.readInput();
		rj.printAttributes();
	    rj.printConstructor();
	    rj.printSegment(9, 61);
	    rj.printAlgorithm();
	    rj.printSegment(67, 1000);
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
			int count = 1;
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
	
	//Function to generate code for retrieving data from DB
	public void printAlgorithm(){
		List<String> projAttributes = Fai.getProjAttri_S();
		List<String> selectCondition  = Fai.getSelectCondition_Q();
		List<String> groupingAttri = Fai.getGa_V();
		List<String> aggreFunction = Fai.getAf_F();
		
		//Loop for each grouping variable
		for(int i = 0; i < Fai.getNumGV_N(); i++){
			String attriType = null; //Define grouping data attribute type for JDBC connection
			printStr("        	while(rs.next()) {");
			printStr("				" + selectCondition.get(i) + " {");
			//Generate key in HashMap for specified grouping attribute. E.X. "key = rs.getString("cust") + rs.getString("prod");"
			String key = "";
			for(int l = 0; l < groupingAttri.size(); l++){
				if(groupingAttri.get(l).equals("cust") || groupingAttri.get(l).equals("prod")){
					attriType = "String";
				}else if(groupingAttri.get(l).equals("state")){
					attriType = "CharacterStream";
				}else{
					attriType = "Int";
				}
				if(l == groupingAttri.size() - 1){
					key += ("rs.get" + attriType + "(\"" + groupingAttri.get(l) + "\")");  //Concatenate the key String for HashMap
				}else{
					key += ("rs.get" + attriType + "(\"" + groupingAttri.get(l) + "\") + ");  //Concatenate the key String for HashMap
				}
			}
			printStr("					key = " + key + ";");
			printStr("					if(map.containsKey(key)){");
			String aggreResultNew = null; //Assign aggregate result for adding new combination operation
			List<String> aggreResultNewList = new ArrayList<String>(); //List to store aggreResultNew
			//Generate code for updating aggregate values
			for(int k = 0; k < aggreFunction.size(); k++){
				String[] arr = aggreFunction.get(k).split("_");
				String aggreResultUpdate = null; //Assign aggregate result for update operation
				if(Integer.parseInt(arr[2]) == (i + 1)){  //Make sure the aggregate functions are in the current grouping variable
					switch (arr[0]){
				 	case "sum":
				 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " += rs.getInt(\"" + arr[1] + "\");"; 
				 		//Parse to E.X. "map.get(key).sum_quant_1 += rs.getInt("quant")";
				 		aggreResultNew = "fs." + aggreFunction.get(k) + " += rs.getInt(\"" + arr[1] + "\");"; 
				 		//Parse to E.X. "fs.sum_quant_1 += rs.getInt("quant")";
				 		aggreResultNewList.add(aggreResultNew);
				 		break;
				 	case "cnt":
				 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " ++;"; 
				 		//Parse to E.X. "map.get(key).cnt_quant_1++";
				 		aggreResultNew = "fs." + aggreFunction.get(k) + " ++;"; 
				 		//Parse to E.X. "fs.cnt_quant_1++";	
				 		aggreResultNewList.add(aggreResultNew);
				 		break;
				 	case "max":
				 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " < rs.getInt(\"" + arr[1]+ "\"))\n"
				 				+ "							map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
				 		//Parse to E.X. "if (map.get(key).max_quant_1 < rs.getInt("quant"))
						//	map.get(key).max_quant_1 = rs.getInt("quant")";
				 		aggreResultNew = "fs." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1] + "\");"; 
				 		//fs.max_quant_1 = rs.getInt("quant");
				 		aggreResultNewList.add(aggreResultNew);
				 		break;
				 	case "min":
				 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " > rs.getInt(\"" + arr[1]+ "\"))\n"
				 				+ "							map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
				 		aggreResultNew = "fs." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1] + "\");";
				 		aggreResultNewList.add(aggreResultNew);
				 		break;
				 	case "avg":
				 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " = (int) (map.get(key).sum_" + arr[1] + "_" + arr[2] + "/map.get(key).cnt_" + arr[1] + "_" + arr[2] + ");";  
				 		//Parse to E.X. "map.get(key).avg_quant_1 = (int) (map.get(key).sum_quant_1/map.get(key).cnt_quant_1)";
				 		aggreResultNew = "fs." + aggreFunction.get(k) + " = (int) (fs.sum_" + arr[1] + "_" + arr[2] + "/fs.cnt_" + arr[1] + "_" + arr[2] + ");"; 
				 		//Parse to E.X. "fs.avg_quant_1 = (int) (fs.sum_quant_1/fs.cnt_quant_1)";
				 		aggreResultNewList.add(aggreResultNew);
				 		break;
					}	
				}
				printStr("						" + aggreResultUpdate);
			}
			//Generate code for adding new combination of grouping attributes
			printStr("					}else {");
			printStr("						FaiStruct fs = new FaiStruct();");
			//Generate code for assigning attribute values
			for (int l = 0; l < groupingAttri.size(); l++) {
				printStr("						fs." + groupingAttri.get(l) + " = rs.get"
						+ attriType + "(\"" + groupingAttri.get(l) + "\");");
				// Parse to E.X. "fs.cust = rs.getString("cust");"
			}
			//Generate code for assigning aggregate values
			for(int m = 0; m < aggreResultNewList.size(); m++){
				printStr("						" + aggreResultNewList.get(m));
				//Pare to E.X. "fs.sum_quant_1 += rs.getInt("quant");"
			}
			
			printStr("						map.put(key, fs);\n"
					+ "					}\n" 
					+ "				}\n" 
					+ "			}\n");
		}
		
		//Generate code for printing out projected attributes and aggregate functions
		for(int i = 0; i < projAttributes.size(); i++){
			if(i == projAttributes.size() - 1){
				printStr("			System.out.printf(\"%-7s  \\n\", \"" + projAttributes.get(i) + "\");");
			}else{
				printStr("			System.out.printf(\"%-7s  \", \"" + projAttributes.get(i) + "\");"); 
			}
		}
		//Generate code for printing out showing results statements
		printStr("			Iterator<String> iter = map.keySet().iterator();");
		printStr("			while(iter.hasNext()){");
		printStr("				FaiStruct fs = map.get(iter.next());");
		//Generate code for printing out showing grouping attributes statements
		for(int i = 0; i < groupingAttri.size(); i++){
			printStr("				System.out.printf(\"%-7s  \", fs." + groupingAttri.get(i) + ");"); //System.out.printf("%-7s  ", fs.cust);
		}
		//Generate code for printing out showing aggregate functions statements
		for(int i = 0; i < aggreFunction.size(); i++){
			if(i ==  aggreFunction.size() - 2 && !projAttributes.contains(aggreFunction.get(i+1))){
				printStr("				System.out.printf(\"%11s  \\n\", fs." + aggreFunction.get(i) + ");");
				break;
			}
			if(projAttributes.contains(aggreFunction.get(i))){
				if(i == aggreFunction.size() - 1){
					printStr("				System.out.printf(\"%11s  \\n\", fs." + aggreFunction.get(i) + ");");
				}else{
					printStr("				System.out.printf(\"%11s  \", fs." + aggreFunction.get(i) + ");"); //System.out.printf("%-7s  ", fs.avg_quant_1);
				}
	 		}
		}
		printStr("			}");
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
			
			arr = lineList.get(9).split(", ");
			attribute_set = Arrays.asList(arr);
			Fai.setSelectCondition_Q(attribute_set);
			System.out.println(Fai.getSelectCondition_Q());
			
			arr = lineList.get(11).split(", ");
			attribute_set = Arrays.asList(arr);
			Fai.setHavingCondition_G(attribute_set);
			System.out.println(Fai.getHavingCondition_G());
			
			bf.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		        		con_sen.add(attribute + (judge(rs.getString("data_type")) == "int"?" = 0":" = null") + ";");
		        	}
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
		return list;
	}
}
