package net.pet.project.simplerest.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import net.pet.project.simplerest.configuration.jackson.date.JsonLocalDateDeserializer;
import net.pet.project.simplerest.configuration.jackson.date.JsonLocalDateSerializer;
import net.pet.project.simplerest.configuration.jackson.date.JsonLocalDateTimeDeserializer;
import net.pet.project.simplerest.configuration.jackson.date.JsonLocalDateTimeSerializer;
import net.pet.project.simplerest.controller.error.ApiExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static net.pet.project.simplerest.configuration.jackson.date.DateFormatConstants.LOCAL_DATE_TIME_WITH_TIMEZONE_FORMAT;

/**
 * @author VN
 */
@Slf4j
@Configuration
public class CommonConfig {

    private static final String JACKSON_MAPPER_BEAN_NAME = "objectMapper";
    private static final String LOG_TAG = "[COMMON_CONFIG] ::";

    private final Environment env;

    @Autowired
    public CommonConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @ConditionalOnMissingBean(annotation = ControllerAdvice.class)
    public ApiExceptionHandler apiExceptionHandler() {
        log.info("{} Init autoconfig do rest exception handler", LOG_TAG);
        return new ApiExceptionHandler(env);
    }

    /**
     * Bean for serialize/deserialize.
     * @return {@link ObjectMapper}
     */
    @Bean(name = JACKSON_MAPPER_BEAN_NAME)
    public ObjectMapper objectMapper() {
        log.info("{} init jackson mapper", LOG_TAG);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(LOCAL_DATE_TIME_WITH_TIMEZONE_FORMAT));
        objectMapper.getSerializationConfig().withFeatures(WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // LocalDate
        SimpleModule localDateModule = new SimpleModule();
        localDateModule.addDeserializer(
                LocalDate.class,
                new JsonLocalDateDeserializer()
        );
        localDateModule.addSerializer(
                LocalDate.class,
                new JsonLocalDateSerializer()
        );
        objectMapper.registerModule(localDateModule);

        // LocalDateTime
        SimpleModule localDateTimeModule = new SimpleModule();
        localDateTimeModule.addSerializer(
                LocalDateTime.class,
                new JsonLocalDateTimeSerializer()
        );
        localDateTimeModule.addDeserializer(
                LocalDateTime.class,
                new JsonLocalDateTimeDeserializer()
        );
        objectMapper.registerModule(localDateTimeModule);

        return objectMapper;
    }
}