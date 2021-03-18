package net.pet.project.simplerest.configuration.jackson.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Realisation {@link JsonDeserializer} for deserialize date
 * in format {@link DateFormatConstants#LOCAL_DATE_FORMAT}
 * @author VN
 */
public class JsonISOLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        final String text = jsonParser.getValueAsString();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return ZonedDateTime.parse(text).toLocalDateTime();
    }
}