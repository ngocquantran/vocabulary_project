package com.example.myvocab.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtils {
    private DateUtils() {
    }

    public static String getMsStartEndTime(Instant startTime, Instant endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMillis() + "ms";
    }

    // this method will get the first day of current week in format yyMMdd and then return the integer value of it
    public static Integer getFirstDayOfWeek() {
        LocalDate date = LocalDate.now();
        LocalDate firstDayOfWeek = date.with(DayOfWeek.MONDAY);
        return Integer.parseInt(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyMMdd")));
    }

    // this method will get the last day of current week in format yyMMdd and then return the integer value of it
    public static Integer getLastDayOfWeek() {
        LocalDate date = LocalDate.now();
        LocalDate lastDayOfWeek = date.with(DayOfWeek.SUNDAY);
        return Integer.parseInt(lastDayOfWeek.format(DateTimeFormatter.ofPattern("yyMMdd")));
    }

    // this method will get yesterday in format yyMMdd and then return the integer value of it
    public static Integer getPastDateByDuration(int duration) {
        LocalDate date = LocalDate.now();
        return Integer.parseInt(date.minusDays(duration).format(DateTimeFormatter.ofPattern("yyMMdd")));
    }

    // this method will get today in format yyMMdd and then return the integer value of it
    public static Integer getToday() {
        LocalDate date = LocalDate.now();
        return Integer.parseInt(date.format(DateTimeFormatter.ofPattern("yyMMdd")));
    }



    // this method will get date from format yyMMdd and then return the weekday
    public static DayOfWeek getWeekDay(Integer date) {
        LocalDate localDate = LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyMMdd"));
        return localDate.getDayOfWeek();
    }

    // this method will return the end of the last day of current week in timestamp
    public static Instant getEndOfLastDayOfWeek() {
        LocalDate date = LocalDate.now();
        LocalDate lastDayOfWeek = date.with(DayOfWeek.SUNDAY);
        LocalDateTime localDateTime = LocalDateTime.of(lastDayOfWeek, LocalTime.MAX);
        return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
    }

    // this method will compare two date in format yyMMdd and return the difference in days
    public static Integer compareDate(Integer firstDate, Integer secondDate) {
        LocalDate localDate1 = LocalDate.parse(firstDate.toString(), DateTimeFormatter.ofPattern("yyMMdd"));
        LocalDate localDate2 = LocalDate.parse(secondDate.toString(), DateTimeFormatter.ofPattern("yyMMdd"));
        return (int) Duration.between(localDate1.atStartOfDay(), localDate2.atStartOfDay()).toDays();
    }

    // this method will compare two Date and return the difference in seconds
    public static Long compareDateInSecond(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / 1000;
    }

    // this method will return the remaining days until the end of the current week
    public static Integer getRemainingDaysOfWeek() {
        LocalDate date = LocalDate.now();
        LocalDate lastDayOfWeek = date.with(DayOfWeek.SUNDAY);
        return (int) Duration.between(date.atStartOfDay(), lastDayOfWeek.atStartOfDay()).toDays();
    }
}
