/*
 * Программа пример паттерна наблюдатель.
 * Используем систему оповещения подписанных клиентов(акционеров) на изменения курсов валют
 * Любое изменение валюты должно оповестить всех акционеров оторые на него подписанны
 * Сам проект можно разбить на одтдельные файлы классов удалив с них модификатор static
 * для удобного восприятия.
*/
package patterns;

import java.util.Currency;
import java.util.*;
/**
 *Структура проекта. 
 *  ObserverExample
 *    -> $Coordinator  - класс Наблюдатель, добавляет подписчиков для оповещения о изменении курса валют
 *    -> $Exchange     - класс Издатель, реализует основные операции с изменением курса валюты, содержит переменную Coordinator
 *                                          которая следит за изменением курса валют.
 *    -> $Corporation и $Realty реализует интерфейс Listener  - Классы подписчиков, которые следят за изменением Валюты и тут же 
 *                           оповещают владельца о изменении цены на товар.Товаром будут акции и обьекты крупого бизнесса
 *    -> $Demo    - демо взаимодействия классов. Создаются подписчики, и добавляются для отслеживания изменений, так же 
 *                           выводится история изменений и график динамики роста изменения цены
 * @author delverOne25
 */
public class ObserverExample {

    
    /**
     * Класс {@code Coordinator} предназначет за слежением за курсом валюты и при 
     * изменении ее, он немедленно  сообщает всем клиентам ее новое значение
     * <p>
     */
    public static class Coordinator{
        Map<Currency,ArrayList<Listener>> listeners;
        public Coordinator(){  
            Comparator<Currency> ccomp= new ListenerComparator();
                 listeners=new TreeMap<>(ccomp);
                 listeners.put(Currency.getInstance("USD"), new ArrayList<>());
       }
        /**
         * Добавляет новую валюту для отслеживания ее изменения и сообщения клиентам курс
         * @param ISO - значение в формате строки iso-4217
         */
        public void addCurrency(String ISO){
            listeners.put(Currency.getInstance(ISO),new ArrayList<>());
        }
        /** добавляем нового подписчика для оповещения о изменении валюты
         * @param iso - строка с названием валюты
         * @param listener - подписчик для получения извещения о изменении цены валюты
         */
        public void subscribe(String iso, Listener listener){
            ArrayList<Listener> users = listeners.get(Currency.getInstance(iso));
            // если мы еще не отслеживаем изменение этой валюты, то добавим ее
             users.add(listener);   
        }
        public void subscribe(Listener listener){
            this.subscribe(listener.getISO(),listener);
        }
        // удаляем подписчика
        public void unsubscribe(String iso,Listener listener){
            List<Listener> users =listeners.get(Currency.getInstance(iso));
            int index =users.indexOf(listener);
            users.remove(index);
        }
        /**
         * 
         * Основной метод отправляющий извещение всем подписчикам для валюты <b>iso</b>.
         * @param iso - строка с названием валюты
         * @param newValue - новое значение этой денежной еденицы, сообщить всем ее подписчикам
         */
        public void notify(String iso, float newValue){
            List<Listener> users =listeners.get(Currency.getInstance(iso));
            for (Listener user : users) {
                // сообщим всем клиентам о изменении валюты
                user.report(newValue);
            }
        }
    }
    
    
    public static  class ListenerComparator implements Comparator<Currency>{
        @Override
        public int compare(Currency l1, Currency l2){
            return l1.getDisplayName().compareTo(l2.getDisplayName());
        }
    }

