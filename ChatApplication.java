// ==================================================
// EXERCISE 2: REAL-TIME CHAT APPLICATION
// ==================================================

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Scanner;

// Message Model
enum MessageType {
    CHAT, SYSTEM, PRIVATE
}

class Message {
    private final String id;
    private String username;
    private String content;
    private LocalDateTime timestamp;
    private String roomId;
    private MessageType type;

    public Message(String username, String content, String roomId, MessageType type) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        if (username == null) throw new NullPointerException("username");
        if (content == null) throw new NullPointerException("content");
        if (roomId == null) throw new NullPointerException("roomId");
        this.username = username;
        this.content = content;
        this.roomId = roomId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public Message(String username, String content, String roomId) {
        this(username, content, roomId, MessageType.CHAT);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return switch (type) {
            case SYSTEM -> "[" + timestamp.format(formatter) + "] SYSTEM: " + content;
            case PRIVATE -> "[" + timestamp.format(formatter) + "] PRIVATE from " + username + ": " + content;
            default -> "[" + timestamp.format(formatter) + "] " + username + ": " + content;
        };
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
}

// User Model
class User {
    private final String id;
    private String username;
    private final LocalDateTime joinedAt;
    private LocalDateTime lastActivity;
    private boolean isActive;

    public User(String username) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        if (username == null) throw new NullPointerException("username");
        this.username = username;
        this.joinedAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isActive = true;
    }

    public void updateActivity() {
        lastActivity = LocalDateTime.now();
        isActive = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}

// Chat Room Model
class ChatRoom {
    private final String id;
    private String name;
    private final LocalDateTime createdAt;
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final List<Message> messageHistory = new ArrayList<>();
    private int maxUsers;
    private final Object messageLock = new Object();

    public ChatRoom(String name, int maxUsers) {
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        if (name == null) throw new NullPointerException("name");
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.maxUsers = maxUsers;
    }

    public ChatRoom(String name) {
        this(name, 50);
    }

    public boolean addUser(User user) {
        if (user == null) return false;
        if (users.size() >= maxUsers) return false;
        return users.putIfAbsent(user.getId(), user) == null;
    }

    public boolean removeUser(String userId) {
        return users.remove(userId) != null;
    }

    public void addMessage(Message message) {
        if (message == null) return;
        synchronized (messageLock) {
            messageHistory.add(message);
            if (messageHistory.size() > 100) {
                messageHistory.remove(0);
            }
        }
    }

    public List<Message> getRecentMessages(int count) {
        synchronized (messageLock) {
            int from = Math.max(0, messageHistory.size() - count);
            return new ArrayList<>(messageHistory.subList(from, messageHistory.size()));
        }
    }

    public List<Message> getRecentMessages() {
        return getRecentMessages(20);
    }

    public List<User> getActiveUsers() {
        return users.values().stream().filter(User::isActive).collect(Collectors.toList());
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ConcurrentHashMap<String, User> getUsers() { return users; }
    public List<Message> getMessageHistory() { return messageHistory; }
    public int getMaxUsers() { return maxUsers; }
    public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }
}

// Observer Pattern for Chat Events
interface ChatObserver {
    void onMessageReceived(Message message);
    void onUserJoined(User user, String roomId);
    void onUserLeft(User user, String roomId);
    void onRoomCreated(ChatRoom room);
}

// Adapter Pattern for Different Communication Protocols
interface CommunicationProtocol {
    CompletableFuture<Void> sendMessageAsync(String recipient, String message);
    CompletableFuture<Void> broadcastAsync(List<String> recipients, String message);
    boolean isConnected();
}

class WebSocketProtocol implements CommunicationProtocol {
    private boolean connected = true;

    @Override
    public CompletableFuture<Void> sendMessageAsync(String recipient, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[WebSocket] Sent to " + recipient + ": " + message);
        });
    }

    @Override
    public CompletableFuture<Void> broadcastAsync(List<String> recipients, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[WebSocket] Broadcast to " + recipients.size() + " recipients: " + message);
        });
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}

class HttpProtocol implements CommunicationProtocol {
    private boolean connected = true;

