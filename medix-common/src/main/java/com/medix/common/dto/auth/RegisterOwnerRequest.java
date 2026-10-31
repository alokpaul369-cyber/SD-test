package com.medix.common.dto.auth;

import com.medix.common.dto.pharmacy.PharmacyRequest;
import com.medix.common.validation.PhoneNumber;
import com.medix.common.validation.StrongPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Pharmacy owner registration. Creates a user with role PHARMACY_OWNER and their first
 * pharmacy in status PENDING (an admin must approve it before it appears in search).
 */
public record RegisterOwnerRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @PhoneNumber String phone,
        @StrongPassword String password,
        @NotNull @Valid PharmacyRequest pharmacy
) {
}
