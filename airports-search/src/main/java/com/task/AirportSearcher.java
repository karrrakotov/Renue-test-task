package com.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AirportSearcher {
    private final List<Airport> airports = new ArrayList<>();
    private final int searchColumn;
    private String resultCompilation;

    public AirportSearcher(int searchColumn) {
        this.searchColumn = searchColumn;
    }

    public void loadAirports(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                airports.add(new Airport(data, searchColumn));
            }
        }
    }

    public List<Airport> search(String query) {
        Instant start = Instant.now();

        List<Airport> result = airports.stream()
                .filter(a -> a.getSearchValue().toLowerCase().startsWith(query.toLowerCase()))
                .sorted(Comparator.comparing(a -> a.getData(searchColumn)))
                .collect(Collectors.toList());

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();

        resultCompilation = "Количество найденных аэропортов: " + result.size() + "\nВремя затраченное на поиск: "
                + timeElapsed + " мс";

        return result;
    }

    public int getSizeTable() {
        return airports.get(0).getSizeColumn();
    }

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String s) throws NumberFormatException {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isFloat(String s) throws NumberFormatException {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Используйте: java -jar airports-search.jar <searchColumn>");
            System.exit(1);
        }

        int searchColumn;
        try {
            searchColumn = Integer.parseInt(args[0]);

            if(searchColumn == 0) {
                System.err.println("Неверный формат поиска колонки: " + args[0] + "\nПожалуйста, введите значение колонки больше 0.");
                System.exit(1);
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Неверный формат поиска колонки: " + args[0] + "\nПожалуйста, введите значение колонки больше 0.");
            System.exit(1);
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Загрузка аэропортов...");
        AirportSearcher searcher = new AirportSearcher(searchColumn);

        try {
            searcher.loadAirports("airports.csv");
            if(searchColumn > searcher.getSizeTable()) {
                System.err.println("Вы ввели значение колонки, которой нет в таблице!" + "\nПожалуйста, введите нужное значение колонки от: 1 до " + searcher.getSizeTable() + ".");
                System.exit(1);
                return;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке аэропортов: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Аэропорты загружены.");

        while (true) {
            System.out.print("\nВведите поиск поисковой запрос (или введите \"!quit\" для выхода): ");
            String query = scanner.nextLine().trim();
            String enter = query;

            if(query.equalsIgnoreCase("!quit")) {
                query = enter;
            } else if(isDigit(enter) || isDouble(enter) || isFloat(enter)) {
                query = enter;
            } else {
                query = "\"" + enter;
            }
            System.out.println("Введенные данные: " + enter);

            if (query.equalsIgnoreCase("!quit")) {
                break;
            }

            List<Airport> result = searcher.search(query);

            result.stream()
                    .sorted(Comparator.comparing(Airport::getSearchValue))
                    .forEach(a -> System.out.printf("%s[%s]%n", a.getSearchValue(), String.join(" ", a.getSearchString())));

            System.out.println(searcher.resultCompilation);
        }

        scanner.close();
    }
}
