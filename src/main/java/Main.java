
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Main {
    public static void main(String[] args) throws IOException {
        List<Visitor> visitors = new ArrayList<>(loadVisitors("books.json"));
        //System.out.println(visitors);
        firstTask(visitors);
        secondTask(visitors);
        thirdTask(visitors);

        fourthTask(visitors);
        fifthTask(visitors);
        sixTask(visitors);



    }
    //Задание 1
    public static void firstTask (List<Visitor> visitors){
        System.out.println("Задание 1");
        Stream<Visitor> visitorStream1 = visitors.stream();

        System.out.println("Количество посетителей: "+visitorStream1.count());
        System.out.println("Список посетителей: ");
        visitors.forEach(visitor -> System.out.println(visitor.getName() + " " + visitor.getSurname()));
    }

    //Задание 2
    public static void secondTask (List<Visitor> visitors){
        System.out.println("\nЗадание 2");
        List<String> books = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .map(book -> book.getName())
                .distinct()
                .collect(Collectors.toList());

        System.out.println("Список уникальных книг:");
        books.forEach(book -> System.out.println(book));
        System.out.println("Количество уникальных книг: " + books.size());
    }
    //Задание 3
    public static void thirdTask (List<Visitor> visitors){
        System.out.println("\nЗадание 3");
        List<String> booksByYear = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .sorted(new BookPublicationDateComparator())
                .map(book -> book.getName())
                .distinct()
                .collect(Collectors.toList());

        booksByYear.forEach(book -> System.out.println(book));
    }

    //Задание 4
    public static void fourthTask (List<Visitor> visitors){
        System.out.println("\nЗадание 4");
        boolean janeAustin = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .anyMatch(book -> book.getAuthor().equals("Jane Austen"));
        System.out.println(janeAustin);
    }

    //Задание 5
    public static void fifthTask (List<Visitor> visitors){
        System.out.println("\nЗадание 5");
        int maxAddBooks = visitors.stream()
                .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println(maxAddBooks);
    }

    //Задание 6
    public static void sixTask (List<Visitor> visitors){
        System.out.println("\nЗадание 6");
        double avr = visitors.stream()
                .mapToInt(visitor1 -> visitor1.getFavoriteBooks().size())
                .average()
                .orElse(0);

        Map<smsMessage, List<Visitor>> visitorsBySub=visitors.stream()
               .filter(visitor -> visitor.isSubscribed())
               .collect(Collectors.groupingBy(visitor ->
                       {
                           double countFavBooks = visitor.getFavoriteBooks().size();
                           if(countFavBooks>avr) return new smsMessage(visitor.getPhone() ,"you are a bookworm");
                           if(countFavBooks<avr) return new smsMessage(visitor.getPhone() ,"read more") ;
                           else return new smsMessage(visitor.getPhone() ,"fine");
                       }
                       ));
        for(Map.Entry<smsMessage, List<Visitor>> i : visitorsBySub.entrySet()){
            System.out.println(i.getKey().getMessage());
        }

    }


    public static List<Visitor> loadVisitors(String filePath) throws IOException {

        try (FileReader reader =new FileReader(filePath)) {
            Gson gson = new Gson();
            if (!reader.ready()) {
                throw new RuntimeException("Файл не найден");
            }
            Type type = new TypeToken<List<Visitor>>() {
            }.getType();
//                Здесь мы создаем тип, который соответствует списку объектов Visitor. TypeToken позволяет нам сохранить информацию о типе во время выполнения,
//                что необходимо, так как Java использует обобщения (generics), и эта информация теряется при компиляции.
            List<Visitor> visitors = gson.fromJson(reader, type);
            return visitors;
        }
    }
}



@Data
class Visitor{
    private String name;
    private String surname;
    private String phone;
    private boolean subscribed;
    private List<Book> favoriteBooks;
}

@Data
class Book {
    private String name;
    private String author;
    private int publishingYear;
    private String isbn;
    private String publisher;
}


class BookPublicationDateComparator implements Comparator<Book> {
    @Override
    public int compare(Book book1, Book book2) {
        if (book1.getPublishingYear() == 0 && book2.getPublishingYear() == 0) {
            return 0; // Оба года равны 0, книги считаются одинаковыми
        }

        if (book1.getPublishingYear() == 0) {
            return 1; // Первая книга имеет неизвестный год публикации
        }

        if (book2.getPublishingYear() == 0) {
            return -1; // Вторая книга имеет неизвестный год публикации
        }

        return Integer.compare(book1.getPublishingYear(), book2.getPublishingYear());
    }
}

@AllArgsConstructor
@Data
class smsMessage{
    private String phoneNumber;
    private String message;
}