package lk.ijse.dep13.connectionpool.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;

public class CP {
    private static final int DEFAULT_POOL_SIZE = 5;

    private final HashMap<UUID, Connection> MAIN_POOL = new HashMap<>();
    private final HashMap<UUID, Connection> CONSUMER_POOL = new HashMap<>();

    private int poolSize;

    public CP() {
        this.poolSize = getConfiguredPoolSize();
        try {
            initializePool();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public int getPoolSize() {
        return poolSize;
    }

    private int getConfiguredPoolSize() {
        ResourceBundle bundle = ResourceBundle.getBundle("application");
        try {
            return Integer.parseInt(bundle.getString("app.db.poolSize"));
        } catch (Exception e) {
            return DEFAULT_POOL_SIZE;
        }
    }

    private void initializePool() throws IOException, SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));

        String host = properties.getProperty("app.db.host");
        String port = properties.getProperty("app.db.port");
        String database = properties.getProperty("app.db.database");
        String user = properties.getProperty("app.db.user");
        String password = properties.getProperty("app.db.password");

        for (int i = 0; i < poolSize; i++) {
            Connection connection = DriverManager.getConnection("jdbc:mysql://%s:%s/%s"
                    .formatted(host, port, database), user, password);
            UUID id = UUID.randomUUID();
            MAIN_POOL.put(id, connection);
        }
    }
    public synchronized ConnectionWrapper getConnection() {
        while (MAIN_POOL.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        UUID key = MAIN_POOL.keySet().stream().findFirst().get();
        Connection connection = MAIN_POOL.get(key);
        MAIN_POOL.remove(key);
        CONSUMER_POOL.put(key, connection);
        return new ConnectionWrapper(key, connection);
    }

    public synchronized void releaseConnection(UUID id){
        if (!CONSUMER_POOL.containsKey(id)) throw new RuntimeException("Invalid Connection ID");
        Connection connection = CONSUMER_POOL.get(id);
        CONSUMER_POOL.remove(id);
        MAIN_POOL.put(id, connection);
        notify();
    }

    public synchronized void releaseAllConnections(){
        CONSUMER_POOL.forEach(MAIN_POOL::put);
        CONSUMER_POOL.clear();
        notifyAll();
    }
    public record ConnectionWrapper(UUID id, Connection connection) {
    }
}
