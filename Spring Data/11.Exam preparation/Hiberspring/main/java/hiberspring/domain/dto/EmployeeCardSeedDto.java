package hiberspring.domain.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class EmployeeCardSeedDto {
    private String number;

    public EmployeeCardSeedDto() {
    }

    @Column(unique = true)
    @NotNull
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
