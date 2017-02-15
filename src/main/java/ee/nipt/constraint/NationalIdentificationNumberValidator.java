package ee.nipt.constraint;

import ee.nipt.util.IdUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NationalIdentificationNumberValidator implements ConstraintValidator<NationalIdentificationNumber, String> {

    @Override
    public void initialize(NationalIdentificationNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return IdUtils.isValid(value);
    }
}
