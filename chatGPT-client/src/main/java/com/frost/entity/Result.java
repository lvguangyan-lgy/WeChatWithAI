package com.frost.entity;



import java.io.Serializable;
import java.util.HashMap;

/**
 * 用于增删改操作
 */
public class Result extends HashMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private Result(int resultCode, String resultMsg) {
        super.put(Constants.ReturnVal.RET_CODE, resultCode);
        super.put(Constants.ReturnVal.RET_MSG, resultMsg);
    }

    private Result(int resultCode, int errorCode, String resultMsg) {
        this(resultCode, resultMsg);
        super.put(Constants.ReturnVal.ERROR_CODE, errorCode);
    }

    public static Result success(String succMsg) {
        return new Result(Constants.ReturnVal.RET_CODE_SUCC, succMsg);
    }

    public static Result success(String succMsg, Object data) {
        Result result = new Result(Constants.ReturnVal.RET_CODE_SUCC, succMsg);
        result.setRetData(data);
        return result;
    }

    public static Result error(Exception e) {
        return new Result(Constants.ReturnVal.RET_CODE_ERROR, e.getMessage());
    }

    public static Result error(String errorMsg) {
        return new Result(Constants.ReturnVal.RET_CODE_ERROR, errorMsg);
    }

    public static Result error(int errorCode, String errorMsg) {
        return new Result(Constants.ReturnVal.RET_CODE_ERROR, errorCode, errorMsg);
    }

    /**
     * 返回当前对象，链式调用
     */
    @Override
    public Result put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public void setRetData(Object data) {
        this.put(Constants.ReturnVal.RET_DATA, data);
    }
}
