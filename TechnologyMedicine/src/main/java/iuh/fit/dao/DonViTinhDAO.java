package iuh.fit.dao;

import iuh.fit.entity.DonViTinh;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DonViTinhDAO extends GenericDAO<DonViTinh> {

    public List<DonViTinh> findAll() {
        String query = "MATCH (dvt:DonViTinh) RETURN dvt";
        return executeQuery(query, Map.of());
    }

    public Optional<DonViTinh> findById(String id) {
        String query = "MATCH (dvt:DonViTinh {idDVT: $idDVT}) RETURN dvt";
        List<DonViTinh> results = executeQuery(query, Map.of("idDVT", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<DonViTinh> findByName(String name) {
        String query = "MATCH (dvt:DonViTinh {ten: $tenDVT}) RETURN dvt";
        List<DonViTinh> results = executeQuery(query, Map.of("tenDVT", name));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean save(DonViTinh dvt) {
        String query = "CREATE (dvt:DonViTinh {idDVT: $idDVT, tenDVT: $tenDVT})";
        Map<String, Object> params = new HashMap<>();
        params.put("idDVT", dvt.getIdDVT());
        params.put("tenDVT", dvt.getTen());
        return executeUpdate(query, params);
    }

    public boolean update(DonViTinh dvt) {
        String query = "MATCH (dvt:DonViTinh {idDVT: $idDVT}) SET dvt.tenDVT = $tenDVT";
        Map<String, Object> params = new HashMap<>();
        params.put("idDVT", dvt.getIdDVT());
        params.put("tenDVT", dvt.getTen());
        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (dvt:DonViTinh {idDVT: $idDVT}) DETACH DELETE dvt";
        return executeUpdate(query, Map.of("idDVT", id));
    }

    @Override
    protected DonViTinh mapRecordToEntity(Record record) {
        try {
            Value dvtNode = record.get("dvt");
            DonViTinh dvt = new DonViTinh();
            dvt.setIdDVT(dvtNode.get("idDVT").asString());
            dvt.setTen(dvtNode.get("ten").asString());
            return dvt;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to DonViTinh: " + e.getMessage());
            return null;
        }
    }
}
