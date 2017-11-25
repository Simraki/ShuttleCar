package ru.shuttlecar.shuttlecar;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.MarkerItem;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class HelpMethods {

    static void errorShow(final CircularProgressButton btn) {
        btn.setProgress(-1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                btn.setProgress(0);
                btn.setClickable(true);
            }
        }, 500);
    }

    static void initNumberPicker(ImageView minusView, ImageView plusView, final TextView display) {

        minusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(display.getText().toString());
                number--;
                if (number > 0 && number <= 6) {
                    display.setText(String.valueOf(number));
                }
            }
        });

        plusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(display.getText().toString());
                number++;
                if (number > 0 && number <= 6) {
                    display.setText(String.valueOf(number));
                }
            }
        });
    }

    static int getValueFromNumberPicker(TextView display) {
        return Integer.parseInt(display.getText().toString());
    }

    public static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    static String password_verification(String pass) {
        if (pass.length() >= 8 && !pass.contains(" ") && countUnChar(pass) >= 4) {
            return Constants.COMPLETE;
        } else if (pass.length() == 0) {
            return Constants.ERROR_EMPTY;
        } else if (pass.length() <= 8 && !pass.contains(" ")) {
            return Constants.PASS_ERROR_LENGTH;
        } else if (pass.length() >= 8 && pass.contains(" ")) {
            return Constants.PASS_ERROR_SPACE;
        } else if (countUnChar(pass) < 4) {
            return Constants.PASS_ERROR_UNIQUE;
        } else {
            return Constants.PASS_ERROR_ALL;
        }
    }

    static String email_verification(CharSequence email) {
        if (email.toString().isEmpty()) {
            return Constants.ERROR_EMPTY;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Constants.EMAIL_ERROR;
        } else {
            return Constants.COMPLETE;
        }
    }

    static String name_verification(String name) {
        if (name.isEmpty()) {
            return Constants.ERROR_EMPTY;
        } else if (name.length() > 255) {
            return Constants.NAME_ERROR_LENGHT;
        } else {
            return Constants.COMPLETE;
        }
    }

    static String car_verification(String car) {
        if (car.length() > 127) {
            return Constants.CAR_ERROR_LENGHT;
        } else {
            return Constants.COMPLETE;
        }
    }


    static void downloadMainImage(Context context, String url, ImageView image) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(100, 100)
                .defaultDisplayImageOptions(
                        new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .build()
                )
                .build();

        ImageLoader.getInstance().init(config);

        ImageLoader.getInstance().displayImage(url, image);
    }

    public static void downloadImage(Context context, String url, ImageView image) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(
                        new DisplayImageOptions.Builder()
                                .cacheInMemory(false)
                                .cacheOnDisk(false)
                                .build()
                )
                .build();

        ImageLoader.getInstance().init(config);

        ImageLoader.getInstance().displayImage(url, image);

        ImageLoader.getInstance()
    }

    static List<MarkerItem> getStations() {
        List<MarkerItem> list = new ArrayList<>();

        list.add(new MarkerItem("10-я линия", 55.313590, 86.067686));
        list.add(new MarkerItem("13-я линия", 55.310194, 86.061538));
        list.add(new MarkerItem("1-й проезд", 55.407460, 86.050903));
        list.add(new MarkerItem("1-й Универсам", 55.357149, 86.077228));
        list.add(new MarkerItem("улица 1-я Строительная", 55.404658, 86.050254));
        list.add(new MarkerItem("21-й микрорайон", 55.409179, 86.055660));
        list.add(new MarkerItem("22-й переезд", 55.500572, 86.148085));
        list.add(new MarkerItem("2-й проезд", 55.410711, 86.059629));
        list.add(new MarkerItem("улица 2-я Строительная", 55.407482, 86.058433));
        list.add(new MarkerItem("улица 4-я Цветочная", 55.317380, 86.072144));
        list.add(new MarkerItem("62-й проезд", 55.305545, 86.140491));
        list.add(new MarkerItem("АБК", 55.520775, 86.095522));
        list.add(new MarkerItem("Автоагрегат", 55.357605, 86.032141));
        list.add(new MarkerItem("ПГ Автоагрегат", 55.309792, 86.011578));
        list.add(new MarkerItem("Автоколонна №1237", 55.320633, 86.056140));
        list.add(new MarkerItem("Автоколонна №1237 (пр. Советский)", 55.363602, 86.048308));
        list.add(new MarkerItem("Автоколонна №1962", 55.324754, 86.052523));
        list.add(new MarkerItem("Авторемзавод", 55.329805, 86.062935));
        list.add(new MarkerItem("Автостанция ВАЗ", 55.307194, 86.150402));
        list.add(new MarkerItem("Агротехснаб", 55.314815, 86.017974));
        list.add(new MarkerItem("АЗС (ул. Греческая деревня)", 55.500622, 86.116832));
        list.add(new MarkerItem("АЗС (ул. Нахимова)", 55.429782, 86.129262));
        list.add(new MarkerItem("АЗС (ул. Тухачевского)", 55.320404, 86.132695));
        list.add(new MarkerItem("АКБ Кузбассхимбанк", 55.313987, 86.103238));
        list.add(new MarkerItem("АКБ Надежда", 55.336913, 86.131852));
        list.add(new MarkerItem("АКБ Сибирский горизонт", 55.355927, 86.053411));
        list.add(new MarkerItem("улица Александрова", 55.396190, 86.019571));
        list.add(new MarkerItem("Андреевская улица", 55.421409, 86.136489));
        list.add(new MarkerItem("Улица Антипова", 55.428769, 86.133245));
        list.add(new MarkerItem("АО Токем", 55.362065, 86.057238));
        list.add(new MarkerItem("АРЗ", 55.309344, 86.018132));
        list.add(new MarkerItem("улица Асфальтная", 55.320903, 86.046524));
        list.add(new MarkerItem("Аэропорт", 55.281532, 86.116721));
        list.add(new MarkerItem("Банк Москвы", 55.355044, 86.074858));
        list.add(new MarkerItem("улица Баумана", 55.323375, 86.088151));
        list.add(new MarkerItem("улица Баха", 55.289636, 85.981486));
        list.add(new MarkerItem("Бачаты", 55.298898, 85.984567));
        list.add(new MarkerItem("Белозерная улица", 55.279542, 85.985311));
        list.add(new MarkerItem("Бийская улица", 55.333986, 86.091819));
        list.add(new MarkerItem("Больничный городок", 55.332678, 86.159109));
        list.add(new MarkerItem("Бор", 55.377460, 86.084990));
        list.add(new MarkerItem("Ботанический сад", 55.341860, 86.082454));
        list.add(new MarkerItem("Бульвар Строителей", 55.352571, 86.156971));
        list.add(new MarkerItem("улица Варшавская", 55.401850, 86.051379));
        list.add(new MarkerItem("ВГСЧ", 55.397771, 86.061109));
        list.add(new MarkerItem("улица Веры Волошиной", 55.311003, 86.086192));
        list.add(new MarkerItem("Веры Волошиной (ул. Радищева)", 55.314368, 86.081407));
        list.add(new MarkerItem("Весенняя улица", 55.350269, 86.077987));
        list.add(new MarkerItem("Винзавод", 55.287314, 85.976075));
        list.add(new MarkerItem("Водозабор", 55.354955, 86.134674));
        list.add(new MarkerItem("Водокачка", 55.306124, 86.037957));
        list.add(new MarkerItem("Водолей", 55.317491, 86.158882));
        list.add(new MarkerItem("Военкомат", 55.391208, 86.094388));
        list.add(new MarkerItem("Военная часть", 55.389605, 86.122590));
        list.add(new MarkerItem("улица Волгоградская", 55.349017, 86.147571));
        list.add(new MarkerItem("ВОС", 55.336932, 86.078970));
        list.add(new MarkerItem("Восточная проходная", 55.353068, 86.006003));
        list.add(new MarkerItem("Вторчермет", 55.314201, 85.970356));
        list.add(new MarkerItem("улица Гагарина", 55.339789, 86.134192));
        list.add(new MarkerItem("ГАЗС", 55.308699, 86.157814));
        list.add(new MarkerItem("ГАП-1", 55.430715, 86.102443));
        list.add(new MarkerItem("Гаражи", 55.341975, 86.192765));
        list.add(new MarkerItem("Гараж УВД", 55.317702, 86.100722));
        list.add(new MarkerItem("ГБК Кемерово", 55.350504, 86.166099));
        list.add(new MarkerItem("Геологоразведка", 55.331229, 86.086278));
        list.add(new MarkerItem("Гидроузел", 55.312111, 86.096976));
        list.add(new MarkerItem("Гипермаркет Алпи", 55.335707, 86.186507));
        list.add(new MarkerItem("Гипермаркет Поляна", 55.318136, 86.134244));
        list.add(new MarkerItem("Главпочтамт", 55.353598, 86.086099));
        list.add(new MarkerItem("Горняцкая улица", 55.504364, 86.095689));
        list.add(new MarkerItem("Городская ГИБДД", 55.348604, 86.111354));
        list.add(new MarkerItem("Гортопсбыт", 55.316231, 86.024572));
        list.add(new MarkerItem("Гостиный двор", 55.327457, 86.123593));
        list.add(new MarkerItem("Гостиный двор (ул. Свободы)", 55.329096, 86.123343));
        list.add(new MarkerItem("Греческая деревня", 55.511409, 86.107862));
        list.add(new MarkerItem("ГРП", 55.400412, 86.054414));
        list.add(new MarkerItem("Губернский рынок", 55.360288, 86.059634));
        list.add(new MarkerItem("улица Юрия Двужильного", 55.311110, 86.085918));
        list.add(new MarkerItem("Детская поликлиника", 55.396833, 86.113611));
        list.add(new MarkerItem("Детская поликлиника №15", 55.522691, 86.113142));
        list.add(new MarkerItem("Детская поликлиника №16", 55.359820, 86.164725));
        list.add(new MarkerItem("Детские ясли", 55.425460, 86.130857));
        list.add(new MarkerItem("Детский дом №2", 55.314588, 86.093164));
        list.add(new MarkerItem("Детский областной диспансер", 55.380736, 86.079604));
        list.add(new MarkerItem("Детский сад", 55.307474, 86.091490));
        list.add(new MarkerItem("Детский теннисный клуб", 55.403272, 86.037865));
        list.add(new MarkerItem("улица Джамбула", 55.389077, 86.030158));
        list.add(new MarkerItem("ДК Кировского района", 55.389700, 86.008696));
        list.add(new MarkerItem("ДК Шахтёров", 55.384998, 86.089219));
        list.add(new MarkerItem("Дом №13", 55.329038, 86.115448));
        list.add(new MarkerItem("Дом творчества молодёжи", 55.337291, 86.156370));
        list.add(new MarkerItem("Дорожная улица", 55.296625, 86.028370));
        list.add(new MarkerItem("Дорожный комбинат", 55.327553, 86.060507));
        list.add(new MarkerItem("ДОСААФ", 55.365504, 86.038084));
        list.add(new MarkerItem("ДП Бульвар Строителей", 55.347814, 86.164086));
        list.add(new MarkerItem("ДП Комсомольский", 55.337173, 86.194194));
        list.add(new MarkerItem("ДП Ленинградский", 55.344586, 86.178052));
        list.add(new MarkerItem("ДП Московский", 55.362355, 86.173244));
        list.add(new MarkerItem("ДП ФПК", 55.325468, 86.117209));
        list.add(new MarkerItem("ДП Центральный", 55.346193, 86.131598));
        list.add(new MarkerItem("ДП Шалготарьян", 55.358091, 86.176131));
        list.add(new MarkerItem("улица Дружбы", 55.309121, 86.097351));
        list.add(new MarkerItem("ДСК", 55.336563, 85.986117));
        list.add(new MarkerItem("Железнодорожная больница", 55.335760, 86.156690));
        list.add(new MarkerItem("Железнодорожный вокзал", 55.343454, 86.061517));
        list.add(new MarkerItem("Железнодорожный вокзал (пр. Ленина)", 55.344091, 86.063803));
        list.add(new MarkerItem("Железнодорожный переезд", 55.453446, 86.128621));
        list.add(new MarkerItem("Железнодорожная поликлиника", 55.337465, 86.068820));
        list.add(new MarkerItem("Железнодорожная улица", 55.337849, 86.024524));
        list.add(new MarkerItem("Журавлёвы Горы", 55.389468, 86.205948));
        list.add(new MarkerItem("ЖЭК", 55.424633, 86.124795));
        list.add(new MarkerItem("Заводская улица", 55.348825, 86.037595));
        list.add(new MarkerItem("Завод теплоизоляции Каверлит", 55.356857, 86.021268));
        list.add(new MarkerItem("Заречный переулок", 55.297719, 86.009054));
        list.add(new MarkerItem("Зелёный Остров", 55.333327, 86.211789));
        list.add(new MarkerItem("ЗЖБИ", 55.394095, 86.069370));
        list.add(new MarkerItem("ЗКПД", 55.339134, 85.987844));
        list.add(new MarkerItem("ЗМК", 55.328413, 85.952380));
        list.add(new MarkerItem("ЗХВ", 55.331164, 86.146989));
        list.add(new MarkerItem("Искитимский мост", 55.344320, 86.085745));
        list.add(new MarkerItem("ИТК №29", 55.416396, 86.040277));
        list.add(new MarkerItem("Ишанова", 55.324678, 85.976241));
        list.add(new MarkerItem("улица Ишимская", 55.382510, 86.062389));
        list.add(new MarkerItem("улица Каменская", 55.330016, 86.125339));
        list.add(new MarkerItem("Кардиоцентр", 55.388938, 86.124659));
        list.add(new MarkerItem("КГИК", 55.338926, 86.160839));
        list.add(new MarkerItem("КГМИ", 55.335925, 86.164544));
        list.add(new MarkerItem("Кедровая улица", 55.498887, 86.158362));
        list.add(new MarkerItem("Кедровская автобаза", 55.525116, 86.124165));
        list.add(new MarkerItem("КемГУ", 55.352095, 86.090876));
        list.add(new MarkerItem("Кемдор", 55.348150, 86.061809));
        list.add(new MarkerItem("Кемеровостройоптторг", 55.360641, 85.996143));
        list.add(new MarkerItem("Кемеровоторгтехника", 55.359152, 86.002196));
        list.add(new MarkerItem("Кемсоцинбанк", 55.405065, 86.121681));
        list.add(new MarkerItem("КемТИПП", 55.336341, 86.174033));
        list.add(new MarkerItem("улица Киевская", 55.336098, 86.088799));
        list.add(new MarkerItem("Кинотеатр Аврора", 55.351824, 86.158163));
        list.add(new MarkerItem("Кинотеатр Прогресс", 55.320486, 86.071606));
        list.add(new MarkerItem("Киноцентр Космос", 55.346376, 86.080330));
        list.add(new MarkerItem("Киноцентр Юбилейный", 55.344935, 86.126338));
        list.add(new MarkerItem("Кирзавод", 55.356513, 86.017410));
        list.add(new MarkerItem("Кирзаводская", 55.399727, 86.102780));
        list.add(new MarkerItem("Кирова улица", 55.358040, 86.074725));
        list.add(new MarkerItem("Кировская автобаза", 55.400518, 86.025495));
        list.add(new MarkerItem("Кладбище", 55.492721, 86.129414));
        list.add(new MarkerItem("Кладбище №2", 55.423322, 86.042821));
        list.add(new MarkerItem("Кладбище №3", 55.429381, 86.040744));
        list.add(new MarkerItem("Клуб шахты Северная", 55.418803, 86.122874));
        list.add(new MarkerItem("КОАО АЗОТ", 55.347865, 86.000239));
        list.add(new MarkerItem("КОАО Химпром", 55.365327, 86.022485));
        list.add(new MarkerItem("Колодец", 55.369162, 86.095529));
        list.add(new MarkerItem("Кольцо", 55.511375, 86.112141));
        list.add(new MarkerItem("Комсомольский парк", 55.339588, 86.124494));
        list.add(new MarkerItem("Корпорация АСИ", 55.340001, 85.988748));
        list.add(new MarkerItem("Космическая улица", 55.323230, 86.078893));
        list.add(new MarkerItem("Котельная", 55.315146, 86.175607));
        list.add(new MarkerItem("КПП", 55.312057, 85.974552));
        list.add(new MarkerItem("улица Крамского", 55.374585, 86.129437));
        list.add(new MarkerItem("Красноярская", 55.409577, 86.039160));
        list.add(new MarkerItem("Магазин Кристалл", 55.347478, 86.089744));
        list.add(new MarkerItem("Кузбассэнерго", 55.359484, 86.067498));
        list.add(new MarkerItem("КузГТУ", 55.348462, 86.081819));
        list.add(new MarkerItem("КузТАГиС", 55.318154, 86.087292));
        list.add(new MarkerItem("КШТ", 55.326634, 86.151354));
        list.add(new MarkerItem("КЭМЗ", 55.357208, 86.067545));
        list.add(new MarkerItem("Лесная Поляна", 55.422779, 86.231834));
        list.add(new MarkerItem("Лесной склад", 55.419679, 86.094378));
        list.add(new MarkerItem("Литейная улица", 55.339508, 86.114917));
        list.add(new MarkerItem("Линейная улица", 55.414180, 86.126351));
        list.add(new MarkerItem("Лыжная база", 55.370311, 86.085072));
        list.add(new MarkerItem("Магазин №11", 55.339357, 86.171388));
        list.add(new MarkerItem("Магазин №12", 55.343368, 86.157725));
        list.add(new MarkerItem("Магазин №73", 55.339498, 86.103429));
        list.add(new MarkerItem("Магазин №8", 55.351267, 86.042103));
        list.add(new MarkerItem("Магазин Берёзка", 55.393187, 86.017142));
        list.add(new MarkerItem("Магазин Богатырь", 55.345309, 86.164151));
        list.add(new MarkerItem("Магазин НЭТА", 55.348664, 86.142932));
        list.add(new MarkerItem("Магазин Товары для дома", 55.343772, 86.177118));
        list.add(new MarkerItem("Магазин Чибис", 55.326213, 86.119657));
        list.add(new MarkerItem("Магазин Юный Техник", 55.342930, 86.149766));
        list.add(new MarkerItem("Мариинская улица", 55.383187, 86.051064));
        list.add(new MarkerItem("улица Мартемьянова", 55.339155, 86.055970));
        list.add(new MarkerItem("Мебельная фабрика", 55.339228, 86.062427));
        list.add(new MarkerItem("Мебельный магазин", 55.351819, 86.155189));
        list.add(new MarkerItem("МегаФон", 55.354447, 86.065259));
        list.add(new MarkerItem("Медицинский институт", 55.391608, 85.999900));
        list.add(new MarkerItem("Меливодстрой", 55.309677, 86.029517));
        list.add(new MarkerItem("Мехзавод", 55.366295, 86.032957));
        list.add(new MarkerItem("МЖК", 55.400615, 86.117238));
        list.add(new MarkerItem("улица Мичурина", 55.335749, 86.075930));
        list.add(new MarkerItem("Мост", 55.327672, 86.070438));
        list.add(new MarkerItem("Музей-заповедник Красная Горка", 55.375508, 86.078556));
        list.add(new MarkerItem("Мясокомбинат", 55.314418, 86.036937));
        list.add(new MarkerItem("Набережная", 55.372418, 86.079537));
        list.add(new MarkerItem("Насыпь", 55.328128, 86.058285));
        list.add(new MarkerItem("Нефтебаза", 55.324277, 85.968188));
        list.add(new MarkerItem("Никольский собор", 55.382657, 86.040552));
        list.add(new MarkerItem("Новая улица", 55.325642, 86.038738));
        list.add(new MarkerItem("Универсам Ноград", 55.356873, 86.171243));
        list.add(new MarkerItem("совхоз Суховский", 55.320180, 86.187378));
        list.add(new MarkerItem("Областная больница", 55.348615, 86.118245));
        list.add(new MarkerItem("Облсельхозтехника", 55.312109, 86.014324));
        list.add(new MarkerItem("ОВО УВД", 55.335753, 86.150178));
        list.add(new MarkerItem("Онкология", 55.340290, 86.152516));
        list.add(new MarkerItem("ООО Тетрис", 55.319666, 86.171423));
        list.add(new MarkerItem("Оптовый рынок", 55.358038, 86.041994));
        list.add(new MarkerItem("улица Островского", 55.362558, 86.070850));
        list.add(new MarkerItem("ПАЗ-сервис УЕЗТУ", 55.296004, 85.997941));
        list.add(new MarkerItem("ПАТП-1", 55.386035, 86.093791));
        list.add(new MarkerItem("ПАТП-3", 55.324428, 86.166734));
        list.add(new MarkerItem("ПДУ", 55.301841, 86.033327));
        list.add(new MarkerItem("Переезд", 55.334378, 86.012466));
        list.add(new MarkerItem("ПЖРО", 55.394905, 86.099418));
        list.add(new MarkerItem("ПЗАБ", 55.362061, 86.006930));
        list.add(new MarkerItem("Пивзавод", 55.311145, 86.137651));
        list.add(new MarkerItem("Пищевой техникум", 55.339389, 86.139018));
        list.add(new MarkerItem("Плодопитомник", 55.322248, 86.129290));
        list.add(new MarkerItem("Плодопитомник №2", 55.312768, 86.115958));
        list.add(new MarkerItem("Площадь Волкова", 55.348156, 86.076493));
        list.add(new MarkerItem("Поворот (Аэропорт)", 55.331312, 86.048897));
        list.add(new MarkerItem("Подгорная", 55.315613, 86.048926));
        list.add(new MarkerItem("Подстанция", 55.413404, 86.071184));
        list.add(new MarkerItem("Поликлиника №12", 55.336574, 86.185196));
        list.add(new MarkerItem("Поликлиника №5", 55.345067, 86.139427));
        list.add(new MarkerItem("ПО Прогресс", 55.396420, 85.986460));
        list.add(new MarkerItem("Поселок Пионер", 55.323139, 85.951154));
        list.add(new MarkerItem("Пост ГИБДД", 55.437441, 86.139165));
        list.add(new MarkerItem("Почта (ул. Халтурина)", 55.411031, 86.052223));
        list.add(new MarkerItem("Привоз", 55.319711, 86.103920));
        list.add(new MarkerItem("ТРК Променад-2", 55.343411, 86.178518));
        list.add(new MarkerItem("Промсвязьбанк", 55.358856, 86.069673));
        list.add(new MarkerItem("ПУВКХ", 55.328727, 86.163095));
        list.add(new MarkerItem("Пчелобаза", 55.313466, 86.009085));
        list.add(new MarkerItem("Радужная улица", 55.505296, 86.090849));
        list.add(new MarkerItem("Редакция газеты Кузбасс", 55.348742, 86.125983));
        list.add(new MarkerItem("РМЗ", 55.343815, 85.993775));
        list.add(new MarkerItem("РТС", 55.306555, 86.025187));
        list.add(new MarkerItem("Рудничная автобаза", 55.316957, 85.953687));
        list.add(new MarkerItem("улица Рутгерса", 55.378211, 86.075841));
        list.add(new MarkerItem("Рынок Дружба", 55.311560, 86.143891));
        list.add(new MarkerItem("Рябиновая улица", 55.508401, 86.098523));
        list.add(new MarkerItem("Садовое общество Южное", 55.293104, 86.090537));
        list.add(new MarkerItem("Сады", 55.320691, 86.109535));
        list.add(new MarkerItem("Санаторий Журавлик", 55.385652, 86.117809));
        list.add(new MarkerItem("Сбербанк", 55.348792, 86.174012));
        list.add(new MarkerItem("улица Свободы", 55.327287, 86.110359));
        list.add(new MarkerItem("СГПТУ №49", 55.315882, 86.084303));
        list.add(new MarkerItem("Сельскохозяйственный институт", 55.334101, 86.176689));
        list.add(new MarkerItem("Сельхозтехника", 55.310577, 86.018377));
        list.add(new MarkerItem("улица Сибиряков-Гвардейцев", 55.336555, 86.142684));
        list.add(new MarkerItem("Силино", 55.269478, 86.312680));
        list.add(new MarkerItem("СКБ Банк", 55.346752, 86.153668));
        list.add(new MarkerItem("СКК Октябрьский", 55.349332, 86.136115));
        list.add(new MarkerItem("СКК Олимпийский", 55.507231, 86.113925));
        list.add(new MarkerItem("Служба спасения", 55.354102, 86.060175));
        list.add(new MarkerItem("Собор", 55.338871, 86.097311));
        list.add(new MarkerItem("Совхозная улица", 55.350828, 86.050611));
        list.add(new MarkerItem("Сортировочная", 55.323223, 86.035502));
        list.add(new MarkerItem("Сосновый бор", 55.384374, 86.233897));
        list.add(new MarkerItem("Спецшкола", 55.480253, 85.956074));
        list.add(new MarkerItem("Стадион Химик", 55.355902, 86.074629));
        list.add(new MarkerItem("Стадион Шахтёр", 55.382795, 86.082509));
        list.add(new MarkerItem("Стоматологическая клиника Улыбка", 55.344785, 86.107451));
        list.add(new MarkerItem("Стоматология (пр. Ленинградский)", 55.339764, 86.182427));
        list.add(new MarkerItem("Стоматология (ул. Инициативная)", 55.395301, 86.006805));
        list.add(new MarkerItem("Стройгородок", 55.413119, 86.040985));
        list.add(new MarkerItem("Стройкомплект", 55.318502, 86.043016));
        list.add(new MarkerItem("Строммаш", 55.401599, 86.041979));
        list.add(new MarkerItem("улица Суворова", 55.380044, 86.073528));
        list.add(new MarkerItem("Театр Драмы", 55.356065, 86.081887));
        list.add(new MarkerItem("Театр Оперетты", 55.355693, 86.085065));
        list.add(new MarkerItem("Телестудия", 55.505776, 86.105017));
        list.add(new MarkerItem("Терешковой", 55.348211, 86.135713));
        list.add(new MarkerItem("Технопарк", 55.387926, 86.120432));
        list.add(new MarkerItem("Торгово-экономический университет", 55.351730, 86.063745));
        list.add(new MarkerItem("Трамвайное кольцо", 55.392628, 85.993340));
        list.add(new MarkerItem("Трамвайный парк", 55.364268, 86.043667));
        list.add(new MarkerItem("Транссибстрой", 55.342486, 85.981135));
        list.add(new MarkerItem("Троллейбусное депо", 55.333688, 86.143389));
        list.add(new MarkerItem("ТУ Лесная Поляна", 55.415727, 86.232714));
        list.add(new MarkerItem("ТУЛР", 55.342076, 86.167114));
        list.add(new MarkerItem("ТУРР", 55.392893, 86.110453));
        list.add(new MarkerItem("ТУЦР", 55.352383, 86.068498));
        list.add(new MarkerItem("ТЦ Архимед", 55.330998, 86.132047));
        list.add(new MarkerItem("ТЦ Аустроник", 55.351182, 86.171314));
        list.add(new MarkerItem("ТЭЦ", 55.350419, 86.005086));
        list.add(new MarkerItem("Угловая улица", 55.346478, 86.048667));
        list.add(new MarkerItem("УН 1612/40", 55.321347, 86.169876));
        list.add(new MarkerItem("Универмаг", 55.344601, 86.121810));
        list.add(new MarkerItem("Универсам №2", 55.345132, 86.147214));
        list.add(new MarkerItem("Универсам №3", 55.354785, 86.165072));
        list.add(new MarkerItem("Управление ГИБДД Кемеровской области", 55.320438, 86.092363));
        list.add(new MarkerItem("Управление железной дороги", 55.352047, 86.106938));
        list.add(new MarkerItem("Уралсиббанк", 55.348839, 86.101744));
        list.add(new MarkerItem("Училище №77", 55.417028, 86.124781));
        list.add(new MarkerItem("Фабрика Подорожник", 55.296949, 86.003405));
        list.add(new MarkerItem("Фабричная", 55.344861, 86.114008));
        list.add(new MarkerItem("Филармония", 55.353245, 86.091848));
        list.add(new MarkerItem("Фирма Цимус", 55.325125, 86.132196));
        list.add(new MarkerItem("ФПК", 55.330312, 86.132746));
        list.add(new MarkerItem("Химмаш", 55.321737, 86.155798));
        list.add(new MarkerItem("Химстрой", 55.339636, 85.956109));
        list.add(new MarkerItem("Хлебозавод", 55.333459, 86.061961));
        list.add(new MarkerItem("Хозяйственный магазин", 55.382821, 86.073511));
        list.add(new MarkerItem("Храм Святой Троицы", 55.345457, 86.182687));
        list.add(new MarkerItem("Художественное училище", 55.387505, 86.101621));
        list.add(new MarkerItem("ЦГДКБ", 55.333390, 86.167175));
        list.add(new MarkerItem("Центр реабилитации", 55.330917, 86.160891));
        list.add(new MarkerItem("Церковь преподобного Сергия Радонежского", 55.298432, 86.016642));
        list.add(new MarkerItem("Церковь Утоли моя печали", 55.312568, 86.091238));
        list.add(new MarkerItem("Цирк", 55.344765, 86.098891));
        list.add(new MarkerItem("ЦЭММ", 55.388994, 86.068707));
        list.add(new MarkerItem("улица Шатурская", 55.357421, 86.005358));
        list.add(new MarkerItem("Швейная фабрика", 55.344248, 86.076316));
        list.add(new MarkerItem("Школа №17", 55.339266, 86.071193));
        list.add(new MarkerItem("Школа №24", 55.394467, 86.111421));
        list.add(new MarkerItem("Школа №46", 55.405947, 86.109750));
        list.add(new MarkerItem("Школа №60", 55.366962, 86.126680));
        list.add(new MarkerItem("Энергосбыт", 55.344695, 86.132318));
        list.add(new MarkerItem("Юбилейная улица", 55.444525, 86.071862));
        list.add(new MarkerItem("Южная проходная", 55.342528, 85.971467));


        return list;
    }


    static void setRating(SharedPreferences pref, final SimpleRatingBar bar) {
        final float[] rating = new float[1];

        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        final ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_RATING_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {
                    rating[0] = resp.getUser().getRating();
                    bar.setRating(rating[0]);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(bar, Constants.NO_NETWORK, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private static int countUnChar(String str) {
        int count = 0;
        char[] lol = str.toCharArray();

        for (int i = 0; i < lol.length; i++) {
            boolean appears = false;
            for (int j = 0; j < i; j++) {
                if (lol[j] == lol[i]) {
                    appears = true;
                    break;
                }
            }

            if (!appears) {
                count++;
            }
        }
        return count;
    }
}
