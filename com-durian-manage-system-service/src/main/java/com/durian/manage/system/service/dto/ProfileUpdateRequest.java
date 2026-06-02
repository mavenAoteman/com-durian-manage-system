package com.durian.manage.system.service.dto;

public class ProfileUpdateRequest {

    /** 皂坊名（留空则恢复"用户名+的皂坊"默认） */
    private String workshopName;

    /** 个人简介 */
    private String bio;

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
