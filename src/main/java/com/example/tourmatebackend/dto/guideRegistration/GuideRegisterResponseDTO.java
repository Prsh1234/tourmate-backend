package com.example.tourmatebackend.dto.guideRegistration;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.GuideExperience;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.Language;

import java.time.LocalDate;
import java.util.List;

public class GuideRegisterResponseDTO {

    private int userId;
    private String location;
    private int guideId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<GuideExperience> experience;
    private List<Language> languages;
    private List<Category> categories;
    private String bio;
    private Double price;

    private GuideStatus status;

    private byte[] profilePic;

    //government details
    private byte[] governmentPic;
    private  String governmentNumber;
    private LocalDate dob;

    public GuideRegisterResponseDTO() {}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGuideId(){return guideId;}
    public void setGuideId(int guideId){
        this.guideId = guideId;
    }
    public String getBio() {
        return bio;
    }
    public GuideStatus getStatus(){
        return status;
    }
    public void setStatus(GuideStatus status){
        this.status = status;
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
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
