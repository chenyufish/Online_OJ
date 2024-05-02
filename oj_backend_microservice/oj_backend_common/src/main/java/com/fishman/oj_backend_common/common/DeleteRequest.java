package com.fishman.oj_backend_common.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author fishman
 * 
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}