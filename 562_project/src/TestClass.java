import java.io.*; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
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
	    rj.printSegment(9, 59);
	    rj.printAlgorithm();
	    rj.printSegment(80, 1000);
	}
	 
}

class RunJdbc{
	
	//declare the members used in database connect and query
	private PreparedStatement ps = null;
	private Connection conn = null;
	private ResultSet rs = null;
	
	private FaiStruct Fai = new FaiStruct();
	private List<String> con_sen = new ArrayList<String>();	
	//con_sen will be used in the retrieveType function
	//it will record strings for sentences in Fai constructor (in the output java code - Assignment1.java)
	private Graph G = new Graph();
	
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
	
	//close connection
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
	
	//the member in Faistruct should be grouping attribute + aggregate function
	//printAttributes will get these members' data types and write these into output java code - Assignment1.java
	public void printAttributes() {
		//grouping attribute
		List<String> list = retrieveType(Fai.getGa_V());
		for (String str : list)
			printStr(str);
		//aggregate function
		for (String agg : Fai.getAf_F()) {
			String[] arr = agg.split("_");
			printStr(judge(arr[0]) + agg + ";");
			con_sen.add(agg + " = 0;");
		}
	}
	
	//print the constructor part of the Faistruct into output java code - Assignment1.java
	public void printConstructor(){
		printStr("\r\n		FaiStruct() {");
		for (String sen : con_sen)
			printStr("			" + sen);
		printStr("		}\r\n");
	}
	
