package com.nganhang.sentinel.dto;

public class LoginResponseDTO {
    private boolean success;
    private String message;
    private String token;
    private UserResponseDTO user;

    // Getters and Setters
    public boolean isSuccess() { 
        return success; 
    }
    
    public void setSuccess(boolean success) { 
        this.success = success; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
    
    public String getToken() { 
        return token; 
    }
    
    public void setToken(String token) { 
        this.token = token; 
    }
    
    public UserResponseDTO getUser() { 
        return user; 
    }
    
    public void setUser(UserResponseDTO user) { 
        this.user = user; 
    }
}
