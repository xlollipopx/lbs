package com.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorCodeDto {

    @NotBlank
    private String code;
    @NotBlank
    private String email;

    @NotBlank
    @JsonProperty("two_factor_token")
    private String twoFactorToken;

}