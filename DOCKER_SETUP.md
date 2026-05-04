# HomePS - Gaming Café Management System

Modern web-based management system for gaming cafes/internet shops, built with Java Servlets, PostgreSQL, and a responsive HTML5 frontend.

## Features

- **Machine Management**: Track all gaming machines, their status, and current usage
- **Session Management**: Start and end gaming sessions with automatic time tracking
- **Service Orders**: Manage food and beverage orders during active sessions
- **Invoice Management**: Generate and track invoices for each session
- **Promotional Events**: Create and manage discount events (hourly, daily, seasonal)
- **Revenue Analytics**: Daily, weekly, and monthly revenue reports
- **User Management**: Role-based access control (Admin/Employee)

## Architecture

- **Backend**: Java Servlets with Jakarta EE
- **Frontend**: Modern HTML5/CSS3 with vanilla JavaScript
- **Database**: PostgreSQL
- **Build**: Maven
- **Deployment**: Docker & Docker Compose

## Prerequisites

- Docker & Docker Compose (or Java 17+ and Maven 3.9+ for local development)
- PostgreSQL 15 (if running without Docker)

## Quick Start with Docker

### 1. Build and Start the Application

```bash
docker-compose up -d
```

This will:
- Start a PostgreSQL database on port 5432
- Build and start the Tomcat application on port 8080
- Initialize the database schema from `src/main/resources/db/init.sql`

### 2. Access the Application

- **URL**: http://localhost:8080/HomePS
- **Default Login**: Use any username/password (demo mode accepts all credentials)

### 3. Stop the Application

```bash
docker-compose down
```

## Local Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.9+
- PostgreSQL 15 (optional, can use Docker for database only)

### Build

```bash
mvn clean package
```

### Run Database Only (Optional)

```bash
docker-compose -f src/main/docker-compose.yml up db -d
```

Then configure your IDE to run Tomcat with:
- **Database URL**: `jdbc:postgresql://localhost:5432/homeps`
- **Database User**: `postgres`
- **Database Password**: `postgres`

## Project Structure

```
HomePS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/       # Servlets (HomeController, ApiMachineController, etc.)
│   │   │   ├── dao/              # Data Access Objects
│   │   │   ├── model/            # Entity Models (NhanVien, MayPS, etc.)
│   │   │   ├── filter/           # Request Filters (AuthFilter)
│   │   │   └── utils/            # Utility Classes (DBConnection, etc.)
│   │   ├── webapp/
│   │   │   ├── index.html        # Main HTML Interface (replaces JSP files)
│   │   │   └── WEB-INF/
│   │   │       └── web.xml       # Web Configuration
│   │   └── resources/
│   │       └── db/
│   │           └── init.sql      # Database Initialization Script
│   └── test/                     # Unit Tests
├── Dockerfile                    # Docker Image Configuration
├── docker-compose.yml            # Docker Compose Configuration
└── pom.xml                       # Maven Configuration

```

## API Endpoints

### Authentication
- **GET/POST** `/login` - Login page and form submission
- **GET** `/logout` - Logout and destroy session
- **GET** `/app` - Main application (requires authentication)

### Machines
- **GET** `/api/machines` - Get all machines and their status
- **POST** `/home` - Open/close machine (`action=open&mayId=<id>` or `action=close&mayId=<id>`)

### Other Features (in development)
- Dịch vụ (Services) - `/dichvu`
- Hóa đơn (Invoices) - `/hoadon`
- Sự kiện (Events) - `/sukien`
- Thống kê (Statistics) - `/thongke`

## Environment Variables

When using Docker, the following environment variables are automatically configured:

```
DB_URL=jdbc:postgresql://db:5432/homeps
DB_USER=postgres
DB_PASSWORD=postgres
DB_HOST=db
DB_PORT=5432
```

## Database Schema

The database is automatically initialized with tables for:
- **nhan_vien** - Employees
- **may_ps** - Gaming Machines
- **luot_choi** - Gaming Sessions
- **dich_vu** - Services/Menu Items
- **hoa_don** - Invoices
- **su_kien** - Promotional Events

See `src/main/resources/db/init.sql` for complete schema.

## Migration from JSP to HTML Frontend

The application has been migrated from JSP templates to a modern HTML5 single-page application:

### Key Changes
1. **Removed**: All JSP files (`index.jsp`, `login.jsp`, `dichvu.jsp`, etc.)
2. **Added**: `/src/main/webapp/index.html` - Complete responsive HTML interface
3. **Updated**: Controllers to support both form-based and API-based requests
4. **Added**: `AppController` - Serves HTML to authenticated users
5. **Added**: `ApiMachineController` - JSON API for machine data
6. **Updated**: `AuthFilter` - Allows access to HTML and static assets

### Frontend Architecture
The HTML interface:
- Uses vanilla JavaScript (no external framework)
- Implements client-side routing between pages
- Makes API calls to the Java backend
- Features modern dark theme with responsive design
- Supports real-time clock and status updates

## Security

### Authentication
- Session-based authentication
- Role-based access control (Admin/Employee)
- Automatic redirect to login for unauthorized access

### Database Connections
- Connection pooling via DBConnection utility
- Prepared statements for SQL injection prevention
- Transaction support for critical operations

## Troubleshooting

### Container Issues
```bash
# View logs
docker-compose logs -f backend

# Restart containers
docker-compose restart

# Full reset
docker-compose down -v
docker-compose up -d
```

### Database Connection Errors
1. Ensure PostgreSQL container is healthy: `docker-compose logs db`
2. Check environment variables in `docker-compose.yml`
3. Verify database initialization: `docker-compose exec db psql -U postgres -d homeps -c "\\dt"`

### Build Errors
```bash
# Clean build
mvn clean install

# Skip tests
mvn clean package -DskipTests

# Update dependencies
mvn dependency:resolve
```

## Performance Optimization Tips

1. **Connection Pooling**: Implement HikariCP for better connection management
2. **Caching**: Add Redis for session caching
3. **Database Indexing**: Index frequently queried columns
4. **JavaScript Minification**: Minify JavaScript in production builds
5. **CSS Optimization**: Use CSS minification tools

## Future Enhancements

- [ ] Implement proper user authentication (username/password validation)
- [ ] Add real-time notifications using WebSockets
- [ ] Implement service order management system
- [ ] Add payment processing integration
- [ ] Implement automated backups
- [ ] Add two-factor authentication
- [ ] Create mobile app (React Native)
- [ ] Implement advanced analytics with visualization libraries

## Contributing

Contributions are welcome! Please follow these guidelines:
1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit a pull request

## License

This project is provided as-is for educational purposes.

## Support

For issues or questions, please check:
- Docker logs: `docker-compose logs backend`
- Browser console: F12 -> Console tab
- Application logs in container: `/usr/local/tomcat/logs/catalina.out`

## Version History

### v2.0 (Current)
- Migrated from JSP to modern HTML5 frontend
- Improved responsive design
- Enhanced API structure
- Docker containerization

### v1.0
- Initial JSP-based implementation
- Basic machine management
- Session tracking

---

**Last Updated**: May 4, 2026
**Developed for HomePS Gaming Café**

