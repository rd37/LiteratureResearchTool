package domainviewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import databaselogger.DBLogger;
import domain.Controller;
import domain.System;
import domain.SystemChanged;

public class LiteratureTool implements SystemChanged{
	private static JFrame frame = new JFrame("Literature Management Tool");
	private static JFrame graphicsFrame = new JFrame("Interactive Graphical Tool");
	
	public void initialize(){
		//setup drop menu
		//create jmenu items
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("New");
		JMenuItem item1 = new JMenuItem("Group");
		ActionCreationFactory.getInstance().createOpenListener(item1, System.Group, "groupname");
		JMenuItem item2 = new JMenuItem("Literature Product");
		ActionCreationFactory.getInstance().createOpenListener(item2, System.LitProd, "litproductname");
		JMenuItem item3 = new JMenuItem("Literature Review");
		ActionCreationFactory.getInstance().createOpenListener(item3, System.LitRev, "litname");
		JMenuItem item4 = new JMenuItem("Review");
		ActionCreationFactory.getInstance().createOpenListener(item4, System.Rev, "reviewname");
		menu.add(item1);menu.add(item2);menu.add(item3);menu.add(item4);
		
		JMenu menu2 = new JMenu("Debug");
		JMenuItem item21 = new JMenuItem("Show Group Table");
		ActionCreationFactory.getInstance().createShowTableListener(item21, System.Group, "groupname");
		JMenuItem item22 = new JMenuItem("Show LitRev Table");
		ActionCreationFactory.getInstance().createShowTableListener(item22, System.LitRev, "groupname");
		JMenuItem item23 = new JMenuItem("Show Product Table");
		ActionCreationFactory.getInstance().createShowTableListener(item23, System.LitProd, "groupname");
		JMenuItem item24 = new JMenuItem("Show Review Table");
		ActionCreationFactory.getInstance().createShowTableListener(item24, System.Rev, "groupname");
		JMenuItem item25 = new JMenuItem("Show ProdLink Table");
		ActionCreationFactory.getInstance().createShowTableListener(item25, System.ProdLink, "groupname");
		menu2.add(item21);menu2.add(item22);menu2.add(item23);menu2.add(item24);menu2.add(item25);
		
		JMenu menu3 = new JMenu("View");
		JMenuItem showItem = new JMenuItem("Graphical View");
		ActionCreationFactory.getInstance().createGraphicalAction(showItem,graphicsFrame);
		menu3.add(showItem);
		
		/*
		 * setup graphics frame panels controls and link to system
		 */
		DataBaseListenerGraphicsTranslator dbt = new DataBaseListenerGraphicsTranslator();
		dbt.intialize(graphicsFrame);
		
		menubar.add(menu);menubar.add(menu2);menubar.add(menu3);
		frame.setJMenuBar(menubar);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setVisible(true);
		frame.setResizable(true);
		frame.addWindowListener(ControllerProxy.getInstance());

		//setup mainpanel
		MainPanel mainPanel = new MainPanel();
		frame.getContentPane().add(mainPanel,BorderLayout.CENTER);
		frame.pack();
		Controller.getInstance().addStateListener(this);
		Controller.getInstance().intialize();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LiteratureTool tool = new LiteratureTool();
		tool.initialize();
	}
	
	@Override
	public void selectChanged(Object selected) {
		if(selected!=null)
			DBLogger.getInstance().print("LiteratureTool::", selected.toString());
	}
	@Override
	public void stateChanged(int state) {
		DBLogger.getInstance().print("LiteratureTool::", " new state:"+state);
	}

}
