package com.medix.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Phone number: optional leading "+", then 7-20 digits, spaces or dashes.
 * Null is valid; combine with @NotBlank when the phone is required.
 */
@Documented
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "^\\+?[0-9][0-9 -]{6,19}$")
public @interface PhoneNumber {

    String message() default "Phone number must contain 7-20 digits and may start with +";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
