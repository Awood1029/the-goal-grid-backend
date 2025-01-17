// File: main/java/com/thegoalgrid/goalgrid/config/ModelMapperConfig.java
package com.thegoalgrid.goalgrid.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Configure matching strategy
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());

        // Add any custom mappings if necessary
        // Example: mapper.typeMap(Source.class, Destination.class).addMappings(...);

        return mapper;
    }
}