    /**
     *Класс Биржа будет нашим координатором, которая сожержит список всех валют
     *  и их существующий курс.
     *Биржа умеет добавлять валюты, определять их начальную цену и изменять их.
     *<p>При изменении курса валюты вызывает метод {@link moveCourse}, который в свою очередь
     *  вызывает {@code  notify()} обьекта {@code coordinator}, который оповещает слушателей
     *<p>Методы {@link getDinamicChange} и {@link getAnaliticDif} возращают историю изменения курса валют
     *  и динамику изменения в виде массива float[].
     */
    public static class Exchange{// список с значанием для каждой валюты/
        // список с значанием для каждой валюты
        static Map<String, Float>  exchangeRates=new TreeMap<>();
        static Map<String,ArrayList<Float>>historyCourseMony=new TreeMap();
        // инициализируем начальные значения для курс валют в рублях относительно долара
        // а так же инициализируем таблицу с историей изменения курса валют
        static{
            String names[]=new String[]{
                "RUB","USD","CZK","EUR"
            };
            float values[] =new float[]{
                57.5f, 1.0f , 13.0f , 0.8f
            };
            for(int i=0; i<names.length;i++){
                 exchangeRates.put(names[i], new Float(values[i]));
                 historyCourseMony.put(names[i],new ArrayList());
                 historyCourseMony.get(names[i]).add(values[i]);
            }
        }
        public Coordinator coordinator; // это Наблюдатель, который будет сообщать подпичикам о изменнии курса валюты
        private final String basicISO;        //  базовая система валют, отностельно которой будут изменяться другие.
        public Exchange(){
            this("USD", -1);
        }        
        public Exchange(String basicISO){
            this(basicISO, -1);
        }
        /**
         * Конструктор. Обьявлят Наблюдателя {@link Coordinator}. Добовляет валюты в список его отсеживаний.
         * <p>В случае если была переданна новая валюта, отличная от <ii>USD<ii>.То происходит пересчет стандартно установленных
         * Валют, курс переданной валюты становиться <b>1.0f</b>.Остальнные же изменяются относительно него.
         * @param basicISO      устанавливает {@link basicISO} базовую валюту, относительно которой будут изменяться другие
         * @param ParambasicVal базовый курс новой валюту, относительно которой будут пересчитаны все другие
         */
        public Exchange(String basicISO,float ParambasicVal){
            this.basicISO=basicISO;
            if(!basicISO.equals("USD")){
                float basicValue=(float)exchangeRates.get(basicISO);
                // проверим если мы не нашли среди базовых валюту, то проверим была ли она передана в 2-ом параметре
                if(basicValue==0)
                    if(ParambasicVal>0)
                    {
                        basicValue=ParambasicVal;
                        addCurrency(basicISO, basicValue);
                    }
                // пройдемся по всем валютам и пересчетаем их базовые велечины относитнльно новой
                for(Map.Entry<String,Float> it : exchangeRates.entrySet()){
                    String k=it.getKey();
                    float v=(float)it.getValue();
                     if(k.equals(basicISO))
                        v=1.0f;
                    else
                        v=v/basicValue;
                     // так же изменим историю последних значений historyCourseMony
                     historyCourseMony.get(k).set(0,v);
                }
            }
            
            coordinator=new Coordinator();
            // добавим перевоначальный набор отслеживаемых валют
            for(String iso : exchangeRates.keySet()){
                coordinator.addCurrency(iso);
            }
            
        }
        
        /**
         * Добавим новую валюту в список отслеживаемых валют
         * @param iso
         * @param value 
         */
        public void addCurrency(String iso,float value){
            Float course = new Float(value);
            exchangeRates.put(iso, course );
            coordinator.addCurrency(iso);
        }
        /**
         * Изменяет курс переданной в параметре валюты
         * Сообщим клиентам о изменнение курса валюты
         * Сохраним новое значение действущего курса и добавим в историю для аналитики
         * <p>Если переданное значение валюты, является базовой валюты, от которой зависят другие
         * то изменим все зависимые от нею, само же значение базовой валюты должно быть <b>1.0f</b>
         * @param iso       - название валюты
         * @param newValue  - новое значение курса валют
         */
        public void moveCourse(String iso,float newValue){
            float oldValue=exchangeRates.replace(iso, newValue);
            if(iso.equals(basicISO)){
                for(Map.Entry<String,Float> it : exchangeRates.entrySet()){
                    String k=it.getKey();
                    float v =(float)it.getValue();
                    if(!k.equals(basicISO)){
                       it.setValue(v/newValue);
                       // добавим в ситорию новые значения для валют
                       ArrayList<Float> values =historyCourseMony.get(k);
                       values.add(v/newValue);
                    }
                    coordinator.notify(k, v/newValue);
                }
            } else {
                ArrayList<Float> values =historyCourseMony.get(iso);
                values.add(newValue);
                coordinator.notify(iso, newValue);
            }
        }
        // Возвращает историю иземенения курса валют
        public static float[] getDinamicChange(String iso){
         ArrayList<Float> valuesCurrency = historyCourseMony.get(iso);
         float[] result = new float[ valuesCurrency.size()];
         int i=0;
         for(Float it : valuesCurrency)
             result[i++]=(float)it;
            return result;
        }
        // возращает динамику изменения валюты
        public static float[] getAnaliticDif(String iso){
            float[] values =getDinamicChange(iso);
            // oldVl значение i-1(пребедущего) элемента
            float oldVal=values[0];
            // будет содержать коэффициэны изменения валюты
            float diffVal[]=new float[values.length-1];
            for(int i=1; i<values.length;i++){
                diffVal[i-1]=1/((values[i])/oldVal);
                oldVal=values[i];
            }
            return diffVal;
        }
        
    }
    
