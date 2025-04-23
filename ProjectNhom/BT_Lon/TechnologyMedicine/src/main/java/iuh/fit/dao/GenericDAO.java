package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.NhanVien;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GenericDAO<T> {

    protected static final Logger LOGGER = Logger.getLogger(GenericDAO.class.getName());
    protected final Driver driver;

    public GenericDAO() {
        this.driver = Neo4jConfig.getInstance().getDriver();
    }

    /**
     * Thực thi một truy vấn Cypher và trả về kết quả dưới dạng danh sách đối tượng
     *
     * @param query Truy vấn Cypher
     * @param params Tham số cho truy vấn
     * @return Danh sách đối tượng
     */
    protected List<T> executeQuery(String query, Map<String, Object> params) {
        List<T> results = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            Result result = session.run(query, params);
            while (result.hasNext()) {
                Record record = result.next();
                T entity = mapRecordToEntity(record);
                if (entity != null) {
                    results.add(entity);
                }
            }
        } catch (Neo4jException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: " + query, e);
            throw e;
        }

        return results;
    }

    /**
     * Thực thi một truy vấn Cypher trong một transaction
     *
     * @param query Truy vấn Cypher
     * @param params Tham số cho truy vấn
     * @return true nếu thành công, false nếu thất bại
     */
    protected boolean executeUpdate(String query, Map<String, Object> params) {
        try (Session session = Neo4jConfig.getInstance().getSession();
             Transaction tx = session.beginTransaction()) {
            tx.run(query, params);
            tx.commit();
            return true;
        } catch (Neo4jException e) {
            LOGGER.log(Level.SEVERE, "Error executing update: " + query, e);
            return false;
        }
    }

    /**
     * Chuyển đổi một Record từ Neo4j thành đối tượng entity
     *
     * @param record Record từ Neo4j
     * @return Đối tượng entity
     */
    protected abstract T mapRecordToEntity(Record record);
}