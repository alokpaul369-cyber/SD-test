package com.medix.common.dto.admin;

import com.medix.common.enums.Role;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Query parameters of GET /api/admin/users. All filters are optional.
 *
 * @param q       name or email contains
 * @param role    only this role
 * @param enabled only enabled (true) or disabled (false) users
 */
public record AdminUserQuery(
        @Size(max = 100) String q,
        Role role,
        Boolean enabled,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {

    public AdminUserQuery {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

    public static AdminUserQuery all(int page, int size) {
        return new AdminUserQuery(null, null, null, page, size);
    }
}
