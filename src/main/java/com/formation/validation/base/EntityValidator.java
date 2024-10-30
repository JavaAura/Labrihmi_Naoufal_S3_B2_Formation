package com.formation.validation.base;

public interface EntityValidator<T> {
    void validateForCreate(T entity);

    void validateForUpdate(Long id, T entity);
}