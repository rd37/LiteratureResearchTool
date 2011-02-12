package domainviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ActionCreationFactory {
	private static ActionCreationFactory factory = new ActionCreationFactory();
	
	private ActionCreationFactory(){}
	
	public static ActionCreationFactory getInstance(){return factory;}
	public void createSaveProduct(JButton button,final JTextField title,final JTextField year,final JTextArea ref,final JTextField pLoc,final JTextField name){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ControllerProxy.getInstance().saveProduct(title.getText(), year.getText(),ref.getText(),pLoc.getText(),name.getText());
			}
		});
	}
	public void createSaveReview(JButton button,final JTextArea text,final JList reviewList){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {	
				ControllerProxy.getInstance().saveReview(text.getText(),"");
			}
		});
	}
	
	public void createJListListeners(JList list,final int type){
		list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				JList list = (JList)arg0.getSource();
				if(list==null || list.getSelectedValue() == null)
					return;
				String id = list.getSelectedValue().toString();
				ControllerProxy.getInstance().select(type, id);
				list.clearSelection();
			}
		});
	}
	
	public void createAddListener(JButton button,final int type){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().add(type);
			}
		});
	}
	
	public void createOpenListener(JMenuItem item,final int type, final String arg){
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().create(type, arg);
			}
		});
	}
	
	public void createRemoveListener(JButton button,final int type){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().remove();
			}
		});
	}
	
	public void createDeleteAction(JButton button,final int type){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().delete(type);
			}
		});
	}
	
	public void createSetSelectedListener(JButton button,final int type){
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().setSelected();
			}
		});
	}
	
	public void createShowTableListener(JMenuItem item,final int type, final String arg){
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ControllerProxy.getInstance().showTable(type, arg);
			}
		});
	}
	
	
}
