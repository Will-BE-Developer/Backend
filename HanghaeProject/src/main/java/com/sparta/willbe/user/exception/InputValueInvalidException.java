package com.sparta.willbe.user.exception;

import com.sparta.willbe._global.exception.BusinessException;
import com.sparta.willbe._global.exception.ExceptionCode;
import jdk.internal.util.xml.impl.Input;

public class InputValueInvalidException extends BusinessException {
    public InputValueInvalidException(){
        super(ExceptionCode.INPUT_VALUE_INVALID);
    }
}
