package com.example.demo2.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimePattern.LINE.formatter;
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimePattern.DATE_LINE.formatter;

    private DateTimeUtil() {}

    public static void main(String[] args) {
        String createDateTime = "2021-09-23 20:27:45";
        LocalDateTime dateTime = string2DateTime(createDateTime);
        ZonedDateTime local = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        System.out.println("local:" + local);
        System.out.println("local timestamp:" + local.toInstant().toEpochMilli());
        ZonedDateTime zone = transform(local, ZoneId.of("Z"));
        System.out.println("zone:" + zone);
        System.out.println("zone timestamp:" + zone.toInstant().getEpochSecond());
        System.out.println(msec2ZonedDateTime(1632752137545L));
    }

    public static LocalDateTime local2UTC(LocalDateTime time) {
        ZonedDateTime local = ZonedDateTime.of(time, ZoneId.systemDefault());
        return transform(local, ZoneId.of("Z")).toLocalDateTime();
    }

    public static long local2UTCLong(LocalDateTime time) {
        ZonedDateTime local = ZonedDateTime.of(time, ZoneId.systemDefault());
        return transform(local, ZoneId.of("Z")).toInstant().toEpochMilli();
    }

    public static ZonedDateTime transform(ZonedDateTime zonedDateTime, ZoneId zone){
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        Objects.requireNonNull(zone, "zone");
        return zonedDateTime.withZoneSameInstant(zone);
    }

    public static String getCurrentDate() {
        return DEFAULT_DATE_FORMATTER.format(LocalDate.now());
    }

    public static String getCurrentDate(DateTimePattern pattern) {
        return pattern.formatter.format(LocalDate.now());
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

    public static ZonedDateTime msec2ZonedDateTime(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
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