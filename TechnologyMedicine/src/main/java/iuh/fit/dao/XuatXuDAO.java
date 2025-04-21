package iuh.fit.dao;

import iuh.fit.entity.XuatXu;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class XuatXuDAO extends GenericDAO<XuatXu> {

    public List<XuatXu> findAll() {
        String query = "MATCH (xx:XuatXu) RETURN xx";
        return executeQuery(query, Map.of());
    }

    public Optional<XuatXu> findById(String id) {
        String query = "MATCH (xx:XuatXu {idXX: $idXX}) RETURN xx";
        List<XuatXu> results = executeQuery(query, Map.of("idXX", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<XuatXu> findByName(String name) {
        String query = "MATCH (xx:XuatXu {ten: $tenXX}) RETURN xx";
        List<XuatXu> results = executeQuery(query, Map.of("tenXX", name));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean save(XuatXu xx) {
        String query = "CREATE (xx:XuatXu {idXX: $idXX, tenXX: $tenXX})";
        Map<String, Object> params = new HashMap<>();
        params.put("idXX", xx.getIdXX());
        params.put("tenXX", xx.getTen());
        return executeUpdate(query, params);
    }

    public boolean update(XuatXu xx) {
        String query = "MATCH (xx:XuatXu {idXX: $idXX}) SET xx.tenXX = $tenXX";
        Map<String, Object> params = new HashMap<>();
        params.put("idXX", xx.getIdXX());
        params.put("tenXX", xx.getTen());
        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (xx:XuatXu {idXX: $idXX}) DETACH DELETE xx";
        return executeUpdate(query, Map.of("idXX", id));
    }

    @Override
    protected XuatXu mapRecordToEntity(Record record) {
        try {
            Value xxNode = record.get("xx");
            XuatXu xx = new XuatXu();
            xx.setIdXX(xxNode.get("idXX").asString());
            xx.setTen(xxNode.get("ten").asString());
            return xx;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to XuatXu: " + e.getMessage());
            return null;
        }
    }
}
