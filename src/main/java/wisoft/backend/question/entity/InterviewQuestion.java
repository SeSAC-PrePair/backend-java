package wisoft.backend.question.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interview_questions", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_role", columnList = "role"),
        @Index(name = "idx_difficulty", columnList = "difficulty")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "category")
    private String category;

    @Column(name = "role")
    private String role;

    @Column(name = "experience")
    private String experience;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "source_type")
    private String sourceType;

    @Column(columnDefinition = "TEXT")
    private String idealAnswer;

    @Column(columnDefinition = "TEXT")
    private String keywords;  // "cooperate,team" 형식
}