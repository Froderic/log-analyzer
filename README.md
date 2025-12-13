# Log Analyzer

A powerful command-line tool for analyzing and filtering log files with support for statistics, pattern matching, and CSV export capabilities.

## Features

- **Flexible Filtering**: Filter logs by date range, log level, keyword search, or regex patterns
- **Statistical Analysis**: Generate comprehensive statistics on log levels, time-based patterns, and message frequencies
- **CSV Export**: Export analysis results to CSV format for further processing
- **Combined Filters**: Apply multiple filters simultaneously for precise log analysis
- **Performance**: Efficient file processing using BufferedReader for large log files

## Prerequisites

- Java 21 or higher
- Maven 3.6+

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Froderic/log-analyzer.git
cd log-analyzer
```

2. Build the project:
```bash
mvn clean package
```

3. The executable JAR will be created in the `target` directory.

## Usage

### Basic Syntax
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar [OPTIONS] <logfile>
```

### Command-Line Options

| Option | Description |
|--------|-------------|
| `-c, --count` | Display total line count |
| `-l, --level <LEVEL>` | Filter by log level (INFO, ERROR, WARN, DEBUG) |
| `-s, --search <TEXT>` | Search for lines containing specified text |
| `--from <DATE>` | Filter logs from this date (format: YYYY-MM-DD) |
| `--to <DATE>` | Filter logs until this date (format: YYYY-MM-DD) |
| `-r, --regex <PATTERN>` | Search using regex pattern |
| `--stats` | Show log level statistics (count and percentage) |
| `--time-stats <MODE>` | Show time-based statistics (hourly or daily) |
| `--top <N>` | Show top N most frequent log messages |
| `--summary` | Show comprehensive summary report |
| `--export <FILE>` | Export results to CSV file |
| `-h, --help` | Display help information |
| `-V, --version` | Display version information |

## Examples

### Basic Operations

**Count total lines:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -c test.log
```

**Filter by log level:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -l ERROR test.log
```

**Search for specific text:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -s "connection failed" test.log
```

### Date Range Filtering

**Filter logs within a date range:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --from 2024-12-01 --to 2024-12-10 test.log
```

**Combine date filter with log level:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --from 2024-12-09 -l ERROR test.log
```

### Pattern Matching

**Search using regex (case-insensitive):**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -r "user\s+\d+" test.log
```

**Find lines with specific error codes:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -r "ERROR-[0-9]{3}" test.log
```

### Statistical Analysis

**Generate log level statistics:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --stats test.log
```

**Analyze hourly patterns:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --time-stats hourly test.log
```

**Analyze daily patterns:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --time-stats daily test.log
```

**Find top 10 most frequent messages:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --top 10 test.log
```

**Generate comprehensive summary:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --summary test.log
```

### CSV Export

**Export statistics to CSV:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --stats --export stats.csv test.log
```

**Export time-based analysis:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --time-stats daily --export daily-stats.csv test.log
```

**Export top messages:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --top 20 --export top-messages.csv test.log
```

### Combined Filters

**Analyze ERROR logs from specific date range:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --from 2024-12-09 --to 2024-12-11 -l ERROR --stats test.log
```

**Search for pattern in specific date range and export:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --from 2024-12-09 -r "timeout" --stats --export timeout-stats.csv test.log
```

**Filter, analyze, and export in one command:**
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar -l WARN --time-stats hourly --export warn-hourly.csv test.log
```

## Sample Output

### Statistics Display
```
============================================================
LOG LEVEL STATISTICS
============================================================
Level           Count           Percentage     
------------------------------------------------------------
ERROR           45              22.50%
INFO            120             60.00%
WARN            25              12.50%
DEBUG           10              5.00%
------------------------------------------------------------
TOTAL           200            
============================================================
```

### Time-Based Analysis
```
============================================================
TIME-BASED STATISTICS (DAILY)
============================================================
Time Period              Count           Percentage     
------------------------------------------------------------
2024-12-09              85              42.50%
2024-12-10              75              37.50%
2024-12-11              40              20.00%
------------------------------------------------------------
TOTAL                   200            
============================================================
```

## Expected Log Format

The tool expects log files in the following format:
```
YYYY-MM-DD HH:MM:SS LEVEL Message content
```

