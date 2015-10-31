package pl.rbolanowski.tw4a;

import pl.rbolanowski.tw4a.backend.Database;

public class DatabaseProvider {
  
    private Database mDatabase;
    private static DatabaseProvider mInstance;
    
    private DatabaseProvider() {}
    
    public static synchronized DatabaseProvider getInstance() {
        if (mInstance == null) {
            mInstance = new DatabaseProvider();
        }
        return mInstance;
    }
    
    public void setDatabase(Database database) {
        mInstance.mDatabase = database;
    }
    
    public Database getDatabase() {
        return mInstance.mDatabase;
    }
}
