package wisoft.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wisoft.backend.auth.dto.request.DeleteUserRequest;
import wisoft.backend.auth.dto.request.RewardDeductRequest;
import wisoft.backend.auth.dto.request.UserProfileUpdateRequest;
import wisoft.backend.auth.dto.response.*;
import wisoft.backend.auth.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/me")
    public ResponseEntity<DeleteUserResponse> deleteUser(
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody DeleteUserRequest request
    ) {
        DeleteUserResponse response = userService.deleteUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/rewards")
    public ResponseEntity<RewardDeductResponse> deductRewardPoints(
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody RewardDeductRequest request
    ) {
        RewardDeductResponse response = userService.deductRewardPoints(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @RequestHeader("X-User-ID") String userId
    ) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileUpdateResponse> updateUserProfile(
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        UserProfileUpdateResponse response = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/summary")
    public ResponseEntity<UserSummaryResponse> getUserSummary(
            @RequestHeader("X-User-ID") String userId
    ) {
        UserSummaryResponse response = userService.getUserSummary(userId);
        return ResponseEntity.ok(response);
    }
}
