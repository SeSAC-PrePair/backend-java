package wisoft.backend.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wisoft.backend.auth.dto.DeleteUserRequest;
import wisoft.backend.auth.dto.DeleteUserResponse;
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
}
