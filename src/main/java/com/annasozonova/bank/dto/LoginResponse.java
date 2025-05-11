package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload after successful authentication,
 * containing a JWT access token and its type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after successful authentication")
public class LoginResponse {

    /**
     * JWT access token issued to the authenticated user.
     */
    @Schema(description = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNjE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;

    /**
     * Token type prefix, typically "Bearer".
     */
    @Schema(description = "Token type prefix",
            example = "Bearer")
    private String tokenType = "Bearer";
}
