package API.Interfaces;

import java.sql.ResultSet;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IDatabaseManager {
    boolean connect(String db);

    ResultSet get(String query);

    boolean update(String query);

    boolean disconnect();

    boolean loadDictionary(String name);

    void checkDictionary();
}
