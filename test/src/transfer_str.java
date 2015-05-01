import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class transfer_str {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		transfer_str a = new transfer_str();
		a.readInput();
	}
	public String transferStr(String a) {
		//List<String> output = new ArrayList<String>();
		String[] logicSymbol = {" and ", " or "};
		for (String symbol : logicSymbol) {
			int result = a.indexOf(symbol);
			if (result >= 0) {
				String[] arr = a.split(symbol);
//				for (String x : arr)
//					System.out.println(x);
				String ifCondition = "if (";
				for (String x : arr) {
					ifCondition += transferCondition(x) +" && ";	
				}
				
				return (ifCondition.substring(0, ifCondition.length()-3)+")");
			}
		}
		//String[] arr = {a};
		return transferCondition(a);
	}
	public String rightvalue(String x) {
		//String[] x = arr;
		String ifCondition = "";
		
		String[] arr1 = x.split("_");
		if (arr1.length == 1) {
			ifCondition += arr1[0];
		} else if (arr1.length == 2) {
			if (arr1[0].equals("0") ) {
				String type;
				if (arr1[1].equals("cust")||arr1[1].equals("prod")||arr1[1].equals("state"))
					type = "String";
				else 
					type = "Int";
				ifCondition += "rs.get" + type + "(\"" + arr1[1] +"\")";
			} else {
				ifCondition += "fs." + arr1[1] ;
			}
		} else {
			ifCondition += "fs." + arr1[0] + "_" + arr1[1] + "_"  + arr1[2];
		}
		
		return ifCondition;
	}
	public String transferCondition(String a) {
		String[] compareSymbol = {" = ", " < ", " > ", " >= ", " <= ", " <> ", " != "};
		String[] operand = {"+", "-", "*", "/"};
		for (String symbol : compareSymbol) {
			int result = a.indexOf(symbol);
			if (result >= 0) {
				String[] arr = a.split(symbol);
				List<String> ifCondition_set = new ArrayList<String>();
				String ifCondition = ""; //Assign aggregate result for update operation
				int from=-1, to=0;	//dependency
				
				String x = arr[0];
//				System.out.println(x);
				String[] arr1 = x.split("_");
				if (arr1.length == 1) {
					if (arr1[0].indexOf('"') >= 0)	//impossible case
						ifCondition += arr1[0]; 
				} else if (arr1.length == 2) {
					if (arr1[0].equals("0") ) {
						String type;
						if (arr1[1].equals("cust")||arr1[1].equals("prod")||arr1[1].equals("state"))
							type = "String";
						else 
							type = "Int";
						ifCondition += "rs.get" + type + "(\"" + arr1[1] +"\")";
						from = 0;
					} else {
						ifCondition += "fs." + arr1[1] ;
						from = Integer.parseInt(arr1[0]);
					}
				} else {
					ifCondition += "fs." + arr1[0] + "_" + arr1[1] + "_"  + arr1[2];
					from = Integer.parseInt(arr1[2]);
				}
				
				if (ifCondition.indexOf("cust") >= 0||ifCondition.indexOf("prod") >= 0||ifCondition.indexOf("state") >= 0) {
					ifCondition += ".compareTo(";
					x = arr[1];
					boolean flag = true;
//					System.out.println(x);
					for (String o : operand) {
						if (x.indexOf(o) >= 0) {
							arr1 = x.split("\\"+o);
							ifCondition += rightvalue(arr1[0]) + o + rightvalue(arr1[1]);
							flag = false;
							to = max(rightto(arr1[0]), rightto(arr1[1]));
						}
					}
					if (flag) {
						ifCondition += rightvalue(x);
						to = rightto(x);
					}	
					ifCondition += ")";
					if (symbol.equals(" = "))
						ifCondition += " == " + "0";
					else if (symbol.equals(" <> ")) 
						ifCondition += " != " + "0";
					else 
						ifCondition += symbol + "0";
					
					//ifCondition += ")";
					//System.out.println(ifCondition);
					if (to != -1 && from != to)
						System.out.println(to+" "+from);
					return ifCondition;
				} else { 
					x = arr[1];
					boolean flag = true;
//					System.out.println(x);
					for (String o : operand) {
						if (x.indexOf(o) >= 0) {
							arr1 = x.split("\\"+o);
							if (symbol.equals(" = "))
								ifCondition += " == ";
							else if (symbol.equals(" <> ")) 
								ifCondition += " != ";
							else 
								ifCondition += symbol;
							ifCondition += rightvalue(arr1[0]) + o + rightvalue(arr1[1]);
							to = max(rightto(arr1[0]), rightto(arr1[1]));
							flag = false;
						}
					}
					if (flag) {
						if (symbol.equals(" = "))
							ifCondition += " == ";
						else if (symbol.equals(" <> ")) 
							ifCondition += " != ";
						else 
							ifCondition += symbol;
						ifCondition += rightvalue(x);
						to = rightto(x);
					}

					//ifCondition += ")";
					//System.out.println(ifCondition);
					if (to != -1 && from != to)
						System.out.println(to+" "+from);
					return ifCondition;
				}
					
				
				//return null;
			}
		}
		return null;
	}
	private int rightto(String x) {
		String[] arr1 = x.split("_");
		if (arr1.length == 1) {
			return -1;
		} else if (arr1.length == 2) {
			if (arr1[0].equals("0") ) {
				String type;
				return 0;
			} else {
				return Integer.parseInt(arr1[0]);
			}
		} else {
			return Integer.parseInt(arr1[2]);
		}
		//return -1;
	}
	private int max(int i, int j) {
		if (i > j) return i;
		else return j;
	}
	public void readInput(){
		File file = new File("File2.txt");
		try {
			//read all line into lineList
			FileReader fr = new FileReader(file);
			List<String> lineList = new ArrayList<String>();
			BufferedReader bf = new BufferedReader(fr);
			String line;
			while((line = bf.readLine()) != null){
				lineList.add(line);
			}
			
			String[] arr = lineList.get(10).split(", ");
			List<String> attribute_set = new ArrayList<String>();
			for (String a : arr) {
//				String[] x = transferStr(a);
//				transferCondition(x[0]);
				System.out.println(transferStr(a));
				//System.out.println(x[0]);
				//attribute_set.addAll(Arrays.asList(arr));	
			}
			
			
			
//			System.out.println(Fai.getSelectCondition_Q());
//			
//			arr = lineList.get(11).split(", ");
//			attribute_set = Arrays.asList(arr);
//			
//			System.out.println(Fai.getHavingCondition_G());
//			
//			for (int i = 13; i < lineList.size(); i++) {
//				arr = lineList.get(i).split(" ");
//				G.InsertEdge(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));	//insert edge
//			}
			
			bf.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
