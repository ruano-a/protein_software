package Manager.ProteinMenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 * Created by paull on 24/03/2016.
 */
public class MenuBarManager {
    private static MenuBar bar;

    public MenuBarManager(){
        bar = new MenuBar();
    }

    public static void addMenu(Menu menu){
        bar.getMenus().add(menu);
    }

    public MenuBar getBar(){
        return bar;
    }
}
