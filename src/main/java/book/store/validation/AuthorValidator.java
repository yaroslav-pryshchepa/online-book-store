package book.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class AuthorValidator implements ConstraintValidator<Author, String> {
    private static final String PATTERN_OF_AUTHOR = "^([A-Za-zА-Яа-яЁёІіЇїЄєҐґ]+[-']?\\s?)+$";

    @Override
    public boolean isValid(String author, ConstraintValidatorContext constraintValidatorContext) {
        return author != null && Pattern.compile(PATTERN_OF_AUTHOR).matcher(author).matches();
    }
}
