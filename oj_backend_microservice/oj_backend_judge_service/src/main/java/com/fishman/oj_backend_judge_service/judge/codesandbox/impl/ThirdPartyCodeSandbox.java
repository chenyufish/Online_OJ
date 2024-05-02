package com.fishman.oj_backend_judge_service.judge.codesandbox.impl;


import com.fishman.oj_backend_judge_service.judge.codesandbox.CodeSandbox;
import com.fishman.oj_backend_model.model.codesandbox.ExecuteCodeRequest;
import com.fishman.oj_backend_model.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
