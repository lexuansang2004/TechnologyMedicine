package iuh.fit.dto;

import iuh.fit.entity.KhachHang;

import java.util.HashMap;
import java.util.Map;

public class RequestDTO {
    private String action;
    private Map<String, Object> data;

    public RequestDTO() {
        this.data = new HashMap<>();
    }

    public RequestDTO(String action, Map<String, Object> data) {
        this.action = action;
        this.data = data != null ? data : new HashMap<>();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getData() {
        return data;
    }


    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }
}