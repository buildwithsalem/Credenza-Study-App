# Study Tracker

A Java-based web application for tracking study sessions, monitoring learning progress, and setting study goals. Built with Spring Boot and designed for educational demonstrations and personal productivity.

## Features

- **Log Study Sessions**: Record your study sessions with subject, duration, and notes
- **Track Progress**: Visualize your study time with interactive charts
- **Set Goals**: Create daily, weekly, or monthly study goals
- **Real-time Analytics**: Get insights on your learning patterns and progress
- **Persistent Storage**: All data is saved automatically using H2 database

## Technology Stack

### Backend (Java)
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence layer
- **Spring Web** - REST API controllers
- **H2 Database** - In-memory/file-based database
- **Lombok** - Reduces boilerplate code
- **Jakarta Validation** - Input validation

### Frontend
- **HTML5/CSS3** - Page structure and styling
- **Bootstrap 5** - Responsive UI framework
- **Vanilla JavaScript** - Client-side logic
- **Chart.js** - Data visualization

## Project Structure

```
study-tracker/
├── src/
│   └── main/
│       ├── java/com/studytracker/
│       │   ├── StudyTrackerApplication.java    # Main application class
│       │   ├── model/                          # JPA entities
│       │   │   ├── StudySession.java
│       │   │   └── Goal.java
│       │   ├── repository/                     # Data access layer
│       │   │   ├── StudySessionRepository.java
│       │   │   └── GoalRepository.java
│       │   ├── service/                        # Business logic
│       │   │   ├── StudySessionService.java
│       │   │   └── GoalService.java
│       │   └── controller/                     # REST API endpoints
│       │       ├── StudySessionController.java
│       │       └── GoalController.java
│       └── resources/
│           ├── application.properties          # Configuration
│           └── static/                         # Frontend files
│               ├── index.html
│               ├── style.css
│               └── app.js
├── pom.xml                                     # Maven dependencies
└── README.md
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/study-tracker.git
cd study-tracker
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Open your browser and navigate to:
```
http://localhost:5000
```

## API Endpoints

### Study Sessions

- `POST /api/sessions` - Create a new study session
- `GET /api/sessions` - Get all study sessions
- `GET /api/sessions/{id}` - Get session by ID
- `DELETE /api/sessions/{id}` - Delete a session
- `GET /api/sessions/analytics/{period}` - Get analytics (daily/weekly/monthly)

### Goals

- `POST /api/goals` - Create a new goal
- `GET /api/goals` - Get all goals
- `GET /api/goals/active` - Get active goals only
- `GET /api/goals/{id}` - Get goal by ID
- `PUT /api/goals/{id}` - Update a goal
- `DELETE /api/goals/{id}` - Delete a goal
- `GET /api/goals/{id}/progress` - Get goal progress

## Database

The application uses H2 database for data persistence:

- **File location**: `./data/studytracker.mv.db`
- **H2 Console**: Available at `http://localhost:5000/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/studytracker`
- **Username**: `sa`
- **Password**: (empty)

## Configuration

Key configuration in `src/main/resources/application.properties`:

```properties
server.port=5000
spring.datasource.url=jdbc:h2:file:./data/studytracker
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

## Usage Examples

### Log a Study Session

1. Enter subject (e.g., "Java Programming")
2. Enter duration in minutes (e.g., 60)
3. Add optional notes
4. Click "Log Session"

### Create a Study Goal

1. Enter goal name (e.g., "Daily Java Study")
2. Set target minutes (e.g., 120)
3. Select goal type (Daily/Weekly/Monthly)
4. Click "Create Goal"

### View Progress

- Use the dropdown to select time period (Last 24 Hours, Last Week, Last Month)
- View bar chart showing study time by subject
- See total study time for the selected period

## Development

### Building from Source

```bash
mvn clean package
```

### Running Tests

```bash
mvn test
```

### Creating Executable JAR

```bash
mvn clean package
java -jar target/study-tracker-1.0.0.jar
```

## Contributing

This project is designed for educational purposes. Feel free to fork and modify for your own learning!

## License

This project is open source and available for educational use.

## Author

Created as a class presentation project demonstrating Java Spring Boot development skills.

## Acknowledgments

- Spring Boot for the excellent framework
- Chart.js for beautiful data visualizations
- Bootstrap for responsive UI components
