package com.fishman.oj_backend_judge_service.judge.codesandbox;


import com.fishman.oj_backend_model.model.codesandbox.ExecuteCodeRequest;
import com.fishman.oj_backend_model.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
