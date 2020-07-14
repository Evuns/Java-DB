package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.constant.GlobalConstants;
import softuni.exam.model.dto.PlaneSeedDto;
import softuni.exam.model.dto.PlaneSeedRootDto;
import softuni.exam.model.Plane;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.service.PlaneService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class PlaneServiceImpl implements PlaneService {

    private final ModelMapper modelMapper;
    private final PlaneRepository planeRepository;
    private final ValidationUtil validator;
    private final XmlParser xmlParser;

    @Autowired
    public PlaneServiceImpl(ModelMapper modelMapper,
                            PlaneRepository planeRepository, ValidationUtil validator,
                            XmlParser xmlParser) {
        this.modelMapper = modelMapper;
        this.planeRepository = planeRepository;
        this.validator = validator;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.planeRepository.count() > 0;
    }

    @Override
    public String readPlanesFileContent() throws IOException {
        return Files.readString(Path.of(GlobalConstants.PLANES_FILE_PATH));
    }

    @Override
    public String importPlanes() throws JAXBException, FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        PlaneSeedRootDto planeSeedRootDto = this.xmlParser.parseXml(PlaneSeedRootDto.class,
                GlobalConstants.PLANES_FILE_PATH);

        List<PlaneSeedDto> planeSeedDtos = planeSeedRootDto.getPlanes();
        for (PlaneSeedDto planeSeedDto : planeSeedDtos) {


            if (this.validator.isValid(planeSeedDto)) {
                if (this.planeRepository.findByRegisterNumber
                        (planeSeedDto.getRegisterNumber()) == null) {
                    Plane plane = this.modelMapper.map(planeSeedDto, Plane.class);
                    this.planeRepository.saveAndFlush(plane);
                        stringBuilder.append(GlobalConstants.SUCCESSFUL_MESSAGE)
                        .append(String.format("Plane %s", plane.getRegisterNumber()));

                } else {
                    stringBuilder.append("This plane is already in DB");
                }
            } else {
                stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Plane");
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    @Override
    public Plane getPlaneByRegisterNumber(String number) {
        return this.planeRepository.findByRegisterNumber(number);
    }
}
