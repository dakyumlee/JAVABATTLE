package com.javabattle.arena.service;

import org.springframework.stereotype.Service;
import com.javabattle.arena.model.Problem;
import java.util.*;

@Service
public class ProblemService {
    
    public Problem getRandomProblem() {
        Problem problem = new Problem();
        problem.setId(1L);
        problem.setTitle("두 수의 합");
        problem.setDescription("두 정수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.\n\n입력:\n첫째 줄에 A와 B가 주어진다. (0 < A, B < 10)\n\n출력:\n첫째 줄에 A+B를 출력한다.\n\n예제 입력:\n1 2\n\n예제 출력:\n3");
        problem.setDifficulty("EASY");
        problem.setTestInput("1 2");
        problem.setExpectedOutput("3");
        return problem;
    }
    
    public List<Problem> getAllProblems() {
        List<Problem> problems = new ArrayList<>();
        problems.add(getRandomProblem());
        return problems;
    }
}
