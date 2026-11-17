package com.subject.commons.result;
import lombok.Data;
/**
 * 全局统一响应结果
 */
@Data
public class Result1<T> {
    /**
     * 响应码：200成功，500失败
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    // 成功响应带数据
    public static <T> Result1<T> success(T data) {
        Result1<T> result = new Result1<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功响应无数据
    public static <T> Result1<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result1<T> error(String msg) {
        Result1<T> result = new Result1<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}