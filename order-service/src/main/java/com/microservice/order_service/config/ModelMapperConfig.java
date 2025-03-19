package com.microservice.order_service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Xử lý chuyển đổi từ PersistentBag sang List
        modelMapper.createTypeMap(Collection.class, List.class)
                .setConverter(mappingContext -> new ArrayList<>(mappingContext.getSource()));

        return modelMapper;
    }
}
