package com.fishman.fishman_oj.judge.codesandbox;


import com.fishman.fishman_oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.fishman.fishman_oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.fishman.fishman_oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.fishman.fishman_oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class CodeSandboxTest {
    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void excuteCode(){
        CodeSandbox codeSandbox=new RemoteCodeSandbox();
        String code="int main(){}";
        String language= QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList= Arrays.asList("1,2","3,4");
        ExecuteCodeRequest executeCodeRequest =ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse=codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
    @Test
    void excuteCodeByValue(){
        CodeSandbox codeSandbox=CodeSandboxFactory.newInstance(type);
        String code="int main(){}";
        String language= QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList= Arrays.asList("1,2","3,4");
        ExecuteCodeRequest executeCodeRequest =ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse=codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        while(scanner.hasNext()){
            String type=scanner.next();
            CodeSandbox codeSandbox=new RemoteCodeSandbox();
            String code="int main(){}";
            String language= QuestionSubmitLanguageEnum.JAVA.getValue();
            List<String> inputList= Arrays.asList("1,2","3,4");
            ExecuteCodeRequest executeCodeRequest =ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse=codeSandbox.executeCode(executeCodeRequest);
            Assertions.assertNotNull(executeCodeResponse);
        }
    }

}