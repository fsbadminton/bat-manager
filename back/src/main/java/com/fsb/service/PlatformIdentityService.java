package com.fsb.Service;

import com.fsb.pojo.DTO.PlatformBindingConfirmRequest;
import com.fsb.pojo.VO.PlatformBindingTokenVO;
import com.fsb.pojo.VO.PlatformBindingVO;
import com.fsb.pojo.VO.AiResolvedIdentityVO;

import java.util.List;

public interface PlatformIdentityService {
    PlatformBindingTokenVO startBinding(Long customerId, String platform);

    PlatformBindingVO confirmBinding(PlatformBindingConfirmRequest request);

    List<PlatformBindingVO> listBindings(Long customerId);

    void unbind(Long customerId, String platform, String externalUserId);

    void unbindByReference(Long customerId, String platform, String bindingReference);

    void moveBinding(Long sourceCustomerId, Long targetCustomerId, String platform, String externalUserId);

    void moveBindingByReference(Long sourceCustomerId, Long targetCustomerId, String platform, String bindingReference);

    PlatformBindingVO resolve(String platform, String externalUserId);

    String resolveUsername(String platform, String externalUserId);

    AiResolvedIdentityVO resolveAiIdentity(String platform, String externalUserId);
}
