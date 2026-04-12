package com.fsb.Service.impl;

import com.fsb.Mapper.ReviewMapper;
import com.fsb.Service.ReviewService;
import com.fsb.pojo.DTO.UserReviewCreateDTO;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.pojo.DTO.UserReviewUpdateDTO;
import com.fsb.pojo.VO.ReviewVO;
import com.fsb.pojo.entity.Review;
import com.fsb.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    @Transactional
    public Long addReview(UserReviewCreateDTO dto, String username) {
        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setProductId(dto.getProductId());
        review.setMemberUsername(username);
        review.setContent(dto.getContent());
        review.setStar(dto.getStar());
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        review.setIsDeleted(0);

        reviewMapper.insert(review);
        return review.getId();
    }

    @Override
    public PageResult list(UserReviewPageQueryDTO dto, String username) {
        dto.setUsername(username);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        Page<Review> list = reviewMapper.list(dto);

        List<ReviewVO> reviewVOList = new ArrayList<>();
        for (Review review : list) {
            ReviewVO reviewVO = new ReviewVO();
            BeanUtils.copyProperties(review, reviewVO);
            reviewVOList.add(reviewVO);
        }
        return new PageResult(list.getTotal(), reviewVOList);
    }

    @Override
    public void delete(Long id, String memberUsername) {
        int affectedRows = reviewMapper.deleteByIdAndMemberUsername(id, memberUsername);
        if (affectedRows <= 0) {
            throw new RuntimeException("评论不存在或无删除权限");
        }
    }

    @Override
    public void updateReview(UserReviewUpdateDTO dto, String memberUsername) {
        Review review = reviewMapper.getByIdAndMemberUsername(dto.getId(), memberUsername);
        if (review == null) {
            throw new RuntimeException("评论不存在或无编辑权限");
        }

        review.setContent(dto.getContent());
        review.setStar(dto.getStar());
        review.setMemberUsername(memberUsername);
        review.setUpdateTime(LocalDateTime.now());

        reviewMapper.updateByIdAndMemberUsername(review);
    }
}
