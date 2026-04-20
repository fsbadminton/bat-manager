package com.fsb.Controller.admin;

import com.fsb.Service.ReviewService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.PermissionConstants;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/list")
    public Result<PageResult> list(UserReviewPageQueryDTO dto) {
        AuthUser authUser = requireAuthUser();
        if (!authUser.hasPermission(PermissionConstants.REVIEW_READ_ANY)) {
            throw new AuthException(403, "Review read permission required");
        }
        return Result.success(reviewService.list(dto, dto.getUsername()));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthUser authUser = requireAuthUser();
        if (!authUser.hasPermission(PermissionConstants.REVIEW_DELETE_ANY)) {
            throw new AuthException(403, "Review delete permission required");
        }
        reviewService.deleteAny(id);
        return Result.success();
    }

    private AuthUser requireAuthUser() {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return authUser;
    }
}
