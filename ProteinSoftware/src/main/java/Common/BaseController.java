package Common;

import API.Interfaces.IBaseController;
import API.Interfaces.IBaseModel;
import javafx.scene.Node;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class BaseController implements IBaseController {
    protected Node      view;
    protected IBaseModel model;

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node v) {
        view = v;
    }

    @Override
    public IBaseModel getModel() {
        return model;
    }
}
