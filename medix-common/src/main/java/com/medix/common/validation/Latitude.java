package com.medix.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Latitude in decimal degrees, -90 to 90. Null is valid; combine with @NotNull when required.
 */
@Documented
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@DecimalMin("-90.0")
@DecimalMax("90.0")
public @interface Latitude {

    String message() default "Latitude must be between -90 and 90";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
