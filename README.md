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
5. Navigate to the location of the required JAR files (the jars folder in the root project directory).
6. Select all necessary JARs and click **OK**.
7. Ensure the added libraries are marked for all modules in the project.
8. Click **Apply** and **OK** to save the changes.

> **Tip:** If dependencies are still not resolved, try **File** > **Invalidate Caches / Restart** > **Invalidate and Restart**.

## 2. Running Tests in IntelliJ

Once dependencies are set up, you can run the tests using the built-in test runner.

### Steps to Run Tests:
1. Click next to the Run symbol to open Run Configurations
2. Open "All Configurations"
3. Select the Run type specified in each test and follow the steps


When running the tests, client 4 will always be considered to be the owner of ISTCoin, and therefore the owner of the Blacklist.
## 3. Regular test
This tests aims to show a regular execution of both the consensus and EVM, with no Byzantine Behaviour.

### Steps:
1. Run the **Normal Run** configuration.
2.  Go to Client with Id 4.
3. Check your own balance by running ``7``.
4. Verify that consensus runs correctly on the servers, client waits for f+1 consensus replies, as well as f+1 EVM replies.
5. Transfer funds to another client by running ``4 1 500`` (this will transfer 500 ISTCoin to address binded with 1)
6. Verify that this transaction is executed
7. Go to Client with Id 5.
8. Verify that you have received the funds with ``7``.
9. Go back to Client 4 and increase allowance for Client 5 with ``5 1 120``
10. Go back to Client 5 and transfer yourself 100 from Client 4 with ``10 0 1 100``
11. Verify your balance with ``7``
12. Try to transfer 40 more ISTCoin, and check that an error is returned with  ``10 0 1 40``
13. Go back to Client 4 and blacklist Client 5 with ``1 1``
14. Verify that all these movements did indeed change your balance with ``7``
14. Verify that he is blacklisted with ``3 1``
15. Go back to client 5 and try to transfer funds to Client 4 with ``4 0 10`` and verify the error


## 4. Byzantine Consensus tests

### 4.1 A non leader tries to initiate consensus
1. Run the "

### Troubleshooting Test Issues
- Ensure all dependencies are correctly added and recognized.
- If tests are not running, try rebuilding the project (`Build` > `Rebuild Project`).
- Check the **Run/Debug Configurations** to ensure proper test settings.

---

This guide should help you get the project running smoothly in IntelliJ. If you encounter issues, make sure to check the **Event Log** (`Alt + 0`) for error messages.

