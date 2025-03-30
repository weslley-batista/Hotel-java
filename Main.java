import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Hotel hotel = new Hotel();

    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int option = getValidIntegerInput("Escolha uma opção: ", 1, 10);
            
            switch (option) {
                case 1 -> makeReservation();
                case 2 -> cancelReservation();
                case 3 -> System.out.printf("Média do preço de todas as reservas: R$%.2f%n", hotel.calculateAveragePrice());
                case 4 -> System.out.printf("Mediana do preço de todas as reservas: R$%.2f%n", hotel.calculateMedianPrice());
                case 5 -> calculateAveragePriceByType();
                case 6 -> calculateMedianPriceByType();
                case 7 -> calculateAveragePriceByGender();
                case 8 -> calculateMedianPriceByGender();
                case 9 -> showReservationHistory();
                case 10 -> running = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n===== Sistema de Hotelaria =====");
        System.out.println("1. Fazer reserva");
        System.out.println("2. Cancelar reserva");
        System.out.println("3. Calcular média de preços");
        System.out.println("4. Calcular mediana de preços");
        System.out.println("5. Calcular média por tipo de quarto");
        System.out.println("6. Calcular mediana por tipo de quarto");
        System.out.println("7. Calcular média por sexo");
        System.out.println("8. Calcular mediana por sexo");
        System.out.println("9. Histórico de reservas");
        System.out.println("10. Sair");
    }

    private static void makeReservation() {
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        
        if (hotel.hasReservation(name)) {
            System.out.println("Esta pessoa já possui uma reserva ativa.");
            return;
        }
        
        String gender = getValidGenderInput();
        int age = getValidIntegerInput("Idade: ", 0, 120);
        int peopleCount = getValidIntegerInput("Quantidade de pessoas: ", 1, 8);

        Person person = new Person(name, gender, age, peopleCount);
        
        if (!person.canReserve()) {
            System.out.println("A pessoa deve ter pelo menos 18 anos para reservar.");
            return;
        }

        if (hotel.makeReservation(person)) {
            System.out.println("Reserva realizada com sucesso!");
        } else {
            System.out.println("Não há quartos disponíveis para esta quantidade de pessoas.");
        }
    }

    private static void cancelReservation() {
        System.out.print("Nome da pessoa para cancelar reserva: ");
        String name = scanner.nextLine();
        
        if (hotel.cancelReservation(name)) {
            System.out.println("Reserva cancelada com sucesso!");
        } else {
            System.out.println("Pessoa não encontrada ou sem reserva ativa.");
        }
    }

    private static void calculateAveragePriceByType() {
        String roomType = getValidRoomTypeInput();
        double average = hotel.calculateAveragePriceByType(roomType);
        System.out.printf("Média de preço para quartos %s: R$%.2f%n", roomType, average);
    }

    private static void calculateMedianPriceByType() {
        String roomType = getValidRoomTypeInput();
        double median = hotel.calculateMedianPriceByType(roomType);
        System.out.printf("Mediana de preço para quartos %s: R$%.2f%n", roomType, median);
    }

    private static void calculateAveragePriceByGender() {
        String gender = getValidGenderInput();
        double average = hotel.calculateAveragePriceByGender(gender);
        System.out.printf("Média de preço para hóspedes do sexo %s: R$%.2f%n", gender, average);
    }

    private static void calculateMedianPriceByGender() {
        String gender = getValidGenderInput();
        double median = hotel.calculateMedianPriceByGender(gender);
        System.out.printf("Mediana de preço para hóspedes do sexo %s: R$%.2f%n", gender, median);
    }

    private static void showReservationHistory() {
        List<Person> history = hotel.getReservationHistory();
        if (history.isEmpty()) {
            System.out.println("\nNenhuma reserva encontrada no histórico.");
            return;
        }
        
        System.out.println("\n=== Histórico de Reservas ===");
        for (Person person : history) {
            String status = hotel.getGuests().contains(person) ? "Ativa" : "Cancelada";
            System.out.printf("Nome: %-15s | Sexo: %-9s | Idade: %2d | Pessoas: %d | Status: %-9s%n",
                    person.getName(),
                    person.getGender(),
                    person.getAge(),
                    person.getNumberOfPeople(),
                    status);
        }
    }

    private static String getValidGenderInput() {
        while (true) {
            System.out.print("Sexo (Masculino, Feminino, Outro): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Masculino") || input.equalsIgnoreCase("Feminino") || input.equalsIgnoreCase("Outro")) {
                return input;
            }
            System.out.println("Opção inválida. Por favor, escolha entre Masculino, Feminino ou Outro.");
        }
    }

    private static String getValidRoomTypeInput() {
        while (true) {
            System.out.print("Tipo de quarto (Simples, Comum, Luxuoso): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Simples") || input.equalsIgnoreCase("Comum") || input.equalsIgnoreCase("Luxuoso")) {
                return input;
            }
            System.out.println("Tipo de quarto inválido. Por favor, escolha entre Simples, Comum ou Luxuoso.");
        }
    }

    private static int getValidIntegerInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Por favor, insira um valor entre %d e %d.%n", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
                scanner.nextLine();
            }
        }
    }
}

