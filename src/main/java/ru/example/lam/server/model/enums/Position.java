package ru.example.lam.server.model.enums;

import lombok.Getter;

import ru.example.lam.server.coreserver.exceptions.LamServerException;

/**
 * Enum для значений относительной позиции записи в журнале
 */
@Getter
public enum Position {
    NEXT("next"),
    PREVIOUS("previous"),
    CURRENT("current");

    private final String name;
    public static final String POSITION_EXCEPTION_MESSAGE = "No enum found with type: [%s]";

    Position(String name) {
        this.name = name;
    }

    /**
     * Возвращает значение позиции записи в журнале, если не указана - то вернет 'current'
     *
     * @param name значение позиции
     * @return значение позиции
     * @throws LamServerException {@link LamServerException} исключение с описанием причины
     */
    public static Position getPosition(String name) throws LamServerException {
        if (name == null)
            return CURRENT;
        for (Position position : values()) {
            if (position.getName().equals(name.toLowerCase())) {
                return position;
            }
        }
        throw new LamServerException(String.format(POSITION_EXCEPTION_MESSAGE, name));
    }
}
