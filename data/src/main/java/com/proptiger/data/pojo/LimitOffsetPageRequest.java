package com.proptiger.data.pojo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.proptiger.data.util.Constants;

/**
 * This is an implementations of Pageable inteface to provide functionality of
 * limit and offset kind of queries. So instead of using page and page size this
 * class will make queries like limit and offset.
 * 
 * @author Rajeev Pandey
 * 
 */
public class LimitOffsetPageRequest implements Pageable {

    private final int  offset;
    private final int  rows;
    private final Sort sort;

    public LimitOffsetPageRequest(int offset, int rows) {
        this(offset, rows, null);
    }

    /*
     * By default fetch 10 rows from first row
     */
    public LimitOffsetPageRequest() {
        this(0, Constants.DEFAULT_NO_OF_ROWS, null);
    }

    public LimitOffsetPageRequest(int page, int size, Direction direction, String... properties) {

        this(page, size, new Sort(direction, properties));
    }

    public LimitOffsetPageRequest(int page, int size, Sort sort) {

        if (0 > page) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }

        if (0 >= size) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero!");
        }

        this.offset = page;
        this.rows = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset;
    }

    @Override
    public int getPageSize() {
        return rows;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public String toString() {
        return "[offset=" + offset + ", rows=" + rows + "]";
    }

}
