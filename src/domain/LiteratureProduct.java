package domain;

import java.util.LinkedList;

public class LiteratureProduct {
	private LinkedList<LiteratureProduct> parents = new LinkedList<LiteratureProduct>();
	private LinkedList<LiteratureProduct> children = new LinkedList<LiteratureProduct>();
	
	private String litProdName;
	private String fileLocation ="";
	private String productTitle;
	private String productRef;
	private String productYear;
	
	public LiteratureProduct(String name){
		litProdName=new String("Product:"+this.hashCode());
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
	
	public void setName(String name){
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
