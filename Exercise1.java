// ==================================================
// EXERCISE 1: DESIGN PATTERNS DEMONSTRATION
// ==================================================

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.Year;

// 1. BEHAVIORAL PATTERNS

// 1.1 OBSERVER PATTERN - News Agency System
interface NewsObserver {
    void update(String news);
}

interface NewsAgency {
    void subscribe(NewsObserver observer);
    void unsubscribe(NewsObserver observer);
    void notifyObservers(String news);
}

class NewsAgencyImpl implements NewsAgency {
    private final List<NewsObserver> observers = new ArrayList<>();
    private final Logger logger = Logger.getLogger(NewsAgencyImpl.class.getName());

    public void subscribe(NewsObserver observer) {
        if (observer == null) throw new NullPointerException("observer");
        if (!observers.contains(observer)) {
            observers.add(observer);
            logger.info("Observer subscribed to news agency");
        }
    }

    public void unsubscribe(NewsObserver observer) {
        if (observer != null && observers.contains(observer)) {
            observers.remove(observer);
            logger.info("Observer unsubscribed from news agency");
        }
    }

    public void notifyObservers(String news) {
        if (news == null || news.trim().isEmpty()) return;

        for (NewsObserver observer : new ArrayList<>(observers)) {
            try {
                observer.update(news);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error notifying observer", ex);
            }
        }
        logger.info("News broadcast to " + observers.size() + " observers");
    }

    public void publishNews(String news) {
        logger.info("Publishing news: " + news);
        notifyObservers(news);
    }
}

class NewsChannel implements NewsObserver {
    private final String channelName;
    private final Logger logger = Logger.getLogger(NewsChannel.class.getName());

    public NewsChannel(String channelName) {
        if (channelName == null) throw new NullPointerException("channelName");
        this.channelName = channelName;
    }

    @Override
    public void update(String news) {
        logger.info("[" + channelName + "] Broadcasting: " + news);
        System.out.println("[" + channelName + "] Broadcasting: " + news);
    }
}

// 1.2 COMMAND PATTERN - Smart Home Control System
interface Command {
    void execute();
    void undo();
    String getDescription();
}

class Light {
    private final String location;
    private boolean isOn;

    public Light(String location) {
        if (location == null) throw new NullPointerException("location");
        this.location = location;
    }

    public void turnOn() {
        isOn = true;
        System.out.println("Light in " + location + " is ON");
    }

    public void turnOff() {
        isOn = false;
        System.out.println("Light in " + location + " is OFF");
    }

    public String getLocation() {
        return location;
    }

    public boolean isOn() {
        return isOn;
    }
}

class LightOnCommand implements Command {
    private final Light light;
    private final String description;

    public LightOnCommand(Light light) {
        if (light == null) throw new NullPointerException("light");
        this.light = light;
        this.description = "Turn on light in " + light.getLocation();
    }

    @Override
    public void execute() {
        light.turnOn();
    }

    @Override
    public void undo() {
        light.turnOff();
    }

    @Override
    public String getDescription() {
        return description;
    }
}

class LightOffCommand implements Command {
    private final Light light;
    private final String description;

    public LightOffCommand(Light light) {
        if (light == null) throw new NullPointerException("light");
        this.light = light;
        this.description = "Turn off light in " + light.getLocation();
    }

    @Override
    public void execute() {
        light.turnOff();
    }

    @Override
    public void undo() {
        light.turnOn();
    }

    @Override
    public String getDescription() {
        return description;
    }
}

class RemoteControl {
    private final Stack<Command> commandHistory = new Stack<>();
    private final Logger logger = Logger.getLogger(RemoteControl.class.getName());

    public void executeCommand(Command command) {
        if (command == null) throw new NullPointerException("command");

        try {
            command.execute();
            commandHistory.push(command);
            logger.info("Executed command: " + command.getDescription());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to execute command: " + command.getDescription(), ex);
            throw ex;
        }
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            try {
                lastCommand.undo();
                logger.info("Undone command: " + lastCommand.getDescription());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to undo command: " + lastCommand.getDescription(), ex);
                throw ex;
            }
        } else {
            System.out.println("No commands to undo");
        }
    }
}

// 2. CREATIONAL PATTERNS

// 2.1 FACTORY PATTERN - Vehicle Manufacturing System
enum VehicleType {
    CAR, MOTORCYCLE, TRUCK
}

abstract class Vehicle {
    protected String model;
    protected int year;

    public abstract void start();
    public abstract void stop();

    public String getInfo() {
        return getClass().getSimpleName() + ": " + model + " (" + year + ")";
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }
}

class Car extends Vehicle {
    public Car(String model, int year) {
        this.model = model;
        this.year = year;
    }

    @Override
    public void start() {
        System.out.println("Car " + model + " engine started");
    }

    @Override
    public void stop() {
        System.out.println("Car " + model + " engine stopped");
    }
}

class Motorcycle extends Vehicle {
    public Motorcycle(String model, int year) {
        this.model = model;
        this.year = year;
    }

    @Override
    public void start() {
        System.out.println("Motorcycle " + model + " engine revved up");
    }

    @Override
    public void stop() {
        System.out.println("Motorcycle " + model + " engine shut down");
    }
}

class Truck extends Vehicle {
    public Truck(String model, int year) {
        this.model = model;
        this.year = year;
    }

    @Override
    public void start() {
        System.out.println("Truck " + model + " diesel engine started");
    }

    @Override
    public void stop() {
        System.out.println("Truck " + model + " diesel engine stopped");
    }
}

