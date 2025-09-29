package main.java.com.barakaguard.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T, ID> {

    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T entity);

    void delete(ID id);

}
