public class FaiStruct {
	String cust;
	int sum_quant_1;
	int sum_quant_2;
	int sum_quant_3;
	int cnt_quant_1;
	int cnt_quant_3;
	int avg_quant_1;
	int avg_quant_3;
}
public class TestClass {
	public static void main(String[] args) {
		Statement st = con.createStatement();
		String ret = "select * from sales";
		rs = st.executeQuery(ret);
		more=rs.next();
	}
}
