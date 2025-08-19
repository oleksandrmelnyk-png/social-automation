package com.kayleighrichmond.social_automation.service.api.account;

import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.web.controller.social.dto.LikePostsRequest;

public interface LikeHandler {

    void processLikePosts(String accountId, LikePostsRequest likePostsRequest);

    Platform getPlatform();

}
