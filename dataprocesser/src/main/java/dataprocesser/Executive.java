package dataprocesser;
import java.io.File;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executive { 

	public static void main(String args[]){
		System.out.println("Data Processing Started!");
		//input folder which contains all the zip files
		String inputPath="/Users/arpitshah/Documents/C++ Project/i-Tree Europe/input";
		String outputPath="/Users/arpitshah/Documents/C++ Project/i-Tree Europe/output/";
		File zipfiles=new File(inputPath);
		for(File zipfile:zipfiles.listFiles()){
			//reading file
			String countryName=zipfile.getName().split("_")[0];
			String unzippedExcelFilesPath=Unzip.unzip(zipfile.getAbsolutePath());
			File countryExcelFiles=new File(unzippedExcelFilesPath);
			HashSet<String> stationNames=new HashSet<>();
			DataReader dr=new DataReader();



			//writing to database
			{
				int numberOfThreads=Math.min(countryExcelFiles.listFiles().length, 5);
				ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
				for(File countryExcelFile:countryExcelFiles.listFiles()){
					if(countryExcelFile.getName().contains("aggregated"))
						continue;
					try{
						executor.execute(() -> {
							dr.writeFileToDatabase(Integer.valueOf(countryExcelFile.getName().split("_")[1]),countryExcelFile,stationNames);;
						});
					}catch (Exception e) {
						LoggerJava.log(e.getMessage());
					}
				}

				executor.shutdown();

				while(!executor.isTerminated());
			}



			//indexing on stationName
			dr.doIndexing();



			//writing to excel files by reading from database
			{
				int numberOfThreads=Math.min(stationNames.size(), 5);
				ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

				//writing new excel file with processed data using stationNames
				String outputFolderPath=outputPath+countryName;
				new File(outputFolderPath).mkdirs();
				for(String stationName:stationNames){
					executor.execute(() -> {
						ResultSet rs=dr.getData(stationName);
						DataWriter.writeDataToExcel(outputFolderPath, stationName, rs);
					});
				}

				executor.shutdown();

				while(!executor.isTerminated());
			}
			
			dr.closeConnection();
			System.out.println("Data Processing of "+countryName+ " done.");
		}
		System.out.println("Data Processing Ended!");
	}
}

//public static void writeFileToDatabase(String pollType, File file){
//		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//			Object[] lines = br.lines().toArray();
//			int length=lines.length;
//			for(int i=1;i<2;i++){
//				String[] values=lines[i].toString().split("\\t");
////				for(String val:values){
////					System.out.print(val+" ");
////				}
////				System.out.println();
//				org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//				String startDate=values[10];
//				System.out.println(startDate);
//				DateTime dt = formatter.parseDateTime(startDate);
//				System.out.println(dt.getDayOfMonth()+" "+dt.getMonthOfYear()+" "+dt.getYear());
//				System.out.println(dt.getHourOfDay()+" "+dt.getMinuteOfHour()+" "+dt.getSecondOfMinute());
//			}
////			for(Object line:lines){
////				System.out.println(line.toString());
////			}
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//}	

//	HashSet<String> map=new HashSet<>();
//	DataReader dr=new DataReader();
//	File file=new File("Temp");
//	for(File f:file.listFiles()){
//		try{
//		String name=f.getName();
//		System.out.println(name);
//		String[] str=name.split("_");
//		String pollutantType=getPollutantName(Integer.valueOf(name.split("_")[1]));
//		System.out.println(pollutantType);
//		dr.writeFileToDatabase(pollutantType,f);
////		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
////			Object[] lines = br.lines().toArray();
////			for(Object line:lines){
////				System.out.println(line.toString());
////			}
////		} catch (Exception e) {
////			System.out.println(e);
////		}
//		}catch (Exception e) {
//			System.out.println(e);
//		}
//		
//	}
//try{  
//	String url="jdbc:mysql://localhost:3306/";
//	{
//Connection con=DriverManager.getConnection(  
//url,"root","Zaq12wsx");  
////here sonoo is database name, root is username and password  
//Statement stmt1=con.createStatement();  
//int rs1=stmt1.executeUpdate("create database emp123");
//con.close();
//	}
//Connection con=DriverManager.getConnection(  
//url+"emp123","root","Zaq12wsx");  
//{
//Statement stmt = con.createStatement();
//
//String sql = "CREATE TABLE REGISTRATION " +
//             "(id INTEGER not NULL, " +
//             " first VARCHAR(255), " + 
//             " last VARCHAR(255), " + 
//             " age INTEGER, " + 
//             " PRIMARY KEY ( id ))"; 
//
//stmt.executeUpdate(sql);
//}
//Statement stmt=con.createStatement();
//ResultSet rs=stmt.executeQuery("select * from REGISTRATION");  
//while(rs.next())  
//System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
//con.close();  
//}catch(Exception e){ System.out.println(e);}  
//} 
//
//import java.io.File;
//
//public class Executive {
//	public static void main(String[] args){
//		//Unzip zip=new Unzip();
//		LoggerJava.configLogger();
//		String source = "/Users/arpitmshah/Documents/Temp/arpi.zip";
//		File unzippedFolder=new File(Unzip.unzip(source));
//		for(final File file: unzippedFolder.listFiles()){
//			if(!file.getName().contains("aggregated"))
//			System.out.println(file.getAbsolutePath());
//		}
//	}
//}