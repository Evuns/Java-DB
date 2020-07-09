package hiberspring.service.impl;

import com.google.gson.Gson;
import hiberspring.common.GlobalConstants;
import hiberspring.domain.dto.TownSeedDto;
import hiberspring.domain.entity.Town;
import hiberspring.repository.TownRepository;
import hiberspring.service.TownService;
import hiberspring.util.ValidationUtil;
import hiberspring.util.ValidationUtilImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
@Transactional
public class TownServiceImpl implements TownService {
    private final ModelMapper modelMapper;
    private final ValidationUtilImpl validationUtil;
    private final TownRepository townRepository;
    private final Gson gson;

    @Autowired
    public TownServiceImpl(ModelMapper modelMapper,
                           ValidationUtilImpl validationUtil,
                           TownRepository townRepository, Gson gson) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townRepository = townRepository;
        this.gson = gson;
    }

    @Override
    public Boolean townsAreImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsJsonFile() throws IOException {
        return Files.readString(Path.of(GlobalConstants.TOWNS_FILE_PATH));
    }

    @Override
    public String importTowns(String townsFileContent) throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        TownSeedDto[] townSeedDto = this.gson.fromJson(
                new FileReader(GlobalConstants.TOWNS_FILE_PATH), TownSeedDto[].class);
        Arrays.stream(townSeedDto).forEach(townDto -> {
            if (this.validationUtil.isValid(townDto)) {
                if (this.townRepository.findByName(townDto.getName()) == null) {
                    Town town = modelMapper.map(townDto, Town.class);
                    this.townRepository.saveAndFlush(town);
                    stringBuilder.append("Successfully imported Town ").append(town.getName());
                } else {
                    stringBuilder.append(String.format("Town %s already in DB", townDto.getName()));
                }
            } else {
                stringBuilder.append("Error: Invalid data.");
            }
            stringBuilder.append(System.lineSeparator());
        });

        return stringBuilder.toString();
    }

    @Override
    public Town getTownByName(String name) {
        return this.townRepository.findByName(name);
    }
}
