package hiberspring.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "employee_cards")
public class EmployeeCard extends BaseEntity {

    private String number;

    public EmployeeCard() {
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