	//Function to print some fixed code segments(from line number = begin to line number = end)
	public void printSegment(int begin, int end){
		File file = new File("code segment for generator.txt");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String line;
			int count = 1;	//line number
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
	
	//Function to generate the Algorithm part of the code 
	//the algorithm is about scan
	//At the first time we build HashMap and fill grouping attribute and calculate F0
	//next NumGV_N time calculate F1 to Fn 
	//read the EMF paper page 6 will help you to understand the algorithm
	public void printAlgorithm(){
		List<String> projAttributes = Fai.getProjAttri_S();
		List<String> selectCondition = Fai.getSelectCondition_Q();
		List<String> groupingAttri = Fai.getGa_V();
		List<String> aggreFunction = Fai.getAf_F();
		int NumGV_N = Fai.getNumGV_N();
		List<String> havingCondition = Fai.getHavingCondition_G();
		
		//the loop of scan begins: NumGV_N + 1 times
		printStr("\t\t	for (int i = 0; i < " + String.valueOf(G.max_indegree() + 1) + "; i++) {");
		printSegment(61, 65);
		printStr("\t\t\t	while(more) {");
		String attriType = null; //Define grouping data attribute type for JDBC connection
		printStr("\t\t\t\t	if(i==0) {");
		printStr("\t\t\t\t\t	" + selectCondition.get(0) + " {");
		
		List<String> attriTypeList = new ArrayList<String>();
		//Generate key in HashMap for specified grouping attribute. E.X. "key = rs.getString("cust") + rs.getString("prod");"
		String key = "";
		//decide the attriType to combine the string like rs.get[attriType]([grouping attribute])	E.X. rs.getString("cust")
		for(int l = 0; l < groupingAttri.size(); l++){
			if(groupingAttri.get(l).equals("cust") || groupingAttri.get(l).equals("prod")){
				attriType = "String";
			}else if(groupingAttri.get(l).equals("state")){
				attriType = "CharacterStream";
			}else{
				attriType = "Int";
			}
			attriTypeList.add(attriType);
			if(l == groupingAttri.size() - 1){
				key += ("rs.get" + attriType + "(\"" + groupingAttri.get(l) + "\")");  //Concatenate the key String for HashMap
			}else{
				key += ("rs.get" + attriType + "(\"" + groupingAttri.get(l) + "\") + ");  //Concatenate the key String for HashMap
			}
		}
		printStr("\t\t\t\t\t\t	key = " + key + ";");
		
		printStr("\t\t\t\t\t\t	if(map.containsKey(key)){");
		
		String aggreResultNew = null; //Assign aggregate result for adding new combination operation
		List<String> aggreResultNewList = new ArrayList<String>(); //List to store aggreResultNew
		//Generate code for updating aggregate values
		LinkedList<Integer> nodeSet = G.topoSort();
		if (nodeSet != null) {
			for (int i = 0; i < nodeSet.size(); i++) {
				printStr("\t\t\t\t\t\t\t	" + selectCondition.get(nodeSet.get(i)) + " {");
				aggreResultNewList.add(selectCondition.get(nodeSet.get(i)) + " {");
				for(int k = 0; k < aggreFunction.size(); k++){
					String[] arr = aggreFunction.get(k).split("_");
					String aggreResultUpdate = null; //Assign aggregate result for update operation
					if(Integer.parseInt(arr[2]) == nodeSet.get(i)){  //Make sure the aggregate functions are in the current grouping variable
						switch (arr[0]){
					 	case "sum":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " += rs.getInt(\"" + arr[1] + "\");"; 
					 		//Parse to E.X. "map.get(key).sum_quant_1 += rs.getInt("quant")";
					 		aggreResultNew = "fs." + aggreFunction.get(k) + " += rs.getInt(\"" + arr[1] + "\");"; 
					 		//Parse to E.X. "fs.sum_quant_1 += rs.getInt("quant")";
					 		aggreResultNewList.add("\t" + aggreResultNew);
				 		break;
					 	case "cnt":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " ++;"; 
					 		//Parse to E.X. "map.get(key).cnt_quant_1++";
					 		aggreResultNew = "fs." + aggreFunction.get(k) + " ++;"; 
					 		//Parse to E.X. "fs.cnt_quant_1++";	
					 		aggreResultNewList.add("\t" + aggreResultNew);
				 		break;
					 	case "max":
					 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " < rs.getInt(\"" + arr[1]+ "\"))\n"
					 				+ "\t\t\t\t\t\t\t	map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
					 		//Parse to E.X. "if (map.get(key).max_quant_1 < rs.getInt("quant"))
							//	map.get(key).max_quant_1 = rs.getInt("quant")";
					 		aggreResultNew = "fs." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1] + "\");"; 
					 		//fs.max_quant_1 = rs.getInt("quant");
					 		aggreResultNewList.add("\t" + aggreResultNew);
				 		break;
					 	case "min":
					 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " > rs.getInt(\"" + arr[1]+ "\"))\n"
					 				+ "\t\t\t\t\t\t\t	map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
					 		aggreResultNew = "fs." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1] + "\");";
					 		aggreResultNewList.add("\t" + aggreResultNew);
				 		break;
					 	case "avg":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " = (int) (map.get(key).sum_" + arr[1] + "_" + arr[2] + "/map.get(key).cnt_" + arr[1] + "_" + arr[2] + ");";  
					 		//Parse to E.X. "map.get(key).avg_quant_1 = (int) (map.get(key).sum_quant_1/map.get(key).cnt_quant_1)";
					 		aggreResultNew = "fs." + aggreFunction.get(k) + " = (int) (fs.sum_" + arr[1] + "_" + arr[2] + "/fs.cnt_" + arr[1] + "_" + arr[2] + ");"; 
					 		//Parse to E.X. "fs.avg_quant_1 = (int) (fs.sum_quant_1/fs.cnt_quant_1)";
					 		aggreResultNewList.add("\t" + aggreResultNew);
				 		break;
						}	
					}
					if (aggreResultUpdate != null)
						printStr("\t\t\t\t\t\t\t\t	" + aggreResultUpdate); 
								
				}
				aggreResultNewList.add("}");
				printStr("\t\t\t\t\t\t\t	}");
			}	
		}
		
		//Generate code for adding new combination of grouping attributes
		printStr("\t\t\t\t\t\t	} else {");
		printStr("\t\t\t\t\t\t\t	FaiStruct fs = new FaiStruct();");
		//Generate code for assigning attribute values
		for (int l = 0; l < groupingAttri.size(); l++) {
			printStr("\t\t\t\t\t\t\t	fs." + groupingAttri.get(l) + " = rs.get"
					+ attriTypeList.get(l) + "(\"" + groupingAttri.get(l) + "\");");
			// Parse to E.X. "fs.cust = rs.getString("cust");"
		}
		//Generate code for assigning aggregate values
		for(int m = 0; m < aggreResultNewList.size(); m++){
			printStr("\t\t\t\t\t\t\t	" + aggreResultNewList.get(m));
			//Pare to E.X. "fs.sum_quant_1 += rs.getInt("quant");"
		}

