package ru.example.lam.server.coreserver.utils.qpredicates;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Класс создает {@link Predicate} для QuerydslPredicateExecutor репозитория.
 * Приватный конструктор, вызывается через builder, можно собрать через buildAnd или buildOr(логическое И, ИЛИ соответственно)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {
    private final List<Predicate> predicates = new ArrayList<>();

    /**
     * Метод для добавления параметров и условий.
     * Проверяет если параметр не null, то выполняется переданная функция
     */
    public <T> QPredicates add(T object, Function<T, Predicate> function) {
        if (object != null) {
            predicates.add(function.apply(object));
        }
        return this;
    }

    public Predicate buildAnd() {
        return ExpressionUtils.allOf(predicates);
    }

    public Predicate buildOr() {
        return ExpressionUtils.anyOf(predicates);
    }

    public static QPredicates builder() {
        return new QPredicates();
    }
}
