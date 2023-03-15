package priv.cookies.usercenter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import priv.cookies.usercenter.common.BaseResponse;
import priv.cookies.usercenter.common.ErrorCode;
import priv.cookies.usercenter.common.ResultUtil;

@RestControllerAdvice
@Slf4j
public class GlobalSessionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){

        //全局处理器能捕捉并返回自定义异常 但仍浏览器显示Sprinboot默认的异常处理结果

        System.out.println("捕捉到 BusinessException ~");
        log.error("businessException:" + e.getMessage(),e.getCode(),e);
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
        //return new BaseResponse(e.getCode(),null,e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("businessException:" + e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }


}
