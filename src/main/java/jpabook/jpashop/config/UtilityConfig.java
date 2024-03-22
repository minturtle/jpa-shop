package jpabook.jpashop.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilityConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
