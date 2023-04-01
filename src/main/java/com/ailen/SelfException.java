package com.ailen;

import lombok.Getter;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;
import java.util.Set;

@Getter
public class SelfException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private Integer code;

    public SelfException(String message) {
        super(message);
        this.code = 0;
    }

    public SelfException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SelfException(String message, Throwable cause) {
        super(message, cause);
    }

}
