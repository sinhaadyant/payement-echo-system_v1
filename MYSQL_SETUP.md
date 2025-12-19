# MySQL Setup Guide

This guide will help you set up MySQL for the Payment Echo System.

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher (or use Docker)

## Quick Start with Docker Compose

The easiest way to get started is using Docker Compose:

```bash
# Start MySQL and the application
docker-compose up -d

# Check MySQL logs
docker-compose logs mysql

# Stop everything
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

The MySQL container will:
- Create database `payment_echo_db` automatically
- Use root password: `root` (change in production!)
- Expose port `3306` on localhost

## Local MySQL Installation

### 1. Install MySQL

**macOS (using Homebrew):**
```bash
brew install mysql
brew services start mysql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

**Windows:**
Download and install from [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)

### 2. Create Database

Connect to MySQL:
```bash
mysql -u root -p
```

Create database:
```sql
CREATE DATABASE payment_echo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Application

Update `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/payment_echo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 4. Run Application

```bash
./gradlew bootRun
```

The application will automatically:
- Create tables if they don't exist
- Update schema if needed (via Hibernate DDL auto-update)
- Initialize sample data

## Database Connection Details

### Default Configuration

- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `payment_echo_db`
- **Username**: `root` (change in production!)
- **Password**: `root` (change in production!)

### Connection String Format

```
jdbc:mysql://localhost:3306/payment_echo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

## Production Configuration

For production, update `application.properties` with:

1. **Strong password**:
   ```properties
   spring.datasource.password=strong_secure_password_here
   ```

2. **SSL enabled**:
   ```properties
   spring.datasource.url=jdbc:mysql://your-host:3306/payment_echo_db?useSSL=true&requireSSL=true&serverTimezone=UTC
   ```

3. **Connection pool tuning**:
   ```properties
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=10
   ```

4. **Use environment variables** (recommended):
   ```properties
   spring.datasource.url=${DATABASE_URL}
   spring.datasource.username=${DATABASE_USERNAME}
   spring.datasource.password=${DATABASE_PASSWORD}
   ```

## Database Management Tools

### MySQL Workbench
- Download: https://dev.mysql.com/downloads/workbench/
- Free GUI tool for MySQL

### DBeaver
- Download: https://dbeaver.io/
- Free universal database tool

### Command Line
```bash
# Connect to MySQL
mysql -u root -p payment_echo_db

# Show tables
SHOW TABLES;

# Describe a table
DESCRIBE payments;

# View data
SELECT * FROM payments LIMIT 10;
```

## Troubleshooting

### Connection Refused

**Problem**: `Communications link failure`

**Solutions**:
1. Check if MySQL is running:
   ```bash
   # macOS/Linux
   brew services list  # or systemctl status mysql
   
   # Docker
   docker-compose ps
   ```

2. Verify port 3306 is accessible:
   ```bash
   telnet localhost 3306
   ```

### Access Denied

**Problem**: `Access denied for user 'root'@'localhost'`

**Solutions**:
1. Reset MySQL root password:
   ```bash
   mysql -u root -p
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
   ```

2. Check username/password in `application.properties`

### Timezone Issues

**Problem**: `The server time zone value 'XYZ' is unrecognized`

**Solution**: Add `serverTimezone=UTC` to connection URL (already included in default config)

### Database Doesn't Exist

**Problem**: `Unknown database 'payment_echo_db'`

**Solution**: The connection URL includes `createDatabaseIfNotExist=true`, but if it still fails:
```sql
CREATE DATABASE payment_echo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Schema Management

The application uses Hibernate's `ddl-auto=update` which:
- Creates tables automatically on first run
- Updates schema when entities change
- **Does NOT** delete data when entities are removed

For production, consider:
- Using Flyway or Liquibase for migrations
- Setting `spring.jpa.hibernate.ddl-auto=validate` (doesn't auto-create/update)

## Testing

Tests still use H2 in-memory database (configured in `application-test.properties`):
- Faster test execution
- No MySQL setup required for tests
- Isolated test environment

## Backup and Restore

### Backup
```bash
mysqldump -u root -p payment_echo_db > backup.sql
```

### Restore
```bash
mysql -u root -p payment_echo_db < backup.sql
```

## Performance Tips

1. **Add indexes** on frequently queried columns:
   ```sql
   CREATE INDEX idx_payment_status ON payments(status);
   CREATE INDEX idx_payment_created_at ON payments(created_at);
   ```

2. **Monitor slow queries**:
   ```sql
   SET GLOBAL slow_query_log = 'ON';
   SET GLOBAL long_query_time = 2;
   ```

3. **Optimize connection pool** (already configured in `application.properties`)

## Security Best Practices

1. ✅ Use strong passwords
2. ✅ Create dedicated database user (not root)
3. ✅ Enable SSL in production
4. ✅ Restrict network access
5. ✅ Regular backups
6. ✅ Keep MySQL updated

## Next Steps

- [ ] Set up database migrations (Flyway/Liquibase)
- [ ] Configure connection pooling
- [ ] Set up database backups
- [ ] Add database monitoring
- [ ] Create production database user

---

**Need Help?** Check the main [README.md](README.md) for more information.

