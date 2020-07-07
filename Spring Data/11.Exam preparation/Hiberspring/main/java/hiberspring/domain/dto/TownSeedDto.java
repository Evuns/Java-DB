package hiberspring.domain.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TownSeedDto {
    private String name;
    private Integer population;

    public TownSeedDto() {
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
