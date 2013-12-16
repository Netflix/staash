package com.netflix.paas.rest.util;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Pair<L, R> {
    
    private final L left;
    
    private final R right;
    
    
    /**
     * @param left
     * @param right
     */
    public Pair(L left, R right) {
        Validate.notNull(left);
        this.left = left;
        Validate.notNull(right);
        this.right = right;
    }
    
    /**
     * @return L
     */
    public L getLeft() {
        return left;
    }
    
    /**
     * @return R
     */
    public R getRight() {
        return right;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other ==  this) return true;
        if (!(other instanceof Pair))
            return false;
        Pair<?,?> that = (Pair<?,?>) other;
        return new EqualsBuilder().
                   append(this.left, that.left).
                   append(this.right, that.left).
                   isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                   append(this.left).
                   append(this.right).
                   toHashCode();                   
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(").
        append(this.left).
        append(",").
        append(this.right).
        append(")");
        return sb.toString();
    }
}

