package com.example.tourmatebackend.dto.user;

public class ChangeProfilePicRequest {
    private byte[] profilePic; // must be byte[]

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
}
