package com.fishman.oj_backend_judge_service.judge;



import com.fishman.oj_backend_judge_service.judge.strategy.DefaultJudgeStrategy;
import com.fishman.oj_backend_judge_service.judge.strategy.JavaLanguageJudgeStrategy;
import com.fishman.oj_backend_judge_service.judge.strategy.JudgeContext;
import com.fishman.oj_backend_judge_service.judge.strategy.JudgeStrategy;
import com.fishman.oj_backend_model.model.codesandbox.JudgeInfo;
import com.fishman.oj_backend_model.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
