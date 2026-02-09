package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.GuideExperience;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.Language;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime createdAt = LocalDateTime.now();
    //bio
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
    @ElementCollection(targetClass = GuideExperience.class)
    @Enumerated(EnumType.STRING)
    private List<GuideExperience> experience;

    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    private List<Language> languages;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //MISC
    @Enumerated(EnumType.STRING)
    private GuideStatus status = GuideStatus.PENDING; // PENDING, APPROVED, REJECTED

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference("user-guide")
    private User user;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("guide-tour")
    private List<Tour> tours; // ✅ Added — list of tours created by this guide

    //skills and expertise
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guide_categories", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<Category> categories;

    private String bio;
    private Double price;

    @Lob
    @Column(name = "profilePic", columnDefinition = "LONGBLOB")
    private byte[] profilePic;

    //government details
    @Lob
    @Column(name = "governmentPic", columnDefinition = "LONGBLOB")
    private byte[] governmentPic;
    private  String governmentNumber;
    private LocalDate dob;


    //Bank details

    private String bankName;
    private String accountHolderName;
    private String accountNumber;

    private LocalDate joined = LocalDate.now();


    // Getters & Setters
    public LocalDate getJoined() {
        return joined;
    }

    public void setJoined(LocalDate joined) {
        this.joined = joined;
    }



    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<GuideExperience> getExperience() {
        return experience;
    }

    public void setExperience(List<GuideExperience> experience) {
        this.experience = experience;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public GuideStatus getStatus() {
        return status;
    }

    public void setStatus(GuideStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tour> getTours() {
        return tours;
    }

    public void setTours(List<Tour> tours) {
        this.tours = tours;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public byte[] getGovernmentPic() {
        return governmentPic;
    }

    public void setGovernmentPic(byte[] governmentPic) {
        this.governmentPic = governmentPic;
    }

    public String getGovernmentNumber() {
        return governmentNumber;
    }

    public void setGovernmentNumber(String governmentNumber) {
        this.governmentNumber = governmentNumber;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
