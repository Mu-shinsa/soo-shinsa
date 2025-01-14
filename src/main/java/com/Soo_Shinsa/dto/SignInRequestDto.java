package com.Soo_Shinsa.dto;

import com.Soo_Shinsa.constant.Role;
import com.Soo_Shinsa.constant.UserStatus;
import com.Soo_Shinsa.model.User;
import com.Soo_Shinsa.model.UserGrade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignInRequestDto {
    @NotBlank
    @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNum;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$", message = "비밀번호 형식이 올바르지 않습니다. 8자 이상, 대소문자 포함, 숫자 및 특수문자(@$!%*?&#) 포함")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String role;


    private String grade;

    public User toEntity() {
        return User.builder()
                .email(email)
                .phoneNum(phoneNum)
                .password(password)
                .name(name)
                .status(UserStatus.ACTIVE)
                .role(Role.of(role))
                .build();
    }
}
