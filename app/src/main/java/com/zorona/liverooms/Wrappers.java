package com.zorona.liverooms;

import java.util.List;

final public class Wrappers {

    public static class Collection<T> {

        public List<T> data;
    }

    public static class Meta {

        public int from;
        public int page;
        public int pages;
        public int to;
        public int total;
    }

    public static class Paginated<T> extends Collection<T> {

        public Meta meta;
    }

    public static class Single<T> {

        public T data;
    }
}
