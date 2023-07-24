package com.raju;

public class MyPojoOutput {
    public long _timestamp;
    public String __doc_data;
    public int docLength;
    public boolean success;

    public long get_timestamp() {

        return _timestamp;
    }

    public void set_timestamp(long _timestamp) {
        this._timestamp = _timestamp;
    }

    public String get__doc_data() {
        return __doc_data;
    }

    public void set__doc_data(String __doc_data) {
        this.__doc_data = __doc_data;
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}