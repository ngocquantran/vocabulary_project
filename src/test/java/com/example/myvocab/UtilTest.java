package com.example.myvocab;


import com.example.myvocab.util.TimeStampFormat;
import com.example.myvocab.util.Validation;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


public class UtilTest {

    @Test
    void validate_email() {
        String email1 = "quan@gmail.com";
        String email2 = "@gmail.com";
        String email3 = "quan@gmail.";
        String email4 = "quan@gmail";
        String email5 = "quangmail.com";
        String email6 = "quan@.com";
        String email7 = "@gmail.com";
        String email8 = "quan.@gmail.com";
        assertThat(Validation.isValidEmail(email1)).isTrue();
        assertThat(Validation.isValidEmail(email2)).isFalse();
        assertThat(Validation.isValidEmail(email3)).isFalse();
        assertThat(Validation.isValidEmail(email4)).isFalse();
        assertThat(Validation.isValidEmail(email5)).isFalse();
        assertThat(Validation.isValidEmail(email6)).isFalse();
        assertThat(Validation.isValidEmail(email7)).isFalse();
        assertThat(Validation.isValidEmail(email8)).isFalse();
    }

    @Test
    void time_stamp() {
        TimeStampFormat timeStampFormat = new TimeStampFormat();

        LocalDateTime time1 = LocalDateTime.now().minusSeconds(0);
        assertThat(timeStampFormat.format(time1)).isEqualToIgnoringCase("vừa xong");

        LocalDateTime time2 = LocalDateTime.now().minusMinutes(59);
        assertThat(timeStampFormat.format(time2)).containsIgnoringCase("phút trước");

        LocalDateTime time3 = LocalDateTime.now().minusMinutes(61);
        assertThat(timeStampFormat.format(time3)).containsIgnoringCase("giờ trước");

        LocalDateTime time4 = LocalDateTime.now().minusHours(26);
        assertThat(timeStampFormat.format(time4)).containsIgnoringCase("ngày trước");

        LocalDateTime time5 = LocalDateTime.now().minusDays(32);
        assertThat(timeStampFormat.format(time5)).containsIgnoringCase("tháng trước");

        LocalDateTime time6 = LocalDateTime.now().minusWeeks(80);
        assertThat(timeStampFormat.format(time6)).containsIgnoringCase("năm trước");

    }

}
