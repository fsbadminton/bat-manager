package com.fsb.Controller.user;

import com.fsb.Service.ReviewService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.PermissionConstants;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.UserReviewCreateDTO;
import com.fsb.pojo.DTO.UserReviewPageQueryDTO;
import com.fsb.pojo.DTO.UserReviewUpdateDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/review")
public class UserReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public Result<Long> addReview(@RequestBody UserReviewCreateDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REVIEW_CREATE_OWN, "Review create permission required");
        Long id = reviewService.addReview(dto, authUser.getUsername());
        return Result.success(id);
    }

    @GetMapping("/list")
    public Result<PageResult> list(UserReviewPageQueryDTO dto) {
        AuthUser authUser = requireAuthUser();
        String usernameFilter;
        if (authUser.hasPermission(PermissionConstants.REVIEW_READ_ANY)) {
            usernameFilter = dto.getUsername();
        } else if (authUser.hasPermission(PermissionConstants.REVIEW_READ_OWN)) {
            usernameFilter = authUser.getUsername();
        } else {
            throw new AuthException(403, "Review read permission required");
        }
        PageResult pageResult = reviewService.list(dto, usernameFilter);
        return Result.success(pageResult);
    }

    @PutMapping("/update")
    public Result<Void> updateReview(@RequestBody UserReviewUpdateDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REVIEW_UPDATE_OWN, "Review update permission required");
        reviewService.updateReview(dto, authUser.getUsername());
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REVIEW_DELETE_OWN, "Review delete permission required");
        reviewService.delete(id, authUser.getUsername());
        return Result.success();
    }

    private AuthUser requireAuthUser() {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return authUser;
    }

    private void requirePermission(AuthUser authUser, String permission, String message) {
        if (!authUser.hasPermission(permission)) {
            throw new AuthException(403, message);
        }
    }
}
