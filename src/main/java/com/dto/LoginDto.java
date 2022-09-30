package com.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Getter
@Setter
@RequiredArgsConstructor
public class LoginDto {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String verificationCode;

    private String twoFactorToken;
}
