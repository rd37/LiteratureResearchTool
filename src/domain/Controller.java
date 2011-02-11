package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class Controller {
	private static Controller controller = new Controller();
	private LinkedList<SystemChanged> listeners = new LinkedList<SystemChanged>();

	private int state=0;
	private Object selectedObject=null;
	private LinkedList<Object> selectedObjectStack = new LinkedList<Object>();
	private Controller(){};
	
	private void updateListeners(){
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).stateChanged(state);
			listeners.get(i).selectChanged(selectedObject);
		}
	}
	
	public static Controller getInstance(){return controller;}
	
	public void create(int type,String name){
		//this.selObjType=type;
		if(type==System.Group){
			if(state==0){
				LiteratureGrouping grouping = new LiteratureGrouping(name);
				LiteratureGroupManager.getInstance().addGroup(grouping);
				this.selectedObject=grouping;
				state=1;
			}else{
				DBLogger.getInstance().print("Controller", "Must be in state 0 to create group cs:"+state);
			}
		}else
		if(type==System.LitProd){
			if(state==0){
				LiteratureProduct product = new LiteratureProduct(name);
				LiteratureProductManager.getInstance().addProduct(product);
				this.selectedObject=product;
				state=2;
			}else
			if(state==2){
				LiteratureProduct product = new LiteratureProduct(name);
				LiteratureProductManager.getInstance().addProduct(product);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=product;
				state=8;
			}else{
				DBLogger.getInstance().print("Controller", "Must be in state 0 to create literature product cs:"+state);
			}
		}else
		if(type==System.LitRev){
			if(state==2){
				LiteratureReview litrev = new LiteratureReview(name);
				LiteratureReviewManager.getInstance().addLiteratureReview(litrev);
				litrev.setLitProduct((LiteratureProduct)selectedObject);
				this.selectedObject=litrev;
				state=3;
				//this.create(domain.System.Rev, "review");
			}else{
				DBLogger.getInstance().print("Controller", "Must be in state 2 to create literature review cs:"+state);
			}
		}else
		if(type==System.Rev){
			if(state==3){
				Review review = new Review(name);
				LiteratureReviewManager.getInstance().addReview(review);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=review;
				state=6;
			}else{
				DBLogger.getInstance().print("Controller", "Must be in state 1 to create literature review cs:"+state);
			}
		}
		updateListeners();
	}
	
	public void deselect(){
		if(state==1){
			state=0;
			selectedObject=null;
		}else
		if(state==2){
			state=0;
			selectedObject=null;
		}else
		if(state==3){
			state=0;
			selectedObject=null;
		}else
		if(state==5){
			state=1;
			selectedObject=selectedObjectStack.removeLast();
		}else
		if(state==7){
			state=3;
			selectedObject=selectedObjectStack.removeLast();
		}else
		if(state==6){
			state=3;
			selectedObject=selectedObjectStack.removeLast();
		}else
		if(state==8){
			state=2;
			selectedObject=selectedObjectStack.removeLast();
		}
		updateListeners();
	}
	
	public void delete(int type){
		if(state==1){
			if(type==System.Group){
				DefaultListModel litrevs = ((LiteratureGrouping)selectedObject).getLitrevgrouping();
				litrevs.clear();
				LiteratureGroupManager.getInstance().removeGroup((LiteratureGrouping)selectedObject);
				state=0;selectedObject=null;
			}
		}else
		if(state==2){
			if(type==System.LitProd){
				LiteratureProductManager.getInstance().removeProduct((LiteratureProduct)selectedObject);
				state=0;selectedObject=null;
			}
		}else
		if(state==3){
			if(type==System.LitRev){
				DefaultListModel review = ((LiteratureReview)selectedObject).getListModel();
				for(;review.size()>0;){
					Review rev = (Review)review.get(0);
					((LiteratureReview)selectedObject).removeReview(rev);
					LiteratureReviewManager.getInstance().removeReview(rev);
				}
				LiteratureReviewManager.getInstance().removeLiteratureReview((LiteratureReview)selectedObject);
				LiteratureGroupManager.getInstance().removeLiteratureReview((LiteratureReview)selectedObject);
				state=0;selectedObject=null;
			}
		}else
		if(state==6){
			if(type==System.Rev){
				DBLogger.getInstance().print("Controller", "delete type "+type);
				LiteratureReviewManager.getInstance().removeReview((Review)selectedObject);
				((LiteratureReview)selectedObjectStack.getLast()).removeReview((Review)selectedObject);
				state=3;selectedObject=((LiteratureReview)selectedObjectStack.removeLast());
			}
		}else
		if(state==8){
			LiteratureProductManager.getInstance().removeProduct((LiteratureProduct)selectedObject);
			state=2;selectedObject=selectedObjectStack.removeLast();
		}
		updateListeners();
	}
	
	public void select(int type,String id){
		//this.selObjType=type;
		if(type==System.Group){
			if(state==0){
				LiteratureGrouping litGroup = LiteratureGroupManager.getInstance().findLiteratureGrouping(id);
				this.selectedObject=litGroup;
				state=1;
			}
		}else
		if(type==System.LitProd){
			if(state==0){
				LiteratureProduct litprod = LiteratureProductManager.getInstance().findLiteratureProduct(id);
				this.selectedObject=litprod;
				state=2;
			}else
			if(state==3){
				LiteratureProduct litprod = LiteratureProductManager.getInstance().findLiteratureProduct(id);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=litprod;
				state=7;
			}else
			if(state==2){
				LiteratureProduct litprod = LiteratureProductManager.getInstance().findLiteratureProduct(id);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=litprod;
				state=8;
			}
		}else
		if(type==System.LitRev){
			if(state==0){
				LiteratureReview litReview = LiteratureReviewManager.getInstance().findLiteratureReview(id);
				this.selectedObject=litReview;
				state=3;
			}else
			if(state==1){
				LiteratureReview litReview = LiteratureReviewManager.getInstance().findLiteratureReview(id);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=litReview;
				state=5;
			}
		}else
		if(type==System.Rev){
			if(state==3){
				Review review = LiteratureReviewManager.getInstance().findReview(id);
				selectedObjectStack.add(selectedObject);
				this.selectedObject=review;
				state=6;
			}	
		}
		updateListeners();
	}
	
	public void add(int type){
		if(type==System.Group){
			if(state==5){
				LiteratureGrouping litGroup = (LiteratureGrouping)selectedObjectStack.getLast();
				LiteratureReview litReview = (LiteratureReview)selectedObject;
				litGroup.add(litReview);
				selectedObject=selectedObjectStack.removeLast();
				state=1;
			}
		}else
		if(type==System.LitRev){
			if(state==6){
				LiteratureReview litReview = (LiteratureReview)selectedObjectStack.getLast();
				Review review = (Review)selectedObject;
				litReview.addReview(review);
				selectedObject=selectedObjectStack.removeLast();
				state=3;
			}
		}else
		if(type==System.LitProd){
			if(state==8){
				LiteratureProduct parent = (LiteratureProduct)selectedObjectStack.getLast();
				LiteratureProduct child = (LiteratureProduct)selectedObject;
				parent.addChild(child);
				child.addParent(parent);
				selectedObject=selectedObjectStack.removeLast();
				state=2;
			}
		}
		updateListeners();
	}
	
	public void remove(){
		if(state==5){
			LiteratureGrouping litGroup = (LiteratureGrouping)selectedObjectStack.getLast();
			LiteratureReview litReview = (LiteratureReview)selectedObject;
			litGroup.removeLiteratureReview(litReview);
			selectedObject=selectedObjectStack.removeLast();
			state=1;
		}else
		if(state==6){
			LiteratureReview litReview = (LiteratureReview)selectedObjectStack.getLast();
			Review review = (Review)selectedObject;
			litReview.removeReview(review);
			selectedObject=selectedObjectStack.removeLast();
			state=3;
		}else
		if(state==8){
			LiteratureProduct parent = (LiteratureProduct)selectedObjectStack.getLast();
			LiteratureProduct child = (LiteratureProduct)selectedObject;
			parent.removeChild(child);
			child.removeParent(parent);
			selectedObject=selectedObjectStack.removeLast();
			state=2;
		}
		updateListeners();
	}
	
	public void setSelected(){
		if(state==7){
			LiteratureReview litReview = (LiteratureReview)selectedObjectStack.getLast();
			LiteratureProduct product = (LiteratureProduct)selectedObject;
			litReview.setLitProduct(product);
			state=3;
			selectedObject=selectedObjectStack.removeLast();
		}
		updateListeners();
	}
	
	public void save(String text,String id){
		if(state==6){
			((Review)selectedObject).setText(text);
		}
		updateListeners();
	}
	
	public void saveProduct(String title,String year,String ref,String prodLoc,String idname){
		if(state==2){
			
			boolean nameOK=LiteratureProductManager.getInstance().checkID(idname,((LiteratureProduct)selectedObject));
			DBLogger.getInstance().print("Controller", "Save Product "+nameOK);
			if(nameOK){
				//((LiteratureProduct)selectedObject).setName(idname);
				((LiteratureProduct)selectedObject).setProductTitle(title);
				((LiteratureProduct)selectedObject).setProductYear(year);
				((LiteratureProduct)selectedObject).setProductRef(ref);//should save to file
				((LiteratureProduct)selectedObject).setFileLocation(prodLoc);
				((LiteratureProduct)selectedObject).persist();
				this.updateListeners();
			}
		}
	}
	
	public void intialize(){
		DerbyDBPersistance.getInstance().intialize();
	}
	
	public void shutdown(){
		DerbyDBPersistance.getInstance().shutdown();
	}
	/*
	 * Listeners
	 */
	public void addStateListener(SystemChanged listener){
		listeners.add(listener);
	}
	
	public void removeStateListener(SystemChanged listener){
		listeners.remove(listener);
	}
	
	public void showTable(int type,String name){
		DerbyDBPersistance.getInstance().showTable(type);
	}
}
