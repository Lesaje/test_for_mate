# Task 1: SQL Queries

This task includes SQL queries implemented in Scala to extract data from the following tables: `users`, `leads`, `domains`, and `courses`.

## Subtasks

- **1.1** Number of created leads per week grouped by course type
- **1.2** Number of WON flex leads per country created from `01.01.2024`
- **1.3** User email, lead ID, and lost reason for users who lost flex leads from `01.07.2024`

## Project Structure

- **Main logic**: Implemented in Scala
- **Tests**: Located in the folder `test/task1`

## Running Tests

To run the tests:

```bash
sbt test
```


# Task 2: Web Scraper

This task includes a Selenium-based web scraper that collects course data from [mate.academy](https://mate.academy).

## Extracted Data

- Course name
- Short description
- Available formats (full-time / flex)
- Duration for each format
- Modules and their topics

## Project Structure

- **Scraper logic**: `task2/Scraper.scala`
- **Models**: `Course`, `Module`, `CourseFormat`

## How to Run

1. Install Chrome and ChromeDriver
2. Make sure `chromedriver` is in your system `PATH`
3. In SBT console:

```scala
import task2.Scraper
val courses = Scraper.scrapeAllCourses()
courses.foreach(_.prettyPrint())
