package com.example.blogproject.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Authority {
    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;


}
