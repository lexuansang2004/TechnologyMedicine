package iuh.fit.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

import java.util.logging.Logger;

public class Neo4jConfig implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(Neo4jConfig.class.getName());

    private static final String URI = "bolt://localhost:7687";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "14042004"; // Thay đổi mật khẩu thực tế
    private static final String DATABASE_NAME = "technologymedicine"; // Tên database cụ thể

    private final Driver driver;

    private static Neo4jConfig instance;

    private Neo4jConfig() {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USERNAME, PASSWORD));
        LOGGER.info("Neo4j driver initialized");
    }

    public static synchronized Neo4jConfig getInstance() {
        if (instance == null) {
            instance = new Neo4jConfig();
        }
        return instance;
    }

    public Driver getDriver() {
        return driver;
    }

    public Session getSession() {
        // Chỉ định rõ database "technologymedicine" khi tạo session
        return driver.session(SessionConfig.forDatabase(DATABASE_NAME));
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.close();
            LOGGER.info("Neo4j driver closed");
        }
    }
}