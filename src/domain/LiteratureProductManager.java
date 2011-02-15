package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;
import domainviewer.DatabaseChanged;

public class LiteratureProductManager {
	private static LiteratureProductManager manager = new LiteratureProductManager();
	private DefaultListModel model = new DefaultListModel();
	
	private LiteratureProductManager(){};
	
	public static LiteratureProductManager getInstance(){return manager;}
	
	public DefaultListModel getListModel(){
		return model;
	}
	
	private LinkedList<DatabaseChanged> dbListeners = new LinkedList<DatabaseChanged>();
    
    public void registerDBChange(DatabaseChanged listener){
    	dbListeners.add(listener);
    }
    
    public void removeDBChange(DatabaseChanged listener){
    	dbListeners.remove(listener);
    }
    
    private void updateListeners(int type,int op,Object obj){
    	for(int i=0;i<dbListeners.size();i++){
    		dbListeners.get(i).dbupdates(type,op,obj);
    	}
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
			this.updateListeners(System.LitProd,0,list.get(i));
		}
		
	}
	
	public void addProduct(LiteratureProduct product){
		model.addElement(product);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.PRODUCTS, product);
		this.updateListeners(System.LitProd,0,product);
	}
	
	public void removeProduct(LiteratureProduct product){
		model.removeElement(product);
		DerbyDBPersistance.getInstance().removeProduct(product.getName());
		this.updateListeners(System.LitProd,1,product);
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
