package hiberspring.repository;

import hiberspring.domain.entity.Branch;
import hiberspring.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByFirstNameAndLastNameAndPosition(String firstName, String lastName, String position);


    List<Employee> findAllByBranch(Branch branch);
    
}
