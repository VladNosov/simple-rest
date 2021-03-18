package net.pet.project.simplerest.configuration.jackson.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static net.pet.project.simplerest.configuration.jackson.date.DateFormatConstants.LOCAL_DATE_FORMAT;

/**
 * Realisation {@link JsonSerializer} for serialize date
 * in format {@link DateFormatConstants#LOCAL_DATE_FORMAT}
 * @author VN
 */
@Slf4j
public class JsonLocalDateSerializer extends JsonSerializer<LocalDate> {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT);

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(LocalDate value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            return;
        }
        String formattedDate = value.format(formatter);
        gen.writeString(formattedDate);
    }
}