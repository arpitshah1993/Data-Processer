package dataprocesser;
import java.io.FileWriter;
import java.sql.ResultSet;

public class DataWriter {
	public static void writeDataToExcel(String path,String stationName,ResultSet rs){
		try {
			FileWriter out = new FileWriter(path+"/"+stationName+".csv");
			out.write("YEAR"+"\t"+
					"MONTH"+"\t"+
					"SPNAME"+"\t"+
					"CITYNAME"+"\t"+
					"ADDR"+"\t"+
					"UNITS"+"\t"+
					"QUANTITY"+"\t"+
					"DAY"+"\t"+
					"HOUR"+"\n");
			while(rs.next()){
				out.write(rs.getString("YEAR")+"\t"+
						rs.getString("MONTH")+"\t"+
						rs.getString("SPNAME")+"\t"+
						rs.getString("CITYNAME")+"\t"+
						rs.getString("ADDR")+"\t"+
						rs.getString("UNITS")+"\t"+
						rs.getString("QUANTITY")+"\t"+
						rs.getString("DAY")+"\t"+
						rs.getString("HOUR")+"\n");
			}
			rs.close();
			out.close();
		} catch (Exception e) {
			LoggerJava.log(e.getStackTrace().toString());
		}

	}
}
