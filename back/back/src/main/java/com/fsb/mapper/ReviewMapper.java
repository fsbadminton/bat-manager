package com.fsb.Mapper;

import com.fsb.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {
    @Select("select * from review where isDeleted=0")
    List<Review> findAll();

    @Insert("insert into review values(#{reviewID},#{customerID},#{racketID},#{rating},#{comment},#{reviewDate},#{isDeleted})")
    int add(Review review);

    @Update("update review set customerID=#{customerID},racketID=#{racketID},rating=#{rating},comment=#{comment},reviewDate=#{reviewDate} where reviewID=#{reviewID} and isDeleted=0")
    int update(Review review);

    //软删除评论
    @Update("update review set isDeleted=1 where reviewID= #{id}")
    int delete(int id);

    //查出评论的最大条数
    @Select("SELECT MAX(reviewID) FROM review where isDeleted=0")
    Integer findMaxId();

    //根据id查询
    @Select("select * from review where reviewID= #{id} and isDeleted=0")
    Review findById(int reviewID);
}
