package hiberspring.util;

import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Component
public class XmlParserImpl implements XmlParser {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseXml(Class<T> objectClass, String filePath) throws JAXBException, FileNotFoundException {
        try (final InputStream inputStream = new FileInputStream(filePath)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(objectClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(inputStream);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
