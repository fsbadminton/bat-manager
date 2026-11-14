package com.fsb.Service;

import com.fsb.pojo.DTO.AiTicketCreateRequest;
import com.fsb.pojo.VO.AiTicketVO;

import java.util.List;

public interface AiSupportTicketService {
    AiTicketVO create(Long userId, AiTicketCreateRequest request);

    List<AiTicketVO> list(Long userId, int limit);

    AiTicketVO get(Long userId, String ticketNo);
}
