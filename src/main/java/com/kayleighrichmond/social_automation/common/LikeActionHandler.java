package com.kayleighrichmond.social_automation.common;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.LikePostsRequest;

public interface LikeActionHandler {

    void processLikePosts(String accountId, LikePostsRequest likePostsRequest);

    Platform getPlatform();

}
