/**
 * Класс определения патерна Observer с проведением теста
 * 
 */
package patterns;

import java.util.*;
import java.io.*;
/**
 * Паттерн Наблюдатель
 * 
 * @author delverOne25
 */
public class ObserverManager {
    
    public static class Observer{
        /** *  <tt>listeners</tt>  Список обьектов ксобытия которых отслеживает ObserverManager  */  
        Map<String, List<EventListener>> listeners  =new HashMap<>();
        /**
         * Конструктор.
         * <p>Добавляет Список операций для каждой из которых будет свой список Слушателей</p>
         * @param operations - Массив операций
         */
        public Observer(String ...operations){
            for(String operation : operations){
                this.listeners.put(operation,new ArrayList<EventListener>());
            }
        }
        /**
         * Добавляет нового подписчика на определенное событие.
         * <p>В случае если <tt>eventType</tt> не зарегестрирован, то генерирует исключение 
         * @param eventType  - тип события
         * @param listener   - подписичик реализовавший интерфейс EventListener
         * @throws NullPointerException  если <tt>users</tt> равен <i>null</i>
         */
        public void subscribe(String eventType, EventListener listener) throws NullPointerException{
            List<EventListener> users = listeners.get(eventType);
            users.add(listener);
        }
            /**
         * Удаляет подписчика на определенное событие.
         * <p>В случае если <eventType> не зарегестрирован, то генерирует исключение 
         * @param eventType  - тип события
         * @param listener   - подписичик реализовавший интерфейс EventListener
         * @throws NullPointerException  если <users> равен <null>
         */
        public void unscribe(String eventType, EventListener listener) throws NullPointerException{
            List<EventListener> users =listeners.get(eventType);
            int index =users.indexOf(listener);
            users.remove(index);
        }
        /**
         * Уведомляет всех подписчиков о случившимся событии
         * @param EventType - тип события
         * @param file   - файл в котором произошло изменение
         */
        public void notify(String EventType, File file){
            List<EventListener> users = listeners.get(EventType);
            for(EventListener listener : users){
                listener.update(EventType, file);
            }
        }
    }
    
    // Издатель события. Изменения которого будут отслеживать наблюдатели
    public static class Editor{
        public Observer observer;
        private File mfile;
        
        public Editor(){
            this.observer = new Observer("open","save","close");
        }
        // Оповещает слушателей о открытии файла
        public void openFile(String path){
            this.mfile=new File(path);
            observer.notify("open",mfile);
        }
        /**
         * Оповещает слушателей о изменении в файле
         * @throws Exception 
         */
        public void saveFile() throws Exception{
            if(this.mfile!=null)
                observer.notify("save", mfile);
            else
                throw new Exception("Пожалуйста откройте файл");
        }
  
    }
    /**
     * Интерфейс {@code EventListener} будут реализовывать подписчики.
     * 
     */
    public interface EventListener{
       /**
        * Регистрирует подписчика для события <tt>eventType</tt>
        * При изменении работы с <tt>file</tt>
        * <p>Метод {@code update} вызывает метод {@code notify} Наблюдателя
        *  обьекта класса {@link ObserverManager}</i>
        * Сам же событие генерирует {@link Editor} 
        * @param eventType
        * @param file 
        */
        public void update(String eventType, File file);
    }
    
    //---------------------Определим Слушателей подписчиков на событие----------
    public  static class EmailNotifycationListener implements EventListener{
        private final String email;
        public EmailNotifycationListener(String mail){
            email=mail;
        }
        /**
         * Реализация интерфейсного метода {@code update}.
         * <p>Cообщает всем подписчикам для события <tt>eventType</tt> о изменении.
         * @param eventType
         * @param file 
         */
        @Override
        public void update(String eventType, File file){
            System.out.println("Email to "+email+": Someone has performed "+eventType
            +"  Operation with the following file: "+file.getName());
        }
    }
    
    
    public static class LogOpenListener implements EventListener{
        private final String mlog;
         
        
        public LogOpenListener(String log){
            mlog=log;
        }
        @Override
        public void update(String eventType, File file){
            System.out.println("Save to log "+mlog+": Someone has performed "+eventType
                    +" Operation with the following file: "+file.getName());
        }
    }
    
    static class Demo{
        public static void main(String[] args){
            Editor editor=new Editor();
            // добавляем нового слушателя для события open и save
            try{
                editor.observer.subscribe("open", new LogOpenListener("/src/patterns/Patterns.java"));
                editor.observer.subscribe("save", new EmailNotifycationListener("admin@example.com"));
            }catch(NullPointerException ex){
                System.err.println("Вы пытаетесь добавить слушателя на не зарегестрированное событие");
            }
            /// теперь произведем осноыные опрации 
            try{
                editor.openFile("Patterns.java");
                editor.saveFile();
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }
    
}
