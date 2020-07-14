package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.constant.GlobalConstants;
import softuni.exam.model.dto.PassengerSeedDto;
import softuni.exam.model.Passenger;
import softuni.exam.model.Town;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Service
public class PassengerServiceImpl implements PassengerService {

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PassengerRepository passengerRepository;
    private final Gson gson;
    private final TownService townService;


    @Autowired
    public PassengerServiceImpl(ModelMapper modelMapper,
                                ValidationUtil validationUtil,
                                PassengerRepository passengerRepository, Gson gson,
                                TownService townService) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.passengerRepository = passengerRepository;
        this.gson = gson;
        this.townService = townService;
    }


    @Override
    public boolean areImported() {
        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Path.of(GlobalConstants.PASSENGERS_FILE_PATH));
    }

    @Override
    public String importPassengers() throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        PassengerSeedDto[] passengerSeedDtos = gson.fromJson(
                new FileReader(GlobalConstants.PASSENGERS_FILE_PATH), PassengerSeedDto[].class);

        for (PassengerSeedDto passengerDto : passengerSeedDtos) {
            if (this.validationUtil.isValid(passengerDto)) {

                if (this.passengerRepository.findByEmail(passengerDto.getEmail()) == null) {
                    Passenger passenger = this.modelMapper.map(passengerDto, Passenger.class);
                    Town town = this.townService.getTownByName(passengerDto.getTown());
                    if (town == null) {
                        stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Passenger");
                    }

                    passenger.setTown(town);
                    this.passengerRepository.saveAndFlush(passenger);
                    stringBuilder.append(GlobalConstants.SUCCESSFUL_MESSAGE).append(String.format(
                            "Passenger %s - %s",
                            passenger.getLastName(), passenger.getEmail()));

                } else {
                    stringBuilder.append("This passenger email already exist");
                }
            } else {
                stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Passenger");
            }
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }


    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Passenger> passengers = this.passengerRepository.getByTicketsCountAndEmail();
        passengers.stream().forEach(passenger -> {
            stringBuilder.append(String.format("Passenger %s  %s%n" +
                            "\tEmail - %s%n" +
                            "\tPhone - %s%n" +
                            "\tNumber of tickets - %d%n",
                    passenger.getFirstName(), passenger.getLastName(), passenger.getEmail(),
                    passenger.getPhoneNumber(), passenger.getTickets().size()));
            stringBuilder.append(System.lineSeparator());
        });

        return stringBuilder.toString();
    }

    @Override
    public Passenger getPassengerByEmail(String email) {
        return this.passengerRepository.findByEmail(email);
    }
}
