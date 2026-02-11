package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String subject;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime created_At = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY) // Many messages belong to one user
    @JoinColumn(name = "user_id")      // foreign key in Support table
    private User user;

    private boolean view;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreated_At() { return created_At; }
    public void setCreated_At(LocalDateTime created_At) { this.created_At = created_At; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;  // ✅ assign to the instance variable
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
}