		printStr("\t\t\t\t\t\t\t	map.put(key, fs);\n"
				+ "\t\t\t\t\t\t	}\n"
				+ "\t\t\t\t\t	}\n"); 
		
		printStr("\t\t\t\t	}else {");
		printSegment(71, 75);
		
		//deal with the next NumGV_N times of scan
		//the method seems to be same as the previous one, due to the slightly different output formation, 
		//we have to do it again 
		nodeSet = G.topoSort();
		int loopNum = 1;
		while(nodeSet != null) {
			//Generate code for updating aggregate values
			printStr("\t\t\t\t\t\t	case " + String.valueOf(loopNum++) + ":");
			for (int i = 0; i < nodeSet.size(); i++) {
				printStr("\t\t\t\t\t\t\t	" + selectCondition.get(nodeSet.get(i)) + " {");
				for(int k = 0; k < aggreFunction.size(); k++){
					String[] arr = aggreFunction.get(k).split("_");
					String aggreResultUpdate = null; //Assign aggregate result for update operation
					if(Integer.parseInt(arr[2]) == nodeSet.get(i)){  //Make sure the aggregate functions are in the current grouping variable
						switch (arr[0]){
					 	case "sum":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " += rs.getInt(\"" + arr[1] + "\");"; 
					 		//Parse to E.X. "map.get(key).sum_quant_1 += rs.getInt("quant")";
					 		break;
					 	case "cnt":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " ++;"; 
					 		//Parse to E.X. "map.get(key).cnt_quant_1++";
					 		break;
					 	case "max":
					 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " < rs.getInt(\"" + arr[1]+ "\"))\n"
					 				+ "\t\t\t\t\t\t\t\t\t	map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
					 		//Parse to E.X. "if (map.get(key).max_quant_1 < rs.getInt("quant"))
					 		break;
					 	case "min":
					 		aggreResultUpdate = "if (map.get(key)." + aggreFunction.get(k) + " > rs.getInt(\"" + arr[1]+ "\"))\n"
					 				+ "\t\t\t\t\t\t\t\t\t	map.get(key)." + aggreFunction.get(k) + " = rs.getInt(\"" + arr[1]+ "\");";
					 		break;
					 	case "avg":
					 		aggreResultUpdate = "map.get(key)." + aggreFunction.get(k) + " = (int) (map.get(key).sum_" + arr[1] + "_" + arr[2] + "/map.get(key).cnt_" + arr[1] + "_" + arr[2] + ");";  
					 		//Parse to E.X. "map.get(key).avg_quant_1 = (int) (map.get(key).sum_quant_1/map.get(key).cnt_quant_1)";
					 		break;
						}
						printStr("\t\t\t\t\t\t\t\t	" + aggreResultUpdate);
					}
				}
				printStr("\t\t\t\t\t\t\t	}");
			}
			printStr("\t\t\t\t\t\t\t	break;");
			nodeSet = G.topoSort();
		}
		printSegment(77, 79);
		
		printStr("\t\t\t\t\t	}");

		printStr("\t\t\t\t	}");
		printStr("\t\t\t\t	more = rs.next();");

		printStr("\t\t\t	}");
		printStr("\t\t	}");
		
		//Generate code for printing out projected attributes and aggregate functions
		for(int i = 0; i < projAttributes.size(); i++){
			if(i == projAttributes.size() - 1){
				printStr("\t\t	System.out.printf(\"%-7s  \\n\", \"" + projAttributes.get(i) + "\");");
			}else{
				printStr("\t\t	System.out.printf(\"%-7s  \", \"" + projAttributes.get(i) + "\");"); 
			}
		}
		//Generate code for printing out showing results statements
		printStr("\t\t	Iterator<String> iter = map.keySet().iterator();");
		printStr("\t\t	while(iter.hasNext()){");
		printStr("\t\t\t	FaiStruct fs = map.get(iter.next());");
		
		//the having clause is one "if" sentence since we work out the whole map
		//we just judge whether the tuple should be print or not by it 
		printStr("\t\t\t	" + havingCondition.get(0) + " {");
		
