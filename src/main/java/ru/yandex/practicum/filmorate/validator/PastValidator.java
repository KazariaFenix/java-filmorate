package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Component
public class PastValidator implements ConstraintValidator<IsAfter, LocalDate> {
    private String validDate;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        validDate = constraintAnnotation.current();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        String[] splitDate = validDate.split("_");
        return value != null && value.isAfter(LocalDate.of(Integer.valueOf(splitDate[0]),
                Integer.valueOf(splitDate[1]), Integer.valueOf(splitDate[2])));
    }
}