Example:
```
2024-12-09 10:00:01 INFO Application started successfully
2024-12-09 10:00:15 ERROR Connection timeout to database
2024-12-09 10:00:30 WARN Retry attempt 3 of 5
```

## Project Structure
```
log-analyzer/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/woo/loganalyzer/
│   │           ├── LogAnalyzerApp.java
│   │           └── CSVExporter.java
│   └── test/
│       └── java/
├── pom.xml
├── README.md
└── test.log (sample log file)
```

## Dependencies

- **Picocli** (4.7.5): Command-line argument parsing
- **JUnit** (5.10.1): Unit testing framework

## Development

### Running Tests
```bash
mvn test
```

### Building from Source
```bash
mvn clean compile
mvn package
```

## Testing

This project uses manual testing with sample log files to verify functionality.

### Test Log File

A sample `test.log` file is included with various log levels and timestamps for testing:
```
2024-12-05 09:30:12 INFO Server initialization started
2024-12-05 09:30:15 INFO Loading configuration files
2024-12-06 10:15:23 ERROR Configuration file not found
2024-12-06 10:15:25 WARN Using default configuration
2024-12-07 11:20:01 INFO User registration: alice
2024-12-07 11:20:15 INFO User login: alice
2024-12-08 14:45:33 ERROR Database connection timeout
2024-12-08 14:45:35 WARN Retrying database connection (attempt 1/3)
2024-12-09 10:00:01 INFO Server started on port 8080
2024-12-09 10:00:15 INFO User login: user123
2024-12-09 10:01:23 ERROR Database connection failed
2024-12-09 10:01:25 WARN Retrying database connection
2024-12-09 10:01:30 INFO Database connected successfully
```

### Manual Test Cases

**Basic Functionality:**
- ✓ Line counting (`-c`)
- ✓ Log level filtering (`-l ERROR`, `-l INFO`, etc.)
- ✓ Text search (`-s "database"`)
- ✓ Regex pattern matching (`-r "user\d+"`)

**Date Filtering:**
- ✓ Single date filtering (`--from 2024-12-09`)
- ✓ Date range filtering (`--from 2024-12-07 --to 2024-12-09`)
- ✓ Combined with other filters

**Statistical Analysis:**
- ✓ Log level statistics (`--stats`)
- ✓ Hourly time analysis (`--time-stats hourly`)
- ✓ Daily time analysis (`--time-stats daily`)
- ✓ Top message frequency (`--top 5`)
- ✓ Comprehensive summary (`--summary`)

**CSV Export:**
- ✓ Export statistics (`--stats --export stats.csv`)
- ✓ Export time analysis (`--time-stats daily --export daily.csv`)
- ✓ Export top messages (`--top 10 --export messages.csv`)
- ✓ Proper CSV formatting with escaped commas

**Combined Filters:**
- ✓ Multiple filters simultaneously
- ✓ Filters with statistics
- ✓ Filters with CSV export

**Error Handling:**
- ✓ File not found
- ✓ Invalid date format
- ✓ Invalid time-stats mode
- ✓ Summary with CSV export (proper error message)

## Technical Details

- **Architecture**: Single-pass log processing with stream-based filtering
- **File Processing**: Uses BufferedReader for memory-efficient handling of large files
- **Pattern Matching**: Java regex with case-insensitive matching
- **Date Handling**: ISO 8601 format (YYYY-MM-DD) for consistent parsing
- **CSV Export**: Proper escaping of special characters (commas, quotes)

## Limitations

- Summary reports (`--summary`) cannot be exported to CSV due to their comprehensive multi-section format
- Log format must follow the expected timestamp structure for date filtering
- Very large files (>1GB) may require increased JVM heap size

## Future Enhancements

- Support for multiple log file formats
- Real-time log monitoring mode
- Interactive TUI (Text User Interface)
- Performance metrics and benchmarking

## License

This project is created for educational purposes as part of a software engineering portfolio.

## Author

Woo Seok - [GitHub Profile](https://github.com/Froderic)

## Acknowledgments

Built as part of a structured career transition plan to demonstrate proficiency in:
- Java development
- Command-line tool design
- File I/O and data processing
- Maven build management
- Git version control