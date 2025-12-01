package com.fsb.Service.impl;

import com.fsb.entity.Review;
import com.fsb.Mapper.ReviewMapper;
import com.fsb.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    @Override
    public List<Review> findAll() {
        return reviewMapper.findAll();
    }

    @Override
    public boolean add(Review review) {
        Integer maxId = reviewMapper.findMaxId();
        int newID = (maxId == null)? 0 : maxId+1;
        review.setReviewID(newID);
        review.setReviewDate(new java.util.Date());
        return reviewMapper.add(review)>0;
    }

    @Override
    public boolean update(Review review) {
        return reviewMapper.update(review)>0;
    }

    @Override
    public boolean delete(int id) {
        return reviewMapper.delete(id)>0;
    }

    @Override
    public Review findById(int reviewID) {
        return reviewMapper.findById(reviewID);
    }

    public boolean exists(Review review){
        if(review==null)
            return true;
        return false;
    }

}
