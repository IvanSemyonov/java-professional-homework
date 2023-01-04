package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class CheckTimeProcessorTest {

    @Test
    @DisplayName("Должен бросать исключение в четную секунду")
    void throwExceptionInEvenSecond() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 2));
        CheckTimeProcessor processor = new CheckTimeProcessor(() -> localDateTime);
        Message message = new Message.Builder(1).build();

        Throwable throwable = catchThrowable(() -> processor.process(message));

        assertThat(throwable).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Не должен бросать исключение в не четную секунду")
    void notThrowExceptionInEvenSecond() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 1));
        CheckTimeProcessor processor = new CheckTimeProcessor(() -> localDateTime);
        Message message = new Message.Builder(1).build();

        Message messageAfterProcess = processor.process(message);

        assertThat(messageAfterProcess).isEqualTo(message);
    }
}