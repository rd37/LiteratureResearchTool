package domain;

public class System {
	private static System system = new System();
	public static int Group=0;
	public static int LitRev=1;
	public static int LitProd=2;
	public static int Rev=3;
	public static int ProdLink=4;
	
	private System(){}
	
	public static System getInstance(){return system;}
	
	
}