class Hotel {
    private final List<Room> rooms;
    private final List<Person> guests;
    private final List<Person> reservationHistory;

    public Hotel() {
        this.rooms = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.reservationHistory = new ArrayList<>();
        initializeRooms();
    }

    private void initializeRooms() {
        // Quartos Simples (1-60)
        for (int i = 1; i <= 60; i++) {
            rooms.add(new Room(i, "Simples", 50.00, 2));
        }
        // Quartos Comuns (61-90)
        for (int i = 61; i <= 90; i++) {
            rooms.add(new Room(i, "Comum", 60.00, 4));
        }
        // Quartos Luxuosos (91-100)
        for (int i = 91; i <= 100; i++) {
            rooms.add(new Room(i, "Luxuoso", 120.00, 8));
        }
    }

    public boolean hasReservation(String personName) {
        return guests.stream()
            .anyMatch(p -> p.getName().equalsIgnoreCase(personName));
    }

    public boolean makeReservation(Person person) {
        if (hasReservation(person.getName())) {
            return false;
        }

        Optional<Room> availableRoom = rooms.stream()
            .filter(room -> !room.isOccupied())
            .filter(room -> person.getNumberOfPeople() <= room.getCapacity())
            .findFirst();

        if (availableRoom.isPresent()) {
            availableRoom.get().reserve(person);
            guests.add(person);
            reservationHistory.add(person);
            return true;
        }
        return false;
    }

    public boolean cancelReservation(String personName) {
        Optional<Room> roomToCancel = rooms.stream()
            .filter(Room::isOccupied)
            .filter(room -> room.getResponsiblePerson().getName().equalsIgnoreCase(personName))
            .findFirst();

        if (roomToCancel.isPresent()) {
            Person person = roomToCancel.get().getResponsiblePerson();
            roomToCancel.get().release();
            guests.remove(person);
            return true;
        }
        return false;
    }

    public List<Person> getReservationHistory() {
        return reservationHistory;
    }

    public List<Person> getGuests() {
        return guests;
    }

    public double calculateAveragePrice() {
        return calculateAverage(getReservedPrices());
    }

    public double calculateMedianPrice() {
        return calculateMedian(getReservedPrices());
    }

    public double calculateAveragePriceByType(String type) {
        return calculateAverage(getReservedPricesByType(type));
    }

    public double calculateMedianPriceByType(String type) {
        return calculateMedian(getReservedPricesByType(type));
    }

    public double calculateAveragePriceByGender(String gender) {
        return calculateAverage(getReservedPricesByGender(gender));
    }

    public double calculateMedianPriceByGender(String gender) {
        return calculateMedian(getReservedPricesByGender(gender));
    }

    private List<Double> getReservedPrices() {
        return rooms.stream()
            .filter(Room::isOccupied)
            .map(Room::getPrice)
            .collect(Collectors.toList());
    }

    private List<Double> getReservedPricesByType(String type) {
        return rooms.stream()
            .filter(Room::isOccupied)
            .filter(room -> room.getType().equalsIgnoreCase(type))
            .map(Room::getPrice)
            .collect(Collectors.toList());
    }

    private List<Double> getReservedPricesByGender(String gender) {
        return rooms.stream()
            .filter(Room::isOccupied)
            .filter(room -> room.getResponsiblePerson() != null)
            .filter(room -> room.getResponsiblePerson().getGender().equalsIgnoreCase(gender))
            .map(Room::getPrice)
            .collect(Collectors.toList());
    }

    private double calculateAverage(List<Double> values) {
        if (values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateMedian(List<Double> values) {
        if (values.isEmpty()) return 0;
        
        List<Double> sortedValues = values.stream()
            .sorted()
            .collect(Collectors.toList());
        
        int middle = sortedValues.size() / 2;
        
        if (sortedValues.size() % 2 == 0) {
            return (sortedValues.get(middle - 1) + sortedValues.get(middle)) / 2.0;
        } else {
            return sortedValues.get(middle);
        }
    }
}

class Room {
    private final int number;
    private final String type;
    private final double price;
    private final int capacity;
    private boolean occupied;
    private Person responsiblePerson;

    public Room(int number, String type, double price, int capacity) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.occupied = false;
        this.responsiblePerson = null;
    }

    public void reserve(Person person) {
        this.occupied = true;
        this.responsiblePerson = person;
    }

    public void release() {
        this.occupied = false;
        this.responsiblePerson = null;
    }

    public int getNumber() { return number; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public boolean isOccupied() { return occupied; }
    public Person getResponsiblePerson() { return responsiblePerson; }
}

class Person {
    private final String name;
    private final String gender;
    private final int age;
    private final int numberOfPeople;

    public Person(String name, String gender, int age, int numberOfPeople) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.numberOfPeople = numberOfPeople;
    }

    public boolean canReserve() {
        return age >= 18;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public int getNumberOfPeople() { return numberOfPeople; }
}