class VehicleFactory {
    public static Vehicle createVehicle(VehicleType type, String model, int year) {
        if (model == null || model.trim().isEmpty())
            throw new IllegalArgumentException("Model cannot be empty");
        if (year < 1900 || year > Year.now().getValue() + 1)
            throw new IllegalArgumentException("Invalid year");

        return switch (type) {
            case CAR -> new Car(model, year);
            case MOTORCYCLE -> new Motorcycle(model, year);
            case TRUCK -> new Truck(model, year);
        };
    }
}

// 2.2 BUILDER PATTERN - Computer Configuration System
class Computer {
    private String cpu;
    private String ram;
    private String storage;
    private String graphicsCard;

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }

    @Override
    public String toString() {
        return "Computer: CPU=" + cpu + ", RAM=" + ram + ", Storage=" + storage + ", GPU=" + graphicsCard;
    }
}

interface ComputerBuilder {
    ComputerBuilder setCpu(String cpu);
    ComputerBuilder setRam(String ram);
    ComputerBuilder setStorage(String storage);
    ComputerBuilder setGraphicsCard(String gpu);
    Computer build();
}

class ComputerBuilderImpl implements ComputerBuilder {
    private final Computer computer = new Computer();

    @Override
    public ComputerBuilder setCpu(String cpu) {
        if (cpu == null) throw new NullPointerException("cpu");
        computer.setCpu(cpu);
        return this;
    }

    @Override
    public ComputerBuilder setRam(String ram) {
        if (ram == null) throw new NullPointerException("ram");
        computer.setRam(ram);
        return this;
    }

    @Override
    public ComputerBuilder setStorage(String storage) {
        if (storage == null) throw new NullPointerException("storage");
        computer.setStorage(storage);
        return this;
    }

    @Override
    public ComputerBuilder setGraphicsCard(String gpu) {
        if (gpu == null) throw new NullPointerException("gpu");
        computer.setGraphicsCard(gpu);
        return this;
    }

    @Override
    public Computer build() {
        return computer;
    }
}

// 3. STRUCTURAL PATTERNS

// 3.1 ADAPTER PATTERN - Payment Processing System
interface PaymentProcessor {
    boolean processPayment(double amount, String currency);
    String getTransactionId();
}

class PayPalAPI {
    public String makePayment(double amount) {
        System.out.println("PayPal: Processing $" + amount);
        return "PP_" + java.util.UUID.randomUUID().toString().replace("-", "");
    }
}

class PayPalAdapter implements PaymentProcessor {
    private final PayPalAPI payPalAPI;
    private String lastTransactionId;

    public PayPalAdapter(PayPalAPI payPalAPI) {
        if (payPalAPI == null) throw new NullPointerException("payPalAPI");
        this.payPalAPI = payPalAPI;
    }

    @Override
    public boolean processPayment(double amount, String currency) {
        try {
            lastTransactionId = payPalAPI.makePayment(amount);
            return lastTransactionId != null && !lastTransactionId.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getTransactionId() {
        return lastTransactionId;
    }
}

// 3.2 DECORATOR PATTERN - Coffee Shop System
interface Beverage {
    String getDescription();
    double getCost();
}

class Espresso implements Beverage {
    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 1.99;
    }
}

abstract class CondimentDecorator implements Beverage {
    protected Beverage beverage;

    protected CondimentDecorator(Beverage beverage) {
        if (beverage == null) throw new NullPointerException("beverage");
        this.beverage = beverage;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription();
    }

    @Override
    public double getCost() {
        return beverage.getCost();
    }
}

class Mocha extends CondimentDecorator {
    public Mocha(Beverage beverage) {
        super(beverage);
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Mocha";
    }

    @Override
    public double getCost() {
        return beverage.getCost() + 0.20;
    }
}

// Demonstration Main Class
public class Exercise1{
    public static void main(String[] args) {
        System.out.println("=== Demonstrating Observer Pattern ===");
        NewsAgency agency = new NewsAgencyImpl();
        NewsObserver channel1 = new NewsChannel("CNN");
        NewsObserver channel2 = new NewsChannel("BBC");
        agency.subscribe(channel1);
        agency.subscribe(channel2);
        ((NewsAgencyImpl) agency).publishNews("Global News Update");
        agency.unsubscribe(channel2);
        ((NewsAgencyImpl) agency).publishNews("Latest Breaking News");

        System.out.println("\n=== Demonstrating Command Pattern ===");
        Light livingRoomLight = new Light("Living Room");
        Command lightOn = new LightOnCommand(livingRoomLight);
        Command lightOff = new LightOffCommand(livingRoomLight);
        RemoteControl remote = new RemoteControl();
        remote.executeCommand(lightOn);
        remote.executeCommand(lightOff);
        remote.undoLastCommand();

        System.out.println("\n=== Demonstrating Factory Pattern ===");
        Vehicle car = VehicleFactory.createVehicle(VehicleType.CAR, "Tesla Model S", 2023);
        car.start();
        car.stop();
        System.out.println(car.getInfo());

        System.out.println("\n=== Demonstrating Builder Pattern ===");
        Computer computer = new ComputerBuilderImpl()
                .setCpu("Intel i7")
                .setRam("16GB")
                .setStorage("512GB SSD")
                .setGraphicsCard("NVIDIA RTX 3080")
                .build();
        System.out.println(computer);

        System.out.println("\n=== Demonstrating Adapter Pattern ===");
        PayPalAPI payPalAPI = new PayPalAPI();
        PaymentProcessor processor = new PayPalAdapter(payPalAPI);
        boolean success = processor.processPayment(100.0, "USD");
        System.out.println("Payment successful: " + success);
        System.out.println("Transaction ID: " + processor.getTransactionId());

        System.out.println("\n=== Demonstrating Decorator Pattern ===");
        Beverage espresso = new Espresso();
        Beverage mochaEspresso = new Mocha(espresso);
        System.out.println(mochaEspresso.getDescription() + " $" + mochaEspresso.getCost());
    }
}