    @Override
    public CompletableFuture<Void> sendMessageAsync(String recipient, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[HTTP] Sent to " + recipient + ": " + message);
        });
    }

    @Override
    public CompletableFuture<Void> broadcastAsync(List<String> recipients, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[HTTP] Broadcast to " + recipients.size() + " recipients: " + message);
        });
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}

class CommunicationAdapter implements CommunicationProtocol {
    private final CommunicationProtocol protocol;
    private final Logger logger = Logger.getLogger(CommunicationAdapter.class.getName());

    public CommunicationAdapter(CommunicationProtocol protocol) {
        if (protocol == null) throw new NullPointerException("protocol");
        this.protocol = protocol;
    }

    @Override
    public boolean isConnected() {
        return protocol.isConnected();
    }

    @Override
    public CompletableFuture<Void> sendMessageAsync(String recipient, String message) {
        return protocol.sendMessageAsync(recipient, message)
                .thenRun(() -> logger.info("Message sent to " + recipient))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Failed to send message to " + recipient, ex);
                    return null;
                });
    }

    @Override
    public CompletableFuture<Void> broadcastAsync(List<String> recipients, String message) {
        return protocol.broadcastAsync(recipients, message)
                .thenRun(() -> logger.info("Message broadcast to " + recipients.size() + " recipients"))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Failed to broadcast message", ex);
                    return null;
                });
    }
}

// Singleton Pattern for Chat Server Management
class ChatServer {
    private static ChatServer instance;
    private static final Object lock = new Object();

    private final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final List<ChatObserver> observers = new ArrayList<>();
    private final CommunicationProtocol communicationProtocol;
    private final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private ChatServer() {
        // Use WebSocket as default protocol
        communicationProtocol = new CommunicationAdapter(new WebSocketProtocol());
        logger.info("Chat server initialized");
    }

