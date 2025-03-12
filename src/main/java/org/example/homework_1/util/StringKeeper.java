package org.example.homework_1.util;

import org.example.homework_1.util.interfaces.StringKeeperInterface;

public class StringKeeper implements StringKeeperInterface {

    private static final String MENU_TEXT = """
    \n==== Управление финансами ====
    1. Финансовые операции
    2. Меню Анализа финансов
    3. Установить месячный бюджет
    4. Добавить цель накопления
    5. Проверить цели
    6. Показать бюджет
    7. Показать баланс
    8. Меню транзакций
    9. Настройки пользователя
    0. Выйти из аккаунта
    Выберите действие: """;

    private static final String SETTINGS_USER_MENU = """
    1. Изменить Имя пользователя
    2. Изменить Email
    3. Изменить Пароль
    4. Удалить пользователя
    5. Стать Админом
    0. В начало
    Выберите действие: """;

    @Override
    public void printMenu() {
        System.out.println(MENU_TEXT);
    }

    @Override
    public void printUserMenu() {
        System.out.println(SETTINGS_USER_MENU);

    }
}
