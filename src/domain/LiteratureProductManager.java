package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureProductManager {
	private static LiteratureProductManager manager = new LiteratureProductManager();
	private DefaultListModel model = new DefaultListModel();
	
	private LiteratureProductManager(){};
	
	public static LiteratureProductManager getInstance(){return manager;}
	
	public DefaultListModel getListModel(){
		return model;
	}
	
	public boolean checkID(String id,LiteratureProduct exempt){
		for(int i=0;i<model.getSize();i++){
			LiteratureProduct prod = (LiteratureProduct)model.get(i);
			if(exempt!=prod)
				if(prod.toString().equals(id))
					return false;
		}
		return true;
	}
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setProduct(LinkedList<LiteratureProduct> list){
		for(int i=0;i<list.size();i++){
			model.addElement(list.get(i));
		}
	}
	
	public void addProduct(LiteratureProduct product){
		model.addElement(product);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.PRODUCTS, product);
	}
	
	public void removeProduct(LiteratureProduct product){
		model.removeElement(product);
		DerbyDBPersistance.getInstance().removeProduct(product.getName());
	}
	
	public LiteratureProduct findLiteratureProduct(String id){
		for(int i=0;i<model.getSize();i++){
			LiteratureProduct product = (LiteratureProduct)model.get(i);
			if(product.toString().equals(id))
				return product;
		}
		DBLogger.getInstance().print("LiteratureProductManager", "Unable to find id "+id);
		return null;
	}
}