		//Add "fs." before every attribute and aggregate functions
		List<String> parseProjAttributes = new ArrayList<String>();
		String[] columArray = {"cust","prod","day","month","year","state","quant"};
		List<String> columnList = Arrays.asList(columArray);
		for(int i = 0; i < projAttributes.size(); i++){
			String temp = projAttributes.get(i);
			StringBuilder sb = new StringBuilder(temp);
			//Add "fs." to every attributes and agg functions in Computation Expression. E.g. "avg_quant_1/avg_quant_2" to "fs.avg_quant_1/fs.avg_quant_2"
			if(projAttributes.get(i).contains("+") || projAttributes.get(i).contains("-") || projAttributes.get(i).contains("*") || projAttributes.get(i).contains("/")){
				for(int j = 0; j < aggreFunction.size(); j++){
					if(projAttributes.get(i).contains(aggreFunction.get(j))){
						int position = temp.indexOf(aggreFunction.get(j));
						sb.insert(position, "fs.");
						temp = sb.toString();
						while(position < projAttributes.get(i).length()){
							position = temp.indexOf(aggreFunction.get(j), position + aggreFunction.get(j).length());
							if(position != -1){
								sb.insert(position, "fs.");
								temp = sb.toString();
							}else{
								break;
							}
						}
					}
					temp = sb.toString();
				}
//				for(int j = 0; j < columArray.length; j++){
//					if(projAttributes.get(i).contains(columArray[j])){
//						int position = temp.indexOf(columArray[j]);
//						System.out.println(position);
//						sb.insert(position, "fs.");
//						temp = sb.toString();
//						while(position < projAttributes.get(i).length()){
//							position = temp.indexOf(columArray[j], position + columArray[j].length());
//							if(position != -1){
//								sb.insert(position, "fs.");
//								temp = sb.toString();
//							}else{
//								break;
//							}
//						}
//					}
//				}
			//Add "fs." before every attribute and aggregate functions
			}else{
				sb.insert(0, "fs.");
			}
			parseProjAttributes.add(sb.toString());
		}
		
		for(int i = 0; i < parseProjAttributes.size(); i++){
			if(i == parseProjAttributes.size() - 1){
				if(columnList.contains(projAttributes.get(i))){
					printStr("\t\t\t\t	System.out.printf(\"%-7s  \\n\", " + parseProjAttributes.get(i) + ");");
				}else{
					printStr("\t\t\t\t	System.out.printf(\"%11s  \\n\", " + parseProjAttributes.get(i) + ");");
				}
			}else{
				if(columnList.contains(projAttributes.get(i))){
					printStr("\t\t\t\t	System.out.printf(\"%-7s  \", " + parseProjAttributes.get(i) + ");");
				}else{
					printStr("\t\t\t\t	System.out.printf(\"%11s  \", " + parseProjAttributes.get(i) + ");");
				}
			}
		}
		
//		//Generate code for printing out showing grouping attributes statements
//		for(int i = 0; i < groupingAttri.size(); i++){
//			printStr("\t\t\t\t	System.out.printf(\"%-7s  \", fs." + groupingAttri.get(i) + ");"); //System.out.printf("%-7s  ", fs.cust);
//		}
//		//Generate code for printing out showing aggregate functions statements
//		for(int i = 0; i < aggreFunction.size(); i++){
//			if(i ==  aggreFunction.size() - 2 && !projAttributes.contains(aggreFunction.get(i+1))){
//				printStr("\t\t\t\t	System.out.printf(\"%11s  \\n\", fs." + aggreFunction.get(i) + ");");
//				break;
//			}
//			if(projAttributes.contains(aggreFunction.get(i))){
//				if(i == aggreFunction.size() - 1){
//					printStr("\t\t\t\t	System.out.printf(\"%11s  \\n\", fs." + aggreFunction.get(i) + ");");
//				}else{
//					printStr("\t\t\t\t	System.out.printf(\"%11s  \", fs." + aggreFunction.get(i) + ");"); //System.out.printf("%-7s  ", fs.avg_quant_1);
//				}
//	 		}
//		}
		printStr("\t\t\t	}");
		printStr("\t\t	}");
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
			//set the number of vertex
			G.setGraph(Integer.parseInt(str)+1);
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
			
			for (int i = 13; i < lineList.size(); i++) {
				arr = lineList.get(i).split(" ");
				G.InsertEdge(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));	//insert edge
			}
			
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
		        		con_sen.add(attribute + (judge(rs.getString("data_type")).equals("\t\tString ")?" = null" :" = 0") + ";");
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
