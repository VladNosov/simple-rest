package net.pet.project.simplerest.configuration.jackson.date;

import lombok.experimental.UtilityClass;

/**
 * Constants for date formats
 * @author VN
 */
@UtilityClass
public class DateFormatConstants {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_DATE_TIME_FORMAT = DATE_FORMAT;
    public static final String LOCAL_DATE_TIME_WITH_TIMEZONE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSXXX";
}