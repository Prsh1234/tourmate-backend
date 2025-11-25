package com.example.tourmatebackend.dto.admin;


public class GuideDecisionResponseDTO {

    private int guideId;
    private String status;

    public GuideDecisionResponseDTO() {}

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
