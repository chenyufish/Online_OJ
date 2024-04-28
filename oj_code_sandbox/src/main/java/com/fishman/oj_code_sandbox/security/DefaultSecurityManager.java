package com.fishman.oj_code_sandbox.security;

import java.security.Permission;

//默认安全管理器
public class DefaultSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认不做异常处理");
        super.checkPermission(perm);
    }
}
