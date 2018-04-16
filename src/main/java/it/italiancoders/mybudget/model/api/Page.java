package it.italiancoders.mybudget.model.api;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import org.apache.ibatis.session.RowBounds;

public class Page<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("contents")
    private List<T> content;
    private boolean first;
    private boolean last;
    private int number;
    private int totalPages;
    private int size;
    private long totalElements;

    public Page(List<T> content, int pageNumber,int pageSize, long totalElements) {

        this.setContent(content);
        long approssModelPageSize = (totalElements /pageSize);
        this.size = pageSize;

        if (approssModelPageSize * pageSize < totalElements) {
            this.setTotalPages((int) (approssModelPageSize + 1));
        } else {
            this.setTotalPages((int) approssModelPageSize);
        }

        this.number = pageNumber;
        this.setFirst(pageNumber == 0 ? true : false);

        if (totalElements > 0) {
            this.setLast(pageNumber + 1 == this.getTotalPages() ? true : false);
        } else {
            this.setLast(true);
        }
        this.setTotalElements(totalElements);

    }

    public Page(List<T> content, boolean first, boolean last, int idPage, int totalPages, int pageSize, int totalElemens) {
        this.setContent(content);
        this.setFirst(first);
        this.setLast(last);
        this.number = idPage;
        this.setTotalPages(totalPages);
        this.size = pageSize;
        this.setTotalElements(totalElemens);
    }


    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

}