package com.fsb.Service;

import com.fsb.pojo.DTO.UserReviewCreateDTO;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.pojo.DTO.UserReviewUpdateDTO;
import com.fsb.pojo.entity.Review;
import com.fsb.result.PageResult;

import java.util.List;

public interface ReviewService {

//    // 查询所有评论
//    public List<Review> findAll();
//    //  添加评论
//    public boolean add(Review review);
//    // 修改评论
//    public boolean update(Review review);
//    // 删除评论
//    public boolean delete(int id);
//
//    // 根据ID查询评论
//    Review findById(int reviewID);
//
//    //查询是否存在该评论
//    public boolean exists(Review review);




    Long addReview(UserReviewCreateDTO dto, String username);

    PageResult list(UserReviewPageQueryDTO dto, String username);

    void delete(Long id, String memberUsername);

    void deleteAny(Long id);

    void updateReview(UserReviewUpdateDTO dto, String memberUsername);
}
