# Project Setup and Running Tests in IntelliJ

This guide walks you through setting up the project dependencies and running tests in IntelliJ IDEA.

Contrary to last delivery, we will now run the tests directly through InteliJ as opposed to the python script we used before to run all necessary servers and applications.

This is because of dependdency issues that came up that could not be solved through maven.

## 1. Setting Up Dependencies

To ensure all modules have access to the necessary dependencies, you need to add a few JARs as libraries at the root of the project.

### Steps to Add Dependencies:
1. Open **IntelliJ IDEA** and load the project.
2. Navigate to **File** > **Project Structure** (`Ctrl + Alt + Shift + S` on Windows/Linux or `Cmd + ;` on macOS).
3. In the left panel, go to **Libraries**.
4. Click on the **+** button and select **Java**.
5. Navigate to the location of the required JAR files.
6. Select all necessary JARs and click **OK**.
7. Ensure the added libraries are marked for all modules in the project.
8. Click **Apply** and **OK** to save the changes.

> **Tip:** If dependencies are still not resolved, try **File** > **Invalidate Caches / Restart** > **Invalidate and Restart**.

## 2. Running Tests in IntelliJ

Once dependencies are set up, you can run the tests using the built-in test runner.

### Steps to Run Tests:
1. Open **IntelliJ IDEA** and navigate to the `test` directory.
2. Right-click the test class or package you want to run.
3. Select **Run 'TestClassName'**.
4. Alternatively, you can run all tests by opening the **Run** menu and selecting **Run All Tests**.
5. View the test results in the **Run** or **Test Results** window.


When running the tests, client 4 will always be considered to be the owner of ISTCoin, and therefore the owner of the Blacklist.
## 3. Regular test
This tests aims to show a regular execution of both the consensus and EVM, with no Byzantine Behaviour.

### Steps:
1. Run the Normal Run configuration.
2. 

### Troubleshooting Test Issues
- Ensure all dependencies are correctly added and recognized.
- If tests are not running, try rebuilding the project (`Build` > `Rebuild Project`).
- Check the **Run/Debug Configurations** to ensure proper test settings.

---

This guide should help you get the project running smoothly in IntelliJ. If you encounter issues, make sure to check the **Event Log** (`Alt + 0`) for error messages.

