package app.model;

import java.time.LocalDateTime;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public User(int id, String fullName, String email, String passwordHash,
                Role role, UserStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public User(String fullName, String email, String passwordHash,
                Role role, UserStatus status) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
}
