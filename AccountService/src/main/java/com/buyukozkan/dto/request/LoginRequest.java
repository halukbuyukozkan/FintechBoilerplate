package com.buyukozkan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {

    @NotBlank(message = "Email can not be blank")
    private String email;

    @NotBlank(message = "Password can not be blank")
    private String password;

}
