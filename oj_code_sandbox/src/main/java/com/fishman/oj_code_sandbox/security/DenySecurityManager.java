package com.fishman.oj_code_sandbox.security;

import java.security.Permission;

//默认安全管理器
public class DenySecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("权限异常"+perm.toString());
    }
}
