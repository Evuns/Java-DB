package hiberspring.service.impl;

import hiberspring.common.GlobalConstants;
import hiberspring.domain.dto.ProductSeedRootDto;
import hiberspring.domain.entity.Branch;
import hiberspring.domain.entity.Product;
import hiberspring.repository.ProductRepository;
import hiberspring.service.BranchService;
import hiberspring.service.ProductService;
import hiberspring.util.ValidationUtil;
import hiberspring.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final ProductRepository productRepository;
    private final BranchService branchService;
    private final XmlParser xmlParser;

    @Autowired
    public ProductServiceImpl(ModelMapper modelMapper,
                              ValidationUtil validationUtil,
                              ProductRepository productRepository,
                              BranchService branchService, XmlParser xmlParser) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.productRepository = productRepository;
        this.branchService = branchService;
        this.xmlParser = xmlParser;
    }

    @Override
    public Boolean productsAreImported() {
        return this.productRepository.count() > 0;
    }

    @Override
    public String readProductsXmlFile() throws IOException {
        return Files.readString(Path.of(GlobalConstants.PRODUCTS_FILE_PATH));
    }

    @Override
    public String importProducts() throws JAXBException, FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        ProductSeedRootDto productSeedRootDto = this.xmlParser.parseXml(ProductSeedRootDto.class,
                GlobalConstants.PRODUCTS_FILE_PATH);
        productSeedRootDto.getProducts().forEach(productSeedDto -> {
            if (this.validationUtil.isValid(productSeedDto)) {
                if (this.productRepository.findByNameAndBranch_Name(productSeedDto.getName(),
                        productSeedDto.getBranch()) == null) {
                    Product product = this.modelMapper.map(productSeedDto, Product.class);
                    Branch branch = this.branchService.getByName(productSeedDto.getBranch());
                    if (branch == null) {
                        stringBuilder.append("Error: Invalid data.");
                    } else {
                        product.setBranch(branch);
                        this.productRepository.saveAndFlush(product);
                        stringBuilder.append(String.format("Successfully imported %s.",product.getName()));
                    }
                } else {
                    stringBuilder.append("This product is already in the data base.");
                }
            } else {
                stringBuilder.append("Error: Invalid data.");
            }
            stringBuilder.append(System.lineSeparator());
        });


        return stringBuilder.toString();
    }

    @Override
    public Product getByNameAndBranch(String name, String branch) {
        return this.productRepository.findByNameAndBranch_Name(name, branch);
    }

    @Override
    public Set<Branch> getBranchesWithProducts() {
        return this.productRepository.findBranchesWithProducts();
    }
}
