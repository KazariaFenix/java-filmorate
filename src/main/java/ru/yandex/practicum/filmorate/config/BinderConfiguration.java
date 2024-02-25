package ru.yandex.practicum.filmorate.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinderConfiguration {

    @Bean
    public MeterBinder meterBinder() {
        return meterRegistry -> {
            Counter.builder("BINDER_COUNTER")
                    .description("Счётчик обращения к методу  List<Film> getFilmList()")
                    .register(meterRegistry);

            Counter.builder("GET_MPA_BY_ID")
                    .tags("id", "0")
                    .description("Счётчик обращения к методу FilmMPA getMPAById(@PathVariable int id)")
                    .register(meterRegistry);
        };
    }
}
