package com.kayleighrichmond.social_automation.service.nst.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateProfileResponse {

    private CreateProfileResponseData data;
    private boolean err;
    private String msg;
    private int code;

    @Data
    public static class CreateProfileResponseData {
        private String profileId;
        private String fingerprintId;
        private String groupId;
        private String teamId;
        private String userId;
        private String name;
        private int kernel;
        private String kernelVersion;
        private String kernelMilestone;
        private String uaFullVersion;
        private int platform;
        private String platformVersion;
        private boolean saveLocal;
        private int status;
        private String note;
        private Object tags;
        private String _id;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
