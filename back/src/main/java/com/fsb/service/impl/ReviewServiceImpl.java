package com.fsb.Service.impl;

import com.fsb.pojo.DTO.UserReviewCreateDTO;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.pojo.DTO.UserReviewUpdateDTO;
import com.fsb.pojo.VO.ReviewVO;
import com.fsb.pojo.entity.Review;
import com.fsb.Mapper.ReviewMapper;
import com.fsb.Service.ReviewService;
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
//    @Override
//    public List<Review> findAll() {
//        return reviewMapper.findAll();
//    }
//
//    @Override
//    public boolean add(Review review) {
//        Integer maxId = reviewMapper.findMaxId();
//        int newID = (maxId == null)? 0 : maxId+1;
//        review.setReviewID(newID);
//        review.setReviewDate(new java.util.Date());
//        return reviewMapper.add(review)>0;
//    }
//
//    @Override
//    public boolean update(Review review) {
//        return reviewMapper.update(review)>0;
//    }
//
//    @Override
//    public boolean delete(int id) {
//        return reviewMapper.delete(id)>0;
//    }
//
//    @Override
//    public Review findById(int reviewID) {
//        return reviewMapper.findById(reviewID);
//    }
//
//    public boolean exists(Review review){
//        if(review==null)
//            return true;
//        return false;
//    }

    @Override
    @Transactional
    public Long addReview(UserReviewCreateDTO dto, String username) {
        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setProductId(dto.getProductId());
        review.setMemberUsername(username); // 从token或session取
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
    public void delete(Long id) {
        reviewMapper.delete(id);
    }

    @Override
    public void updateReview(UserReviewUpdateDTO dto) {
        Review review = reviewMapper.getByIdAndMemberUsername(dto.getId(), dto.getMemberUsername());
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }

        review.setContent(dto.getContent());
        review.setStar(dto.getStar());
        review.setMemberUsername(dto.getMemberUsername());
        review.setUpdateTime(LocalDateTime.now());

        reviewMapper.updateByIdAndMemberUsername(review);
    }

}
