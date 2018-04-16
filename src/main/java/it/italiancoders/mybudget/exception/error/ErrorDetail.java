package it.italiancoders.mybudget.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class ErrorDetail {

    private String title;

    private String detail;

    private long timeStamp;

    private String developerMessage;

    private String exception;

    private int code;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @lombok.Builder.Default
    private Integer subcode = 0;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, List<ValidationError>> validationErrors ;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getSubcode() {
        return subcode;
    }
    public void setSubcode(int subcode) {
        this.subcode = subcode;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getDeveloperMessage() {
        return developerMessage;
    }
    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, List<ValidationError>> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, List<ValidationError>> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String title;
        private int status;
        private String detail;
        private long timeStamp;
        private String developerMessage;
        private String exception;
        private int code;
        private Map<String, List<ValidationError>> validationErrors ;

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder timeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder developerMessage(String developerMessage) {
            this.developerMessage = developerMessage;
            return this;
        }

        public Builder exception(String exception) {
            this.exception = exception;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder validationErrors(Map<String, List<ValidationError>> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ErrorDetail build() {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setTitle(title);
            errorDetail.setSubcode(status);
            errorDetail.setDetail(detail);
            errorDetail.setTimeStamp(timeStamp);
            errorDetail.setDeveloperMessage(developerMessage);
            errorDetail.setException(exception);
            errorDetail.setCode(code);
            errorDetail.setValidationErrors(validationErrors);
            return errorDetail;
        }
    }
}
