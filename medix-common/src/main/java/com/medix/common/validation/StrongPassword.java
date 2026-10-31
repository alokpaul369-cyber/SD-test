package com.medix.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Password rule: 8-72 characters with at least one letter and one digit.
 * 72 is the BCrypt input limit.
 */
@Documented
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@NotBlank
@Size(min = 8, max = 72)
@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$")
public @interface StrongPassword {

    String message() default "Password must be 8-72 characters and contain at least one letter and one digit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
