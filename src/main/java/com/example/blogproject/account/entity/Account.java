package com.example.blogproject.account.entity;

import com.example.blogproject.account.dto.AccountReqDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Account {
    @Id @Column(name="account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nickname;

    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @ManyToMany
    @JoinTable(
            name = "account_authority",
            joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public Account(AccountReqDto accountReqDto) {
        this.email = accountReqDto.getEmail();
        this.password = accountReqDto.getPassword();
        this.nickname = accountReqDto.getNickname();
    }
}
