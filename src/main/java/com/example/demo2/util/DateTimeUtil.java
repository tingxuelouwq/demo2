package com.example.demo2.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimePattern.LINE.formatter;
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimePattern.DATE_LINE.formatter;

    private DateTimeUtil() {}

    public static String getCurrentDate() {
        return DEFAULT_DATE_FORMATTER.format(LocalDate.now());
    }

    public static String getCurrentDate(DateTimePattern pattern) {
        return pattern.formatter.format(LocalDate.now());
    }

    public static void main(String[] args) {
        System.out.println(msec2DateTime(1556755201000L));
        System.out.println(DateTimeUtil.dateTime2String(DateTimeUtil.msec2DateTime(1556755201000L), DateTimeUtil.DateTimePattern.DATE_LINE));
    }

    public static String getCurrentTime() {
        return DateTimePattern.TIME_NONE_LINE.formatter.format(LocalTime.now());
    }

    public static String getCurrentDateTime() {
        return DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
    }

    public static String getCurrentDateTime(DateTimePattern pattern) {
        return pattern.formatter.format(LocalDateTime.now());
    }

    public static long getMsec(LocalDateTime time) {
        Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    public static LocalDateTime msec2DateTime(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDateTime string2DateTime(String timeStr) {
        return LocalDateTime.parse(timeStr, DEFAULT_DATETIME_FORMATTER);
    }

    public static LocalDateTime string2DateTime(String timeStr, DateTimePattern pattern) {
        return LocalDateTime.parse(timeStr, pattern.formatter);
    }

    public static String dateTime2String(LocalDateTime time) {
        return DEFAULT_DATETIME_FORMATTER.format(time);
    }

    public static String dateTime2String(LocalDateTime time, DateTimePattern pattern) {
        return pattern.formatter.format(time);
    }

    public enum DateTimePattern {

        LINE("yyyy-MM-dd HH:mm:ss"),
        NONE_LINE("yyyyMMdd HH:mm:ss"),
        NONE_SEPARATOR("yyyyMMddHHmmss"),

        MILSEC_LINE("yyyy-MM-dd HH:mm:ss.SSS"),
        MILSEC_NONE_LINE("yyyyMMdd HH:mm:ss.SSS"),
        MILSEC_NONE_SEPARATOR("yyyyMMddHHmmssSSS"),

        DATE_LINE("yyyy-MM-dd"),
        DATE_NONE_LINE("yyyyMMdd"),

        TIME_NONE_LINE("HHmmss");

        private DateTimeFormatter formatter;

        DateTimePattern(String pattern) {
            formatter = DateTimeFormatter.ofPattern(pattern);
        }
    }
}