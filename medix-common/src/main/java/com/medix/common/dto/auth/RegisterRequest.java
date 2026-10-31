package com.medix.common.dto.auth;

import com.medix.common.validation.PhoneNumber;
import com.medix.common.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Customer self-registration. Creates a user with role CUSTOMER.
 */
public record RegisterRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @PhoneNumber String phone,
        @StrongPassword String password
) {
}
