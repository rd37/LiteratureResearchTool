package domainviewer;

import javax.swing.DefaultListModel;

public interface DatabaseChanged {
	public void dbupdate(int type,DefaultListModel model);
}
