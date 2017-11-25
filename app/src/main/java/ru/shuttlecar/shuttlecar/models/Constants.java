package ru.shuttlecar.shuttlecar.models;


public class Constants {
    public static final String BASE_URL = "http://shuttlecar.ru/index.php/";
    public static final String MAP_URL = "https://maps.googleapis.com/";

    public static final String MAP_REQUEST = "/maps/api/directions/json";

    public static final String PDIS = "PDIS";
    public static final String PDEL = "PDEL";

    public static final String REGISTER_OPERATION = "register";
    public static final String LOGIN_OPERATION = "login";
    public static final String ADD_RATING_OPERATION = "addRat";
    public static final String GET_RATING_OPERATION = "getRat";
    public static final String FIND_ORDER_OPERATION = "findOrd";
    public static final String FIND_MY_ORDER_OPERATION = "findMyOrd";
    public static final String FIND_RESERVE_ORDER_OPERATION = "findResOrd";
    public static final String ADD_ORDER_OPERATION = "addOrd";
    public static final String DELETE_ORDER_OPERATION = "delOrd";
    public static final String CHANGE_PROFILE_OPERATION = "chgProf";
    public static final String CHANGE_PASSWORD_OPERATION = "chgPass";
    public static final String CHANGE_IMAGE_OPERATION = "chgImg";
    public static final String RESERVE_ORDER = "resOrd";
    public static final String DELETE_RESERVE_ORDER = "delResOrd";
    public static final String SEND_MESSAGE = "sendMessage";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String CAR_BRAND = "car_brand";
    public static final String CAR_MODEL = "car_model";
    public static final String TELEPHONE = "tel";
    public static final String PHOTO_PERSON = "photo_person";
    public static final String PHOTO_CAR = "photo_car";
    public static final String UNIQUE_ID = "un_id";
    public static final String UIU = "uiu";
    public static final String ID = "id";

    public static final Boolean RATING_FIND_USER = false;
    public static final Boolean RATING_ADD_RATING_USER = true;

    public static final String IMAGE_PERSON = "false";
    public static final String IMAGE_CAR = "true";

    public static final String RESULT_ERROR = "Ошибка";
    public static final String RESULT_COMPLETE = "Успешно";

    public static final String NO_NETWORK = "Нет подключения с сервером";

    public static final String PREFERENCES = "ShuttleCarPref";

    public static final String RESULT_SUCCESS = "success";

    public static final String IS_LOGGED_IN = "logged";

    public static final int IS_PROCESS_EXIT_PROFILE = 0;

    public static final String COMPLETE = "";
    public static final String ERROR_EMPTY = "Здесь пусто :(";

    public static final String PASS_ERROR_LENGTH = "Пароль содержит менее 8 символов";
    public static final String PASS_ERROR_UNIQUE = "Пароль содержит менее 4 уникальных символов";
    public static final String PASS_ERROR_SPACE = "Пароль содержит пробелы";
    public static final String PASS_ERROR_ALL = "Пароль не отвечает требованиям политики";

    public static final String PASS_NO_SIM = "Пароли не схожи";
    public static final String PASS_ERROR_SIM_NAME = "Пароль схож с именем";
    public static final String PASS_ERROR_SIM_EMAIL = "Пароль схож с email";
    public static final String PASS_ERROR_SIM_PASS = "Новый пароль схож со старым";

    public static final String EMAIL_ERROR = "Это не почта :)";

    public static final String PLACE_ERROR_SIM = "Места схожи";
    public static final String PLACE_ERROR_EMPTY = "Выберите 2 точки";

    public static final String TEL_ERROR = "Это не номер телефона :)";
    public static final String TEL_ERROR_SIM = "Вы не можете оценить самого себя";

    public static final String RATING_ERROR = "Вы уже оценивали этого пользователя";

    public static final String NAME_ERROR_LENGHT = "Макс. длина имени - 255";

    public static final String CAR_ERROR_LENGHT = "Макс. длина - 127";

    public static final String NO_TEL_ERROR = "Необходимо указать номер телефона";

    public static final String ORDER_ITEMS_LIST = "OrderItems";
    public static final String ORDER_ITEM = "OrderItem";
}