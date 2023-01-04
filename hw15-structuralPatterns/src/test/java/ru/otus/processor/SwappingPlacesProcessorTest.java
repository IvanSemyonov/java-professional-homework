package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import static org.assertj.core.api.Assertions.assertThat;

class SwappingPlacesProcessorTest {

    @Test
    @DisplayName("Должен поменять местами field11 и field12")
    void swappingPlacesProcessorTest() {
        Processor processor = new SwappingPlacesProcessor();

        String field11 = "field11";
        String field12 = "field12";
        Message message = new Message.Builder(1)
                .field11(field11)
                .field12(field12)
                .build();

        Message result = processor.process(message);

        assertThat(result.getField11()).isEqualTo(field12);
        assertThat(result.getField12()).isEqualTo(field11);
    }
}