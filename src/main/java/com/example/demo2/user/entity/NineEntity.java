package com.example.demo2.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "nine_tb")
public class NineEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String docId;
    private String firstName;
    private String lastName;
    private String remarks;
}