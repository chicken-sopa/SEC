# SEC Project 1 tests
This project includes a set of tests that can be executed utilizing the various python scripts described below.
It is essential to **COMPILE** the project using Maven before running the tests.
---

## Steps to execute the tests

### 1. Pre-requisites
Have these tools installed:
- Java Development Kit (JDK) 8 or superior.
- [Maven](https://maven.apache.org/) for compilation.
- [Python 3](https://www.python.org/) for script execution.

### 2. Compilar o Projeto
Before executing the tests, it's crucial to compile the project:

```bash
mvn clean compile
```


---

### 3. "Normal" program flow

Test1 covers a normal flow. In order to execute this, simply run the following command below.

```bash
python test1.py
```
This test executes a normal execution of the consensus algorithm. Unfortunately

> **Note:** Before executing the script make sure you give the executable permissions to open multiple terminal windows.

---

## Environment

This Python script was designed to be executed **Windows**, **Linux** and **macOS**. Make sure that:
- In Linux:  **gnome-terminal** is installed.
 ```bash
sudo apt install gnome-terminal
```
- On macOS: The system can open the **terminal** using `osascript`.

---

