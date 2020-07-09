package hiberspring.service;

import hiberspring.domain.entity.Branch;
import hiberspring.domain.entity.Product;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ProductService {

    Boolean productsAreImported();

    String readProductsXmlFile() throws IOException;

    String importProducts() throws JAXBException, FileNotFoundException;

    Product getByNameAndBranch(String name, String branch);

    Set<Branch> getBranchesWithProducts();
}