    public static ChatServer getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ChatServer();
                }
            }
        }
        return instance;
    }

    public void subscribe(ChatObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            logger.info("Observer subscribed to chat server");
        }
    }

    public void unsubscribe(ChatObserver observer) {
        if (observer != null && observers.contains(observer)) {
            observers.remove(observer);
            logger.info("Observer unsubscribed from chat server");
        }
    }

    public String createRoom(String roomName, int maxUsers) {
        if (roomName == null || roomName.trim().isEmpty())
            throw new IllegalArgumentException("Room name cannot be empty");

        try {
            ChatRoom room = new ChatRoom(roomName, maxUsers);
            if (chatRooms.putIfAbsent(room.getId(), room) == null) {
                notifyObservers(o -> o.onRoomCreated(room));
                logger.info("Chat room created: " + roomName + " (ID: " + room.getId() + ")");
                return room.getId();
            }
            throw new IllegalStateException("Failed to create room");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error creating room: " + roomName, ex);
            throw ex;
        }
    }

    public String createRoom(String roomName) {
        return createRoom(roomName, 50);
    }

    public boolean joinRoom(String userId, String roomId) {
        User user = users.get(userId);
        ChatRoom room = chatRooms.get(roomId);
        if (user == null || room == null) return false;

        try {
            if (room.addUser(user)) {
                Message joinMessage = new Message("System", user.getUsername() + " joined the room", roomId, MessageType.SYSTEM);
                room.addMessage(joinMessage);

                notifyObservers(o -> o.onUserJoined(user, roomId));
                notifyObservers(o -> o.onMessageReceived(joinMessage));

                logger.info("User " + user.getUsername() + " joined room " + room.getName());
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error joining room: " + user.getUsername() + " -> " + room.getName(), ex);
            return false;
        }
    }

    public boolean leaveRoom(String userId, String roomId) {
        User user = users.get(userId);
        ChatRoom room = chatRooms.get(roomId);
        if (user == null || room == null) return false;

        try {
            if (room.removeUser(userId)) {
                Message leaveMessage = new Message("System", user.getUsername() + " left the room", roomId, MessageType.SYSTEM);
                room.addMessage(leaveMessage);

                notifyObservers(o -> o.onUserLeft(user, roomId));
                notifyObservers(o -> o.onMessageReceived(leaveMessage));

                logger.info("User " + user.getUsername() + " left room " + room.getName());
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error leaving room: " + user.getUsername() + " -> " + room.getName(), ex);
            return false;
        }
    }

    public String registerUser(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");

        if (users.values().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            throw new IllegalArgumentException("Username already taken");
        }

        try {
            User user = new User(username);
            if (users.putIfAbsent(user.getId(), user) == null) {
                logger.info("User registered: " + username + " (ID: " + user.getId() + ")");
                return user.getId();
            }
            throw new IllegalStateException("Failed to register user");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error registering user: " + username, ex);
            throw ex;
        }
    }

    public CompletableFuture<Boolean> sendMessageAsync(String userId, String roomId, String content) {
        User user = users.get(userId);
        ChatRoom room = chatRooms.get(roomId);
        if (user == null || room == null) return CompletableFuture.completedFuture(false);

        if (!room.getUsers().containsKey(userId)) return CompletableFuture.completedFuture(false);

        try {
            Message message = new Message(user.getUsername(), content, roomId);
            room.addMessage(message);
            user.updateActivity();

            notifyObservers(o -> o.onMessageReceived(message));

            List<String> recipients = room.getActiveUsers().stream().map(User::getId).collect(Collectors.toList());
            return communicationProtocol.broadcastAsync(recipients, message.toString())
                    .thenApply(v -> {
                        logger.info("Message sent by " + user.getUsername() + " in room " + room.getName());
                        return true;
                    })
                    .exceptionally(ex -> {
                        logger.log(Level.SEVERE, "Error sending message: " + user.getUsername() + " -> " + room.getName(), ex);
                        return false;
                    });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error sending message: " + user.getUsername() + " -> " + room.getName(), ex);
            return CompletableFuture.completedFuture(false);
        }
    }

    public CompletableFuture<Boolean> sendPrivateMessageAsync(String fromUserId, String toUsername, String content) {
        User fromUser = users.get(fromUserId);
        if (fromUser == null) return CompletableFuture.completedFuture(false);

        User toUser = users.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(toUsername) && u.isActive())
                .findFirst().orElse(null);

        if (toUser == null) return CompletableFuture.completedFuture(false);

        try {
            Message message = new Message(fromUser.getUsername(), content, "private", MessageType.PRIVATE);

            return communicationProtocol.sendMessageAsync(toUser.getId(), message.toString())
                    .thenApply(v -> {
                        logger.info("Private message sent: " + fromUser.getUsername() + " -> " + toUser.getUsername());
                        return true;
                    })
                    .exceptionally(ex -> {
                        logger.log(Level.SEVERE, "Error sending private message: " + fromUser.getUsername() + " -> " + toUsername, ex);
                        return false;
                    });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error sending private message: " + fromUser.getUsername() + " -> " + toUsername, ex);
            return CompletableFuture.completedFuture(false);
        }
    }

    public List<ChatRoom> getAvailableRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoom getRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public List<User> getActiveUsers() {
        return users.values().stream().filter(User::isActive).collect(Collectors.toList());
    }

    private void notifyObservers(java.util.function.Consumer<ChatObserver> action) {
        for (ChatObserver observer : new ArrayList<>(observers)) {
            try {
                action.accept(observer);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error notifying observer", ex);
            }
        }
    }

    public int getRoomCount() {
        return chatRooms.size();
    }

    public int getUserCount() {
        return users.size();
    }
}

// Chat Client Observer Implementation
class ChatClient implements ChatObserver {
    private final Logger logger = Logger.getLogger(ChatClient.class.getName());
    private String currentUserId;
    private String currentRoomId;

    @Override
    public void onMessageReceived(Message message) {
        if (message.getRoomId().equals(currentRoomId) || message.getType() == MessageType.PRIVATE) {
            System.out.println(message.toString());
        }
    }

    @Override
    public void onUserJoined(User user, String roomId) {
        if (roomId.equals(currentRoomId) && !user.getId().equals(currentUserId)) {
            System.out.println("User " + user.getUsername() + " joined the room");
        }
    }

    @Override
    public void onUserLeft(User user, String roomId) {
        if (roomId.equals(currentRoomId) && !user.getId().equals(currentUserId)) {
            System.out.println("User " + user.getUsername() + " left the room");
        }
    }

    @Override
    public void onRoomCreated(ChatRoom room) {
        System.out.println("New room created: " + room.getName());
    }

    public void setCurrentUser(String userId) {
        this.currentUserId = userId;
    }

