package com.example.tourmatebackend.dto.user;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.states.Role;

public class UserDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;

    private Role role = Role.TRAVELLER; // Default TRAVELLER

    private String phoneNumber;

    private byte[] profilePic;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bio = user.getBio();
        this.role = user.getRole();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePic = user.getProfilePic();
    }


// Getters & Setters


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

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

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
