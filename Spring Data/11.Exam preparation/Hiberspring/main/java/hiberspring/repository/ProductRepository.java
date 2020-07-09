package hiberspring.repository;


import hiberspring.domain.entity.Branch;
import hiberspring.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByNameAndBranch_Name(String name, String branchName);

    @Query("SELECT p.branch From Product  AS p")
    Set<Branch> findBranchesWithProducts();
}
