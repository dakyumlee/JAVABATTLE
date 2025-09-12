package com.javabattle.arena.web;

import com.javabattle.arena.model.Problem;
import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.repository.ProblemRepository;
import com.javabattle.arena.service.ClaudeCodeEvaluationService;
import com.javabattle.arena.service.ProblemSubmissionService;
import com.javabattle.arena.dto.AIEvaluationResult;
import com.javabattle.arena.dto.SubmissionRequest;
import com.javabattle.arena.dto.SubmissionResult;
import com.javabattle.arena.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/practice")
public class PracticeController {

    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private ClaudeCodeEvaluationService claudeEvaluationService;
    
    @Autowired
    private ProblemSubmissionService submissionService;

    @Autowired
    private JwtUtils jwtUtils;
    @GetMapping("")
    public String practice(Model model) {
        List<String> categories = problemRepository.findDistinctCategories();
        model.addAttribute("categories", categories);
        return "practice";
    }

    @GetMapping("/solve/{problemId}")
    public String solveProblem(@PathVariable Long problemId, Model model) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            model.addAttribute("problem", problem.get());
            return "solve";
        }
        return "redirect:/practice";
    }

    @PostMapping("/submit/{problemId}")
    @ResponseBody
    public ResponseEntity<SubmissionResult> submitAnswer(
            @PathVariable Long problemId,
            @RequestBody SubmissionRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String userId = jwtUtils.getUserIdFromRequest(httpRequest);
            Optional<Problem> problemOpt = problemRepository.findById(problemId);
            
            if (!problemOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(SubmissionResult.error("존재하지 않는 문제입니다."));
            }
            
            Problem problem = problemOpt.get();
            
            AIEvaluationResult aiResult = claudeEvaluationService.evaluateCode(
                request.getAnswer(),
                problem.getDescription(),
                problem.getExpectedConcepts() != null ? problem.getExpectedConcepts() : problem.getTitle()
            );
            
            ProblemSubmission submission = ProblemSubmission.builder()
                .userId(userId)
                .problemId(problemId)
                .answer(request.getAnswer())
                .isCorrect(aiResult.isCorrect())
                .score(aiResult.getScore())
                .aiFeedback(aiResult.getFeedback())
                .isCreativeSolution(aiResult.isCreative())
                .submittedAt(LocalDateTime.now())
                .build();
            
            submissionService.save(submission);
            
            return ResponseEntity.ok(SubmissionResult.builder()
                .isCorrect(aiResult.isCorrect())
                .score(aiResult.getScore())
                .feedback(aiResult.getFeedback())
                .strengths(aiResult.getStrengths())
                .improvements(aiResult.getImprovements())
                .isCreative(aiResult.isCreative())
                .build());
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(SubmissionResult.error("답안 제출 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/api/problems")
    @ResponseBody
    public ResponseEntity<List<Problem>> getProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category) {

        List<Problem> problems;

        if (difficulty != null && category != null) {
            problems = problemRepository.findByDifficultyAndCategory(difficulty, category);
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficulty(difficulty);
        } else if (category != null) {
            problems = problemRepository.findByCategory(category);
        } else {
            problems = problemRepository.findAll();
        }

        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/problems/{problemId}")
    @ResponseBody
    public ResponseEntity<Problem> getProblem(@PathVariable Long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        return problem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = problemRepository.findDistinctCategories();
        return ResponseEntity.ok(categories);
    }
}