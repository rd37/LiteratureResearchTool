package domainviewer;

//import databaselogger.DBLogger;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import databaselogger.DBLogger;
import domain.Controller;
import domain.SystemChanged;
import domain.System;

public class ControllerProxy extends WindowAdapter implements SystemChanged{
	private static ControllerProxy proxy = new ControllerProxy();
	private int state=0;
	
	private ControllerProxy(){
		Controller.getInstance().addStateListener(this);
	}
	
	public static ControllerProxy getInstance(){return proxy;};
	
	public void saveProduct(String title,String year,String ref,String prodLoc,String idname){
		Controller.getInstance().saveProduct(title, year, ref,prodLoc,idname);
	}
	
	public void saveGroup(String text){
		if(state==1){
			Controller.getInstance().saveGroup(text);
		}
	}
	
	public void saveReview(String text, String revName,String litName){
		if(state==6 || state==3){
			DBLogger.getInstance().print("ControllerProxy", "saving review to "+revName+" lit name "+litName);
			Controller.getInstance().saveReview(text,revName,litName);
		}
	}
	
	public void remove(){
		Controller.getInstance().remove();
	}
	
	public void delete(int type){
		DBLogger.getInstance().print("ControllerProxy", "type "+type);
		Controller.getInstance().delete(type);
	}
	
	public void add(int type){
		Controller.getInstance().add(type);
	}
	
	public void select(int type,String id){
		if(type==System.Group){
			if(state==1 || state==0 || state==3 || state==2){
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}else
			if(state==5){
				Controller.getInstance().deselect();
			}else
			if(state==7 ||state==6 || state==8){
				Controller.getInstance().deselect();
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}
		}else
		if(type==System.LitRev){
			if(state==1 || state==0){
				Controller.getInstance().select(type, id);
			}else
			if(state==3 || state==2){
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}else
			if(state==7){
				Controller.getInstance().deselect();
			}else
			if(state==6 || state==5 || state==8){
				Controller.getInstance().deselect();
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}
		}else
		if(type==System.LitProd){
			if(state==8 || state==6 || state==7){
				Controller.getInstance().deselect();
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}else
			if(state==1 ){
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}else
			if(state==0 || state==1 || state==2 || state==3){
				Controller.getInstance().select(type, id);
			}
		}else
		if(type==System.Rev){
			if(state==3){
				Controller.getInstance().select(type, id);
			}else
			if(state==6){
				Controller.getInstance().deselect();
				Controller.getInstance().select(type, id);
			}
		}
	}
	
	public void create(int type,String arg){
		if(type==System.Group){
			if(state==5 || state==8 || state==7 || state==6){
				Controller.getInstance().deselect();
				Controller.getInstance().deselect();
				Controller.getInstance().create(type, arg);
			}else if(state==1 || state==2 || state == 3 || state==0){
				Controller.getInstance().deselect();
				Controller.getInstance().create(type, arg);
			}
		}else
			if(type==System.LitRev){
				if(state==8){
					Controller.getInstance().deselect();
					Controller.getInstance().create(type, arg);
					//this.create(System.Rev, "review");
				}else{
					if(state==2){
						Controller.getInstance().create(type, arg);
						//this.create(System.Rev, "review");
					}
				}
			}else
				if(type==System.LitProd){
					if(state==5 || state==7 || state==6){
						Controller.getInstance().deselect();
						Controller.getInstance().deselect();
						Controller.getInstance().create(type, arg);
					}else if(state==1 || state == 3 || state==0 || state==8){
						Controller.getInstance().deselect();
						Controller.getInstance().create(type, arg);
					}else if(state==2){
						Controller.getInstance().create(type, arg);
					}
				}else
					if(type==System.Rev){
						if(state==3){
							Controller.getInstance().create(type, arg);
							Controller.getInstance().add(System.LitRev);
						}else if(state==7 || state==6){
							Controller.getInstance().deselect();
							Controller.getInstance().create(type, arg);
							Controller.getInstance().add(System.LitRev);
						}
					}
	}
	
	public void setSelected(){
		Controller.getInstance().setSelected();
	}
	
	public void selectChanged(Object selected) {
		
	}

	public void stateChanged(int state) {
		this.state=state;
	}
	
	public void windowClosing(WindowEvent evt) {
	    Controller.getInstance().shutdown();
	    java.lang.System.exit(0);
	  }

	public void showTable(int type,String name){
		Controller.getInstance().showTable(type,name);
	}
	
	public void linkProducts(){
		Controller.getInstance().linkProducts();
	}
	
	public void unlinkProducts(){
		Controller.getInstance().unlinkProducts();
	}
}
