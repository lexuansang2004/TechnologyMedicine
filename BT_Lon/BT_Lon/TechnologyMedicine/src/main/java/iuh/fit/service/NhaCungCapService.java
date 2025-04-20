package iuh.fit.service;

import iuh.fit.dao.NhaCungCapDAO;
import iuh.fit.entity.NhaCungCap;

import java.util.List;
import java.util.Optional;

public class NhaCungCapService {

    private final NhaCungCapDAO nhaCungCapDAO;

    public NhaCungCapService() {
        this.nhaCungCapDAO = new NhaCungCapDAO();
    }

    public List<NhaCungCap> findAll() {
        return nhaCungCapDAO.findAll();
    }

    public Optional<NhaCungCap> findById(String id) {
        return nhaCungCapDAO.findById(id);
    }

    public List<NhaCungCap> findByName(String name) {
        return nhaCungCapDAO.findByName(name);
    }

    public boolean save(NhaCungCap nhaCungCap) {
        return nhaCungCapDAO.save(nhaCungCap);
    }

    public boolean update(NhaCungCap nhaCungCap) {
        return nhaCungCapDAO.update(nhaCungCap);
    }

    public boolean delete(String id) {
        return nhaCungCapDAO.delete(id);
    }

    public String generateNewId() {
        List<NhaCungCap> nhaCungCapList = findAll();
        int maxId = 0;

        for (NhaCungCap nhaCungCap : nhaCungCapList) {
            String idStr = nhaCungCap.getIdNCC().substring(3);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("NCC%03d", maxId + 1);
    }
}