     /**
     * Interface {@code Listener} определяет метод {@code report} 
     *  который вызывается при изменении курса валюты.
     * <p> Реализация методом будет обработчиком события. 
     *<p>Метод {@link getISO} должен возвращать строку название установленной валюты 
     */
    public interface Listener {
          public void report(float newValue);
          public String getISO();
    }
    // Демонстрационный класс Магазина-Подписчика который содержит товар и его цену
    public static class Corporation implements Listener{
        private Map<String,Float>products;      // товары
        private String iso;          // строка с названнием валюты
        private final float constantExchangeRates; // первоначальная стоимость валюты
        private float exchangeRates; // действующий курс валюты
        private final String name;
        /**
         * Инициализируем курсовую систему.
         * Инициализируем список товаров
         * @param iso             -  название локальной валюты для этого магазина
         * @param priceCurrency  *-- неизменный кначальный курс валюты, действущая цена товара определяется,относительно ее
         * как <ii>exchangeRates/constantExchangeRates</ii>
         */
        public  Corporation(String name, String iso, float priceCurrency){
            this.name=name;
            constantExchangeRates=priceCurrency;
            this.iso=iso;
            exchangeRates=priceCurrency;
            products=new TreeMap<>();
            
        }
        /**
         * Добовляет новый продукт.
         * @param name  название продукта
         * @param price  цена продукта в установленной валюты
         */
        public void addProduct(String name, float price){
            products.put(name, price);
        }
        /**
         * Функция добавления продукта в магазин. Если нету продукта с таким именем
         * Создает новый
         * @param product название продукта
         * @param price  цена продукта в установленной валюты
         */
        public void setProductPrice(String product, float price){
            
            Float oldPrice = products.replace(product,price);
            if(oldPrice==null)
                addProduct(product,price);
            
            
        }
        /**
         * Возращаем цену товара относительно курса  <tt>iso</tt>
         * @param product название товара
         * @return 
         */
        public float getProductPrice(String product){
            return (float)products.get(product)*exchangeRates/constantExchangeRates;
        }
        
        
        @Override
        public void report(float newValue) {
            System.out.println("");
            exchangeRates=newValue;
            System.out.println(name+" получил"+(name.charAt(name.length()-1)=='о'? "o":"")+" оповещение.");
            products.forEach((k,v)->System.out.print("Стоимость "+k+" : "+getProductPrice(k)+"  |  "));
             System.out.println("");
        }

        @Override
        public String getISO() {
           return iso;
        }
                
    }
    /**
     * Класс недвижимости для продажи.
     * Реализирует интерфейс {@link Listener} 
     * Определяет стоимость обьекта,  выводит его стоимость при изменнеии курса валют
     */
    static class Realty implements Listener{
        private String name;
        private float  exchangeRates; // курс валюты
        private final String iso;
        private float price;
        
        public Realty(String name, String iso,float price,float course){
            this.iso=iso;
            this.name=name;
            this.price=price;
            exchangeRates=course;
        }
        /**
         * Возвращает цену товара относительно изменившивося курса валют
         * @param val 
         */
        public void info(float val){
            System.out.println(name+" " +"стоит : "+price*val/exchangeRates);
        }
        
        @Override
        public void report(float newValue) {
          info(newValue);
          price=price*newValue/exchangeRates;
          exchangeRates=newValue;
          
        }

        @Override
        public String getISO() {
            return iso;
        }
        
        
    }
    /**
     * Демонстрируем работу паттерна наблюдатель.
     * <p>Определяем биржу, добовляем в биржу обьекты магазины валют, чьи услуги зависят от курса
     * И пробуем изменить курс валют и получить цены на услуги с учетом курса
     * short2 shop3 будут следить за изменением EUR
     */
    private static class Demo{
        public static void main(String[] args){
            
            Exchange exchange =new Exchange();
            Corporation shop1 =new Corporation("Акционерное сообщество","RUB", Exchange.exchangeRates.get("RUB"));
            shop1.addProduct("Акции Газпром", 2000);
            shop1.addProduct("Акции Магнит", 5000);
            Corporation shop2 =new Corporation("Газпром","EUR", Exchange.exchangeRates.get("EUR"));
            shop2.addProduct("Цена на нефть Газпром", 1500);
            shop2.addProduct("Цена на газ Гащпром", 40);
            Corporation shop3 =new Corporation("Акционер Билли","EUR", Exchange.exchangeRates.get("EUR"));
            shop3.addProduct("Цена на нефть Лукойл", 1200);
            shop3.addProduct("Цена на газ Лукойл", 30);
            Realty realty =new Realty("Дворец Березовского","EUR",2500000.0f, Exchange.exchangeRates.get("EUR"));
            exchange.coordinator.subscribe(shop1);
            exchange.coordinator.subscribe(shop2);
            exchange.coordinator.subscribe(shop3);
            exchange.coordinator.subscribe(realty);
            exchange.moveCourse("RUB", 50);
            exchange.moveCourse("EUR", 0.9f);
            exchange.moveCourse("RUB", 55);
            exchange.moveCourse("EUR", 2.0f);
            exchange.moveCourse("RUB", 50);
            exchange.moveCourse("USD", 1.3f);
            exchange.moveCourse("RUB", 60);
            final float fiffs[] =Exchange.getAnaliticDif("RUB");
            final float values[]=Exchange.getDinamicChange("RUB");
            System.out.println("RUB DINAMIC HISTORY");
            for(int i=0;i<fiffs.length;i++)
                 System.out.println("Value #"+i+" diff : "+fiffs[i]);
             System.out.println("RUB VALUES HISTORY");
                        for(int i=0;i<values.length;i++)
                 System.out.println("Value #"+i+" val : "+values[i]);
        }
    }
}
