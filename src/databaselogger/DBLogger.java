package databaselogger;

public class DBLogger {
	private static DBLogger logger = new DBLogger();
	
	private DBLogger(){}
	
	public static DBLogger getInstance(){return logger;};
	
	public void print(String name, String msg){
		System.out.println(name+":"+msg);
	}
}
