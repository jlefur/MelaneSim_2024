package presentation.display;

import javax.swing.JPanel;
import repast.simphony.userpanel.ui.UserPanelCreator;

public class C_UserPanelMasterTabsCreator implements UserPanelCreator {
  @Override
  public JPanel createPanel() {
    return new C_UserPanelMasterTabs();
  }
}
