package com.vehicare.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	
	private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String email;
    private String password;

}
