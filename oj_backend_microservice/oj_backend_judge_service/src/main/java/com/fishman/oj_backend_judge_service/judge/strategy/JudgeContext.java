package com.fishman.oj_backend_judge_service.judge.strategy;



import com.fishman.oj_backend_model.model.codesandbox.JudgeInfo;
import com.fishman.oj_backend_model.model.dto.question.JudgeCase;
import com.fishman.oj_backend_model.model.entity.Question;
import com.fishman.oj_backend_model.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
