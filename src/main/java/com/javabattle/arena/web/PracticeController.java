package com.javabattle.arena.web;

import com.javabattle.arena.model.Problem;
import com.javabattle.arena.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/practice")
public class PracticeController {

    @Autowired
    private ProblemRepository problemRepository;

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