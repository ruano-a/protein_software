package Manager.Database;

import API.Interfaces.IDatabaseManager;
import Extractor.XMLExtractor;
import Manager.Notification.NotificationManager;
import Protein.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//TODO Revoir la gestion de base de données quand le logiciel est lancé plusieur fois(pour le moment créer une db par instance).

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class DatabaseManager implements IDatabaseManager {
    private Connection      connection;
    private Statement       statement;
    private List<String>    dictionaryLoaded;
    private File            dbFile;

    public DatabaseManager(){
        dictionaryLoaded = new ArrayList<>();
        this.dbFile = this.getDBFile();
        System.out.println(this.dbFile.getName());
        this.connect(dbFile.getName());
        this.update("CREATE TABLE IF NOT EXISTS keyword (word char(255) DEFAULT NULL, lang char(255) DEFAULT NULL, type char(255) DEFAULT NULL, repet int DEFAULT 0);");
        this.update("CREATE TABLE IF NOT EXISTS lang_utils (code char(255) DEFAULT NULL, lang char(255) DEFAULT NULL, sentence char(255) DEFAULT NULL);");
    }

    private File getDBFile() {
        String tmp = "Protein";
        int i = 0;
        File f = new File(Core.getBaseUrl() + tmp + i + ".db");
        while (f.exists()){
            f = new File(Core.getBaseUrl() + tmp + ++i + ".db");
        }
        return f;
    }

    @Override
    public boolean connect(String db){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println(Core.getBaseUrl());
            connection = DriverManager.getConnection("jdbc:sqlite:"+ Core.getBaseUrl() + db);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            System.out.println("Opened database successfully");
            return true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            NotificationManager.pop("SQLite connect", e.getMessage(), "error");
        }
        return false;
    }

    @Override
    public ResultSet get(String query){
        try {
            ResultSet rs = statement.executeQuery(query);
            return rs;
        } catch ( Exception e ) {
            System.err.println( "[get]" +  e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            NotificationManager.pop("SQLite get", e.getMessage(), "error");
        }
        return null;
    }

    @Override
    public boolean update(String query){
        try {
            statement.executeUpdate(query);
            connection.commit();
            return true;
        } catch (Exception e){
            System.err.println( "[update]" + e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            NotificationManager.pop("SQLite update", e.getMessage(), "error");
        }
        return false;
    }


    @Override
    public boolean disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (this.dbFile.delete()){
                System.out.println("Disconnected from database succesfully.");
            }
            return true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            NotificationManager.pop("SQLite disconnect", e.getMessage(), "error");
        }

        return false;
    }

    @Override
    public boolean loadDictionary(String name){
        boolean success = true;
        if (!dictionaryLoaded.contains(name)){
            String query = XMLExtractor.getQueryFromXML(Core.getBaseUrl() + "/classes/Dictionary/" + name + ".xml");
            if (query != null){
                success = update(query);
                if (success){
                    dictionaryLoaded.add(name);
                }
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public void checkDictionary(){
        try {
            ResultSet rs = get("SELECT DISTINCT lang FROM keyword;");
            while (rs.next()){
                String lang = rs.getString("lang");
                dictionaryLoaded.add(lang);
            }
            rs.close();
            rs = get("SELECT COUNT(sentence) AS count FROM lang_utils;");
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0){
                    dictionaryLoaded.add("traduction");
                }
            }
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }
}
