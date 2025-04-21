package iuh.fit.dao;

import iuh.fit.entity.DanhMuc;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DanhMucDAO extends GenericDAO<DanhMuc> {

    public List<DanhMuc> findAll() {
        String query = "MATCH (dm:DanhMuc) RETURN dm";
        return executeQuery(query, Map.of());
    }

    public Optional<DanhMuc> findById(String id) {
        String query = "MATCH (dm:DanhMuc {idDM: $idDM}) RETURN dm";
        List<DanhMuc> results = executeQuery(query, Map.of("idDM", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<DanhMuc> findByName(String name) {
        String query = "MATCH (dm:DanhMuc {ten: $tenDM}) RETURN dm";
        List<DanhMuc> results = executeQuery(query, Map.of("tenDM", name));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean save(DanhMuc danhMuc) {
        String query = "CREATE (dm:DanhMuc {idDM: $idDM, tenDM: $tenDM})";
        Map<String, Object> params = new HashMap<>();
        params.put("idDM", danhMuc.getIdDM());
        params.put("tenDM", danhMuc.getTen());
        return executeUpdate(query, params);
    }

    public boolean update(DanhMuc danhMuc) {
        String query = "MATCH (dm:DanhMuc {idDM: $idDM}) SET dm.tenDM = $tenDM";
        Map<String, Object> params = new HashMap<>();
        params.put("idDM", danhMuc.getIdDM());
        params.put("tenDM", danhMuc.getTen());
        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (dm:DanhMuc {idDM: $idDM}) DETACH DELETE dm";
        return executeUpdate(query, Map.of("idDM", id));
    }

    @Override
    protected DanhMuc mapRecordToEntity(Record record) {
        try {
            Value dmNode = record.get("dm");
            DanhMuc dm = new DanhMuc();
            dm.setIdDM(dmNode.get("idDM").asString());
            dm.setTen(dmNode.get("ten").asString());
            return dm;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to DanhMuc: " + e.getMessage());
            return null;
        }
    }
}
