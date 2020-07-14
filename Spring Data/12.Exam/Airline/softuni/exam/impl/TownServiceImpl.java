package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import softuni.exam.constant.GlobalConstants;
import softuni.exam.model.dto.TownSeedDto;
import softuni.exam.model.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TownServiceImpl implements TownService {

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final TownRepository townRepository;
    private final Gson gson;

    @Autowired
    public TownServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil,
                           TownRepository townRepository, Gson gson) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townRepository = townRepository;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(GlobalConstants.TOWNS_FILE_PATH));
    }

    @Override
    public String importTowns() throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        TownSeedDto[] townSeedDto = gson.fromJson(
                new FileReader(GlobalConstants.TOWNS_FILE_PATH), TownSeedDto[].class);

        for (TownSeedDto townDto : townSeedDto) {
            if (this.validationUtil.isValid(townDto)) {

                if (this.townRepository.findByName(townDto.getName()) == null) {
                    Town town = this.modelMapper.map(townDto, Town.class);

                    this.townRepository.saveAndFlush(town);
                    stringBuilder.append(GlobalConstants.SUCCESSFUL_MESSAGE).append(String.format(
                            "Town %s - %d",
                            town.getName(), town.getPopulation()));

                } else {
                    stringBuilder.append("This town already exist");
                }
            } else {
                stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Town");
            }
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }


    @Override
    public Town getTownByName(String name) {
        return this.townRepository.findByName(name);
    }
}
