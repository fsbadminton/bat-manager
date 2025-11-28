package com.fsb.Controller.user;


import com.fsb.Service.ReviewService;
import com.fsb.pojo.DTO.UserReviewCreateDTO;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.pojo.DTO.UserReviewUpdateDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user/review")
public class UserReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 发布评论
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addReview(@RequestBody UserReviewCreateDTO dto) {
        String username = "zhangsan";
        log.info("用户 {} 添加评论", username);
        // username可以从token解析，也可以前端传
        Long id = reviewService.addReview(dto, username);
        return Result.success(id);
    }


    /**
     * 查询本人评论
     * @param dto
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult> list(UserReviewPageQueryDTO dto) {
        String username = "zhangsan";
        PageResult pageResult = reviewService.list(dto, username);
        return Result.success(pageResult);
    }

    /**
     * 更新用户评论
     */
    @PutMapping("/update")
    public Result updateReview(@RequestBody UserReviewUpdateDTO dto) {
        dto.setMemberUsername("zhangsan");
        reviewService.updateReview(dto);
        return Result.success();
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        String username = "zhangsan";
        reviewService.delete(id);
        return Result.success();
    }
}
