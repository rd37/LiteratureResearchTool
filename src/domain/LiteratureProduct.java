package domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureProduct {
	private LinkedList<LiteratureProduct> parents = new LinkedList<LiteratureProduct>();
	private LinkedList<LiteratureProduct> children = new LinkedList<LiteratureProduct>();
	
	private String litProdName;
	private String fileLocation ="";
	private String productTitle;
	private String productRef;
	private String productYear;
	private String productRefFileName=null;
	
	
	public LiteratureProduct(String name){
		litProdName=new String("Product:"+this.hashCode());
	}
	
	public void persist(){
		/*
		 * Create a new file if file does not exist for the reference string.
		 * save file
		 * 
		 * update database with info.
		 */
		if(productRefFileName==null){
			productRefFileName=productTitle+productTitle.hashCode();
		}
		File openFile = new File(productRefFileName);
		
		try {
			openFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {
			FileWriter writer = new FileWriter(openFile);
			writer.write(productRef);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		DBLogger.getInstance().print("LitProd", "wrote bib text to file "+openFile.getAbsolutePath());
		fileLocation=openFile.getAbsolutePath();
		DerbyDBPersistance.getInstance().updateLiteratureProduct(this.getName(),fileLocation,productTitle,productYear);
	}
	
	public void addParent(LiteratureProduct product){
		parents.add(product);
	}
	
	public void addChild(LiteratureProduct product){
		children.add(product);
	}
	
	public void removeParent(LiteratureProduct product){
		parents.remove(product);
	}
	
	public void removeChild(LiteratureProduct product){
		children.remove(product);
	}
	
	public void setName(String name,boolean dbupdate) throws Exception{
		DBLogger.getInstance().print("LitProduct", "SN:"+name+" CMP:"+litProdName);
		if(!name.equals(litProdName)&&dbupdate)
			DerbyDBPersistance.getInstance().replaceID(DerbyDBPersistance.PRODUCTS,name,this.getName());
		litProdName=name;
	}
	
	public String getName(){
		return litProdName;
	}
	
	public String toString(){
		return litProdName;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductRef(String productRef) {
		this.productRef = productRef;
	}

	public String getProductRef() {
		return productRef;
	}

	public void setProductYear(String productYear) {
		this.productYear = productYear;
	}

	public String getProductYear() {
		return productYear;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getFileLocation() {
		return fileLocation;
	}
}
