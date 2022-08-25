package com.example.myvocab.util;


import org.joda.time.*;

import java.time.LocalDateTime;


public class TimeStampFormat {
    public String format(LocalDateTime createdAt) {
        DateTime commentedAt=new DateTime(createdAt.getYear(),createdAt.getMonthValue(),createdAt.getDayOfMonth(),createdAt.getHour(),createdAt.getMinute(),createdAt.getSecond());

        DateTime now = DateTime.now();
        Minutes minutesBetween = Minutes.minutesBetween(commentedAt, now);
        if (minutesBetween.isLessThan(Minutes.ONE)) {
            return "Vừa xong";
        }
        Hours hoursBetween = Hours.hoursBetween(commentedAt, now);
        if (hoursBetween.isLessThan(Hours.ONE)) {
//            return formatMinutes(minutesBetween.getMinutes());
            return minutesBetween.getMinutes()+" phút trước";
        }
        Days daysBetween = Days.daysBetween(commentedAt, now);
        if (daysBetween.isLessThan(Days.ONE)) {
//            return formatHours(hoursBetween.getHours());
            return hoursBetween.getHours()+" giờ trước";
        }
        Weeks weeksBetween = Weeks.weeksBetween(commentedAt, now);
        if (weeksBetween.isLessThan(Weeks.ONE)) {
//            return formatDays(daysBetween.getDays());
            return daysBetween.getDays()+" ngày trước";
        }
        Months monthsBetween = Months.monthsBetween(commentedAt, now);
        if (monthsBetween.isLessThan(Months.ONE)) {
//            return formatWeeks(weeksBetween.getWeeks());
            return weeksBetween.getWeeks()+" tuần trước";
        }
        Years yearsBetween = Years.yearsBetween(commentedAt, now);
        if (yearsBetween.isLessThan(Years.ONE)) {
//            return formatMonths(monthsBetween.getMonths());
            return monthsBetween.getMonths()+" tháng trước";
        }
//        return formatYears(yearsBetween.getYears());
        return yearsBetween.getYears()+" năm trước";
    }

    private String formatMinutes(long minutes) {
        return format(minutes, " minute ago", " minutes ago");
    }

    private String formatHours(long hours) {
        return format(hours, " hour ago", " hours ago");
    }

    private String formatDays(long days) {
        return format(days, " day ago", " days ago");
    }

    private String formatWeeks(long weeks) {
        return format(weeks, " week ago", " weeks ago");
    }

    private String formatMonths(long months) {
        return format(months, " month ago", " months ago");
    }

    private String formatYears(long years) {
        return format(years, " year ago", " years ago");
    }

    private String format(long hand, String singular, String plural) {
        if (hand == 1) {
            return hand + singular;
        } else {
            return hand + plural;
        }
    }
}
