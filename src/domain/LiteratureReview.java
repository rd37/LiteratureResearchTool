package domain;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureReview {
	private String litRevName;
	//private LinkedList<Review> review = new LinkedList<Review>();
	private DefaultListModel review = new DefaultListModel();
	private LiteratureProduct litProduct;
	
	public LiteratureReview(String name){
		litRevName=new String("litrev:"+this.hashCode());;
	}
	
	public DefaultListModel getListModel(){
		return review;
	}
	
	public void setName(String name,boolean updatedb) throws Exception{
		if(updatedb && !name.equals(this.getName()))
			DerbyDBPersistance.getInstance().replaceID(DerbyDBPersistance.LITREVS, name, this.getName());
		litRevName=name;
	}
	
	public String getName(){
		return litRevName;
	}
	
	public String toString(){
		return litRevName;
	}
	
	public void addReview(Review rev,boolean insertDB){
		review.addElement(rev);
		if(insertDB)
			DerbyDBPersistance.getInstance().updateLitRevAddReview(this.getName(), rev.getName(), litProduct.getName());
	}
	
	public void removeReview(Review rev){
		review.removeElement(rev);
		DerbyDBPersistance.getInstance().removeReview(this.getName(),rev.getName());
	}
	
	public Review findReview(String id){
		for(int i=0;i<review.size();i++){
			Review rev = (Review)review.get(i);
			if(rev.toString().equals(id)){
				return rev;
			}
		}
		DBLogger.getInstance().print("LiteratureReview", "Unalbe to find Review using id "+id);
		return null;
	}
	
	public void removeReview(String id){
		for(int i=0;i<review.size();i++){
			Review rev = (Review)review.get(i);
			if(rev.toString().equals(id)){
				review.removeElement(rev);
				return;
			}
		}
	}
	
	public void setLitProduct(LiteratureProduct litProduct,boolean enterDB) {
		this.litProduct = litProduct;
		String productID=litProduct.getName();
		String litRevID=this.getName();
		DBLogger.getInstance().print("LitRev", "Updated product");
		if(enterDB)
			DerbyDBPersistance.getInstance().updateLitRevSetProductID(litRevID, productID);
	}

	public LiteratureProduct getLitProduct() {
		return litProduct;
	}
}
