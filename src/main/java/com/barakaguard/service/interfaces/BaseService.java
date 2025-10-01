package main.java.com.barakaguard.service.interfaces;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    List<T> getAll();
    Optional<T> getById(ID id);
    void add(T entity);
    void update(T entity);
    void delete(ID id);
}
