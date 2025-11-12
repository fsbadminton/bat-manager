package com.fsb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Integer reviewID;
    private Integer customerID;
    private Integer racketID;
    private Integer rating;
    private String comment;
    private Date reviewDate;
    private int isDeleted;

    @Override
    public String toString() {
        return "评论[" +
                "评论编号:" + reviewID +
                ", 客户编号:" + customerID +
                ", 球拍编号:" + racketID +
                ", 评分:" + rating +
                "星, 评论内容:" + comment +
                ", 评论日期:" + reviewDate +
                ']';
    }
}
