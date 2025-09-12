package com.javabattle.arena.service;

import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemSubmissionService {
    
    @Autowired
    private ProblemSubmissionRepository repository;
    
    public ProblemSubmission save(ProblemSubmission submission) {
        return repository.save(submission);
    }
}