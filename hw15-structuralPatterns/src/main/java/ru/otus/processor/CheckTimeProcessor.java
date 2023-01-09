package ru.otus.processor;

import ru.otus.model.Message;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class CheckTimeProcessor implements Processor {

    private final Supplier<LocalDateTime> dateTimeProvider;

    public CheckTimeProcessor(Supplier<LocalDateTime> dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        int second = dateTimeProvider.get().getSecond();
        if (second % 2 == 0) {
            throw new RuntimeException();
        }
        return message;
    }
}
