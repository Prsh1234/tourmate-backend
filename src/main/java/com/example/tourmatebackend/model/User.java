package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String password; // Nullable for Google users

    private String firstName;
    private String lastName;

    @Lob
    @Column(name = "profilePic", columnDefinition = "LONGBLOB")
    private byte[] profilePic;

    @Enumerated(EnumType.STRING)
    private Role role = Role.TRAVELLER; // Default TRAVELLER

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-guide")
    private Guide guide;

    @OneToMany(mappedBy = "traveller", cascade = CascadeType.ALL)
    @JsonManagedReference("user-tour")
    private List<Tour> bookedTours; // ✅ Added — tours this user has booked


    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }
    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public Guide getGuide() {
        return guide;
    }
    public void setGuide(Guide guide) {
        this.guide = guide;
    }

    public List<Tour> getBookedTours() {
        return bookedTours;
    }
    public void setBookedTours(List<Tour> bookedTours) {
        this.bookedTours = bookedTours;
    }
}
