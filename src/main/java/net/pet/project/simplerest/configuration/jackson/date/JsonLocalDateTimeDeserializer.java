package net.pet.project.simplerest.configuration.jackson.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.pet.project.simplerest.configuration.jackson.date.DateFormatConstants.LOCAL_DATE_TIME_FORMAT;

/**
 * Realisation {@link JsonDeserializer} for deserialize date
 * in format {@link DateFormatConstants#LOCAL_DATE_TIME_FORMAT}
 * @author VN
 */
@Slf4j
public class JsonLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext context) throws IOException {
        final ObjectCodec objectCodec = jsonParser.getCodec();
        final TextNode node = objectCodec.readTree(jsonParser);
        final String text = node.textValue();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return LocalDateTime.parse(text, formatter);
    }
}