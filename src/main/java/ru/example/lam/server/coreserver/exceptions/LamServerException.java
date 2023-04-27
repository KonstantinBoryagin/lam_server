package ru.example.lam.server.coreserver.exceptions;


import ru.example.lam.server.service.journal.IJournalService;

/**
 * Исключение для приложения
 * @see IJournalService
 */
public class LamServerException extends Exception{

    public LamServerException(String message) {
        super(message);
    }

}
