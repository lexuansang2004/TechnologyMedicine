package entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChiTietDoiTraId implements Serializable {
    private String doiTra;
    private String thuoc;
}