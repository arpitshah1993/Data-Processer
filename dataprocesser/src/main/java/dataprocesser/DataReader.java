package dataprocesser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DataReader {
	 int count=0;
	Connection con;
	final String url="jdbc:mysql://localhost:3306/";
	final String dbName="stateData";
	final String USERNAME="root";
	final String PWD="Zaq12wsx";

	public String getPollutantName(int number){
		switch(number){
		case 1: 
			return "SO2";
		case 10:
			return "CO";
		case 5:
			return "PM10";
		case 6001:
			return "PM2.5";
		case 7:
			return "O3";
		case 8:
			return "NO2";
		default:
			return "Unknwn";
		}
	}

	public ResultSet getData(String StationName){
		try{
			Statement stmt = con.createStatement();
			String sql = "SELECT " +
					" YEAR,MONTH,SPNAME,CITYNAME,ADDR,UNITS,QUANTITY,DAY,HOUR " +
					" FROM  DATA "+
					"WHERE STATIONNAME = "+"'"+StationName+"'";
					
			ResultSet rset=stmt.executeQuery(sql);
			return rset;
		}
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}
		return null;
	}

	public void createDatabaseAndTable(){
		//deleting database if already exist
		try{
			Statement stmt=con.createStatement();  
			stmt.executeUpdate("drop database "+dbName);
		}
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}

		try {
			{
				//creating database
				Statement stmt=con.createStatement();  
				stmt.executeUpdate("create database "+dbName);
				con.close();
				//updating connection to database
				con=DriverManager.getConnection(url+dbName,USERNAME,PWD);
			}
			//creating table
			Statement stmt = con.createStatement();
			String sql = "CREATE TABLE DATA " +
					"(Id INTEGER not NULL AUTO_INCREMENT, " +
					" YEAR VARCHAR(5), " + 
					" MONTH VARCHAR(2), " + 
					" SPNAME VARCHAR(60), " + 
					" CITYNAME VARCHAR(100), " + 
					" ADDR VARCHAR(200), " + 
					" STATIONNAME VARCHAR(100), " + 
					" UNITS VARCHAR(100), " + 
					" QUANTITY VARCHAR(70), " + 
					" DAY VARCHAR(2), " + 
					" HOUR VARCHAR(2), " + 
					" PRIMARY KEY ( Id ))"; 
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}

	}

	public void doIndexing(){
		try{
			Statement stmt = con.createStatement();
			String sql = "CREATE INDEX idx " +
					" ON DATA(STATIONNAME)"; 
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}
	}

	public void insertData(String year,String month,String SPName, String cityName, String addr,String stationName, String units, String quantity, String day, String hour){
		try{ 
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO DATA (YEAR,MONTH,SPNAME,CITYNAME,ADDR,STATIONNAME,UNITS,QUANTITY,DAY,HOUR)" +
					" VALUES ("+"'"+ year+"'"+", "+"'"+month+"'"+", "+"'"+SPName+"'"+", "+"'"+cityName+"'"+", "+"'"+addr+"'"+", "+"'"+stationName+"'"+", "+"'"+units+"'"+", "+"'"+quantity+"'"+", "+"'"+day+"'"+", "+"'"+hour+"'"+")";

			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}
	}

	private String getQuantity(String unit, String quantity) {
		if(unit.contains("mg/m3"))
			return String.valueOf(Float.valueOf(quantity)/1000);
		else if(unit.contains("ng/m3"))
			return String.valueOf(Float.valueOf(quantity)*1000);
		else if(unit.contains("pg/m3"))
			return String.valueOf(Float.valueOf(quantity)*1000000);
		//System.out.println(unit);
		return quantity;
	}
	public String getUnit(String unitName){
		return String.valueOf(unitName.contains("ppm")?7:1);
	}

	public void writeFileToDatabase(int pollutant, File file,HashSet<String> station){
		String pollType=this.getPollutantName(pollutant);
		if(pollType.equals("Unknwn"))
			return;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			//org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			Object[] lines = br.lines().toArray();
			int length=lines.length;
			count+=length;
			for(int i=1;i<length;i++){
				String[] values=lines[i].toString().split("\\t");
				String[] startDate=values[10].split(" ");
				String[] date=startDate[0].split("-");
				String[] time=startDate[1].split(":");
				//DateTime dt = formatter.parseDateTime(startDate);
				String unit=getUnit(values[13]);
				String quantity=getQuantity(values[13],values[12]);
				station.add(values[4]);
				//this.insertData(String.valueOf(dt.getYear()),String.valueOf(dt.getMonthOfYear()), pollType, "X", "X",values[4],unit, quantity, String.valueOf(dt.getDayOfMonth()),String.valueOf(dt.getHourOfDay()));
				this.insertData(date[0],date[1], pollType, "X", "X",values[4],unit, quantity, date[2],time[0]);
			}

		} catch (Exception e) {
			LoggerJava.log(e.getMessage());
		}
	}
	
	public void closeConnection(){
		try {
			con.close();
		} catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}
	}
	public DataReader(){
		try {
			con=DriverManager.getConnection(url,"root","Zaq12wsx");
			createDatabaseAndTable();
		} 
		catch (SQLException e) {
			LoggerJava.log(e.getMessage());
		}
	}
}
