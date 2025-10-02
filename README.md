# EI_Assignment_Bhumit

Exercise-1

Java Design Patterns Demonstration
A comprehensive demonstration of 6 essential design patterns in Java, showcasing best practices in object-oriented programming and software architecture.
📋 Overview
This project demonstrates the implementation of three categories of design patterns:

Behavioral Patterns: Observer, Command
Creational Patterns: Factory, Builder
Structural Patterns: Adapter, Decorator

🎯 Design Patterns Included
1. Observer Pattern (Behavioral)
Use Case: News Agency System

Implements a publish-subscribe model
Multiple news channels subscribe to a news agency
Automatic notification when news is published
Thread-safe observer management

2. Command Pattern (Behavioral)
Use Case: Smart Home Control System

Encapsulates requests as objects
Supports undo/redo operations
Command history tracking
Decouples invoker from receiver

3. Factory Pattern (Creational)
Use Case: Vehicle Manufacturing System

Creates objects without specifying exact classes
Supports Car, Motorcycle, and Truck creation
Centralized object creation logic
Input validation for model and year

4. Builder Pattern (Creational)
Use Case: Computer Configuration System

Constructs complex objects step by step
Fluent interface for easy configuration
Separates construction from representation
Supports method chaining

5. Adapter Pattern (Structural)
Use Case: Payment Processing System

Converts PayPal API to standard payment interface
Makes incompatible interfaces work together
Wraps third-party libraries seamlessly
Transaction ID tracking

6. Decorator Pattern (Structural)
Use Case: Coffee Shop System

Adds functionality dynamically to objects
Extends beverage with condiments (Mocha, etc.)
Maintains single responsibility principle
Calculates cumulative costs

Exercise-2

Real-Time Chat Application
A console-based real-time chat application demonstrating advanced Java programming concepts including design patterns, concurrency, asynchronous programming, and thread-safe operations.
📋 Overview
This project implements a fully functional chat system with support for multiple chat rooms, private messaging, user management, and real-time notifications. The application showcases enterprise-level Java development practices and architectural patterns.
✨ Features
Core Features

👤 User Registration: Register with unique usernames
🏠 Chat Rooms: Create and join multiple chat rooms
💬 Real-Time Messaging: Send messages to chat rooms
🔒 Private Messaging: Send direct messages to specific users
📜 Message History: View recent messages when joining a room
👥 User Tracking: Track active users in rooms
⏰ Timestamps: All messages include timestamps

Technical Features

⚡ Asynchronous Operations: Using CompletableFuture for non-blocking I/O
🔒 Thread Safety: ConcurrentHashMap and synchronized blocks
🎯 Design Patterns: Observer, Singleton, Adapter patterns
📊 Logging: Comprehensive logging system
🔄 Protocol Abstraction: Support for WebSocket and HTTP protocols
🛡️ Error Handling: Robust exception handling throughout

🎯 Design Patterns Implemented
1. Observer Pattern
Use Case: Real-time event notifications

ChatObserver interface for event subscriptions
Notifies clients about messages, user joins/leaves, and room creation
Decouples event producers from consumers

2. Singleton Pattern
Use Case: Chat Server Management

Ensures single instance of ChatServer using double-checked locking
Thread-safe lazy initialization
Global point of access for server operations

3. Adapter Pattern
Use Case: Communication Protocol Abstraction

CommunicationProtocol interface with multiple implementations
WebSocketProtocol and HttpProtocol adapters
CommunicationAdapter adds logging and error handling
Easy to switch between protocols or add new ones
