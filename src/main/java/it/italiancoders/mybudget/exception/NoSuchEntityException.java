package it.italiancoders.mybudget.exception;

public class NoSuchEntityException extends RuntimeException{

    public NoSuchEntityException() {
        super();
    }

    public NoSuchEntityException(String s)
    {
        super(s);
    }

    public NoSuchEntityException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public NoSuchEntityException(Throwable throwable)
    {
        super(throwable);
    }
}
