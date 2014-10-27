package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.user.UserAuthProviderDetail;

/**
 * 
 * @author azi
 * 
 */

public interface UserAuthProviderDetailDao extends PagingAndSortingRepository<UserAuthProviderDetail, Integer> {
    public UserAuthProviderDetail findByProviderIdAndProviderUserId(int providerId, String providerUserId);

    public UserAuthProviderDetail findByUserIdAndProviderId(int userId, int providerId);
}
