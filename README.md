# Log Analyzer CLI Tool

A command-line tool for analyzing server log files with filtering, searching, and statistical analysis capabilities.

## Features

### Basic Operations
- **Line Counting**: Display total number of lines in log file
- **Log Level Filtering**: Filter by log levels (INFO, ERROR, WARN, DEBUG)
- **Text Search**: Search for specific text (case-insensitive)
- **Date Range Filtering**: Filter logs within specific date ranges
- **Regex Pattern Matching**: Advanced pattern-based filtering (case-insensitive)

### Statistical Analysis
- **Log Level Statistics**: Count and percentage breakdown by level
- **Time-based Analysis**: Hourly or daily distribution of logs
- **Top N Messages**: Most frequently occurring log messages
- **Summary Report**: Comprehensive overview with health indicators

## Installation

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Build
```bash
  mvn clean package
```

## Usage

### Basic Syntax
```bash
  java -jar target/log-analyzer-1.0-SNAPSHOT.jar <logfile> [OPTIONS]
```

### Options

#### Display Options
- `-c, --count` - Display total line count
- `--stats` - Show log level statistics
- `--time-stats <hourly|daily>` - Show time-based statistics
- `--top <N>` - Show top N most frequent messages
- `--summary` - Show comprehensive summary report

#### Filter Options
- `-l, --level <LEVEL>` - Filter by log level (INFO, ERROR, WARN, DEBUG)
- `-s, --search <TEXT>` - Search for text (case-insensitive)
- `-r, --regex <PATTERN>` - Search using regex pattern (case-insensitive)
- `--from <DATE>` - Filter logs from date (YYYY-MM-DD)
- `--to <DATE>` - Filter logs until date (YYYY-MM-DD)

#### Help
- `-h, --help` - Show help message
- `-V, --version` - Show version information

## Examples

### Basic Usage
```bash
# Count total lines
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --count

# Show all ERROR logs
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --level ERROR

# Search for "database" in logs
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --search "database"

# Filter by date range
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --from 2024-12-01 --to 2024-12-10

# Search with regex pattern
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --regex "error|exception|fail"
```

### Statistical Analysis
```bash
# Log level statistics
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --stats

# Daily distribution
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --time-stats daily

# Hourly distribution
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --time-stats hourly

# Top 10 most common messages
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --top 10

# Comprehensive summary
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --summary
```

### Combined Filters
```bash
# ERROR logs statistics
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --level ERROR --stats

# Daily distribution for date range
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --from 2024-12-01 --to 2024-12-10 --time-stats daily

# Top messages matching pattern
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --regex "database" --top 5

# Summary of ERROR logs only
java -jar target/log-analyzer-1.0-SNAPSHOT.jar server.log --level ERROR --summary
```

## Log Format

The tool expects log files in this format:
```
YYYY-MM-DD HH:MM:SS LEVEL Message content
```

Example:
```
2024-12-09 10:15:23 ERROR Database connection timeout
2024-12-09 10:15:25 WARN Retrying database connection
2024-12-09 10:15:30 INFO Database connected successfully
```

## Technologies Used

- **Java 21**: Core programming language
- **Maven**: Build and dependency management
- **Picocli**: Command-line interface framework
- **BufferedReader**: Efficient file processing

## Project Structure
```
log-analyzer/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/woo/loganalyzer/
│       │       └── LogAnalyzerApp.java
│       └── resources/
├── test.log
├── pom.xml
└── README.md
```

## Performance

- Efficiently processes large log files using BufferedReader
- Case-insensitive regex matching by default
- Memory-efficient streaming approach
- Handles files with millions of lines

## Future Enhancements

Potential features for future versions:
- Export statistics to CSV/JSON
- Real-time log monitoring
- Custom log format configuration
- Multi-file analysis
- Graphical visualization

## License

This project is for educational and portfolio purposes.

## Author

Woo Seok Lee - 2024