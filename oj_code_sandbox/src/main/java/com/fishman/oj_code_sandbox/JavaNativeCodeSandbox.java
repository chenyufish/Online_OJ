package com.fishman.oj_code_sandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.fishman.oj_code_sandbox.model.ExecuteCodeRequest;
import com.fishman.oj_code_sandbox.model.ExecuteCodeResponse;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandbox implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static void main(String[] args) {
        JavaNativeCodeSandbox javaNativeCodeSandbox =new JavaNativeCodeSandbox();
        ExecuteCodeRequest executeCodeRequest=new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1,2","3,4"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse=javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        List<String> InputList =executeCodeRequest.getInputList();
        String code =executeCodeRequest.getCode();
        String language=executeCodeRequest.getLanguage();

        String userDir=System.getProperty("user.dir");
        String globalCodePathName=userDir+ File.separator+GLOBAL_CODE_DIR_NAME;

        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }

        String userCodeParentPath=globalCodePathName+File.separator+ UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            int exitValue= compileProcess.waitFor();
            if (exitValue == 0) {
                System.out.println("编译成功");
                //分批获取进程的正常输出
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                String compileOutputLine;
                StringBuilder compileOutputStringBuilder =new StringBuilder();
                //逐行读取
                while((compileOutputLine= bufferedReader.readLine())!=null){
                    compileOutputStringBuilder.append(compileOutputLine);
                }
                System.out.println(compileOutputStringBuilder);
            }
            else{
                System.out.println("编译失败"+exitValue);

                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                String compileOutputLine;
                StringBuilder compileOutputStringBuilder =new StringBuilder();
                while((compileOutputLine= bufferedReader.readLine())!=null){
                    compileOutputStringBuilder.append(compileOutputLine);
                }

                BufferedReader errorBuffereReader =new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                String errorCompileOutputLine;
                StringBuilder errorCompileOutputStringBuilder =new StringBuilder();
                while((errorCompileOutputLine= bufferedReader.readLine())!=null){
                    errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                }
                System.out.println(errorCompileOutputStringBuilder);
                
            }
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
