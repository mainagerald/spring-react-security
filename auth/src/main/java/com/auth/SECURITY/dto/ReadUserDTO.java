package com.auth.SECURITY.dto;//package com.masqani.masqani.user.application.dto;

import com.auth.SECURITY.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class ReadUserDTO {
    private UUID publicId;
    private String email;
    private String imageUrl;
    private Role role;
}