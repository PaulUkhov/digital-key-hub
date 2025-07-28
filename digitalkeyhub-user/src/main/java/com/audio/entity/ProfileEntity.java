package com.audio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "profiles")
public class ProfileEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String bio;

    private String avatarUrl;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
