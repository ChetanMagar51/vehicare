package com.vehicare.modules.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String address;

    private boolean active;
}