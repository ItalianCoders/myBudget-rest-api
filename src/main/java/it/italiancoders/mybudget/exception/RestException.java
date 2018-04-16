package it.italiancoders.mybudget.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException{

    private String title;

    private HttpStatus status;

    private String detail;

    private int code;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RestException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public RestException(Throwable throwable)
    {
        super(throwable);
    }
    public RestException(HttpStatus status, String title, String detail, int code) {
        super(title);
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.code = code;
    }

    public RestException(String title, Throwable cause, HttpStatus status, String detail, int code) {
        super(title, cause);
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.code = code;
    }
}
