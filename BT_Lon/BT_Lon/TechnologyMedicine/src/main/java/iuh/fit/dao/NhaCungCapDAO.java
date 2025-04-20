package iuh.fit.dao;

import iuh.fit.entity.NhaCungCap;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NhaCungCapDAO extends GenericDAO<NhaCungCap> {

    public List<NhaCungCap> findAll() {
        String query = "MATCH (ncc:NhaCungCap) RETURN ncc";
        return executeQuery(query, Map.of());
    }

    public Optional<NhaCungCap> findById(String id) {
        String query = "MATCH (ncc:NhaCungCap {idNCC: $idNCC}) RETURN ncc";

        List<NhaCungCap> results = executeQuery(query, Map.of("idNCC", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<NhaCungCap> findByName(String name) {
        String query = "MATCH (ncc:NhaCungCap) " +
                "WHERE ncc.tenNCC CONTAINS $tenNCC " +
                "RETURN ncc";

        return executeQuery(query, Map.of("tenNCC", name));
    }

    public boolean save(NhaCungCap nhaCungCap) {
        String query = "CREATE (ncc:NhaCungCap {idNCC: $idNCC, tenNCC: $tenNCC, sdt: $sdt, diaChi: $diaChi})";

        Map<String, Object> params = new HashMap<>();
        params.put("idNCC", nhaCungCap.getIdNCC());
        params.put("tenNCC", nhaCungCap.getTenNCC());
        params.put("sdt", nhaCungCap.getSdt());
        params.put("diaChi", nhaCungCap.getDiaChi());

        return executeUpdate(query, params);
    }

    public boolean update(NhaCungCap nhaCungCap) {
        String query = "MATCH (ncc:NhaCungCap {idNCC: $idNCC}) " +
                "SET ncc.tenNCC = $tenNCC, ncc.sdt = $sdt, ncc.diaChi = $diaChi";

        Map<String, Object> params = new HashMap<>();
        params.put("idNCC", nhaCungCap.getIdNCC());
        params.put("tenNCC", nhaCungCap.getTenNCC());
        params.put("sdt", nhaCungCap.getSdt());
        params.put("diaChi", nhaCungCap.getDiaChi());

        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (ncc:NhaCungCap {idNCC: $idNCC}) " +
                "DETACH DELETE ncc";

        return executeUpdate(query, Map.of("idNCC", id));
    }

    @Override
    protected NhaCungCap mapRecordToEntity(Record record) {
        try {
            Value nccValue = record.get("ncc");

            NhaCungCap nhaCungCap = new NhaCungCap();
            nhaCungCap.setIdNCC(nccValue.get("idNCC").asString());
            nhaCungCap.setTenNCC(nccValue.get("tenNCC").asString());
            nhaCungCap.setSdt(nccValue.get("sdt").asString());
            nhaCungCap.setDiaChi(nccValue.get("diaChi").asString());

            return nhaCungCap;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to NhaCungCap: " + e.getMessage());
            return null;
        }
    }
}