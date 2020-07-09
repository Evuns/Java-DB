package hiberspring.service;

import hiberspring.domain.entity.Employee;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;


public interface EmployeeService {

   Boolean employeesAreImported();

   String readEmployeesXmlFile() throws IOException;

   String importEmployees() throws JAXBException, FileNotFoundException;

   String exportProductiveEmployees();

   Employee getByFirstNameLastNamePosition(String fName, String lName, String position);

}