    public void setCurrentRoom(String roomId) {
        this.currentRoomId = roomId;
    }
}

// Main Chat Application with Console Simulation
public class ChatApplication {
    private final ChatServer chatServer;
    private final ChatClient chatClient;
    private final Logger logger = Logger.getLogger(ChatApplication.class.getName());
    private boolean isRunning;
    private String currentUserId;
    private String currentRoomId;

    public ChatApplication() {
        chatClient = new ChatClient();
        chatServer = ChatServer.getInstance();
        chatServer.subscribe(chatClient);
        isRunning = false;
    }

    public void start() {
        isRunning = true;
        logger.info("Chat application started");

        System.out.println("=================================================");
        System.out.println("       REAL-TIME CHAT APPLICATION");
        System.out.println("=================================================");
        System.out.println("Welcome to the Real-time Chat Application!");
        System.out.println();
        System.out.println("Available Commands:");
        System.out.println("  /register <username>           - Register a new user");
        System.out.println("  /create_room <roomname>        - Create a new chat room");
        System.out.println("  /join <roomid>                 - Join a chat room");
        System.out.println("  /leave                         - Leave current room");
        System.out.println("  /send <message>                - Send message to current room");
        System.out.println("  /private <username> <message>  - Send private message");
        System.out.println("  /exit                          - Exit application");
        System.out.println("=================================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+", 3);
            String command = parts[0];

            try {
                switch (command) {
                    case "/register":
                        if (parts.length < 2) {
                            System.out.println("Usage: /register <username>");
                            break;
                        }
                        currentUserId = chatServer.registerUser(parts[1]);
                        chatClient.setCurrentUser(currentUserId);
                        System.out.println("Registered as " + parts[1] + " (ID: " + currentUserId + ")");
                        break;

                    case "/create_room":
                        if (parts.length < 2) {
                            System.out.println("Usage: /create_room <roomname>");
                            break;
                        }
                        String roomId = chatServer.createRoom(parts[1]);
                        System.out.println("Created room '" + parts[1] + "' (ID: " + roomId + ")");
                        break;

                    case "/join":
                        if (parts.length < 2) {
                            System.out.println("Usage: /join <roomid>");
                            break;
                        }
                        if (currentUserId == null) {
                            System.out.println("Please register first using /register <username>");
                            break;
                        }
                        if (chatServer.joinRoom(currentUserId, parts[1])) {
                            currentRoomId = parts[1];
                            chatClient.setCurrentRoom(currentRoomId);
                            System.out.println("Joined room " + parts[1]);
                            // Display recent messages
                            ChatRoom room = chatServer.getRoom(currentRoomId);
                            if (room != null) {
                                List<Message> messages = room.getRecentMessages();
                                if (!messages.isEmpty()) {
                                    System.out.println("\n--- Recent Messages ---");
                                    messages.forEach(msg -> System.out.println(msg));
                                    System.out.println("--- End of Messages ---\n");
                                }
                            }
                        } else {
                            System.out.println("Failed to join room. Check if room ID is correct.");
                        }
                        break;

                    case "/leave":
                        if (currentUserId == null || currentRoomId == null) {
                            System.out.println("You are not in a room!");
                            break;
                        }
                        if (chatServer.leaveRoom(currentUserId, currentRoomId)) {
                            System.out.println("Left room " + currentRoomId);
                            currentRoomId = null;
                            chatClient.setCurrentRoom(null);
                        } else {
                            System.out.println("Failed to leave room");
                        }
                        break;

                    case "/send":
                        if (parts.length < 2) {
                            System.out.println("Usage: /send <message>");
                            break;
                        }
                        if (currentRoomId == null) {
                            System.out.println("You must join a room first!");
                            break;
                        }
                        chatServer.sendMessageAsync(currentUserId, currentRoomId, parts[1]).join();
                        break;

                    case "/private":
                        if (parts.length < 3) {
                            System.out.println("Usage: /private <username> <message>");
                            break;
                        }
                        chatServer.sendPrivateMessageAsync(currentUserId, parts[1], parts[2]).join();
                        break;

                    case "/exit":
                        isRunning = false;
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("Unknown command. Type a command to see available options.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        ChatApplication app = new ChatApplication();
        app.start();
    }
}