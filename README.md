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
This test aims to show a regular execution of both the consensus and EVM, with no Byzantine Behaviour.

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
15. Verify that he is blacklisted with ``3 1``
16. Go back to client 5 and try to transfer funds to Client 4 with ``4 0 10`` and verify the error
17. Now look at the file that persists the Blockhain.json, to see all these blocks with transactions.
18. You can verify that each block only held one transaction, because we either wait for 5 seconds, or 1 transaction before outputting the block.
19. Now, sending 4 or 5 ``7`` in quick succession. Take in mind this will break the readability of the command line a bit due to duplicated messages.
20. However, if you check Blockchain.json again, you can see all those transactions were appended to the same block.
21. It's best to stop running the servers now, as well as the clients to not overload your CPU. 
22. This concludes the test.

## 4. Byzantine Consensus tests

### 4.1 A non leader tries to initiate consensus
This test aims to show how the system protects itself from a byzantine node trying to initiate consensus when it is not a leader.
All nodes ignore INIT messages that do not come from the pre-determined leader, node 0.

1. Run the " Byzantine Behaviour 0" run configuration.
2. This will start server 1 with byzantine properties, such as trying to start a consensus without being the leader.
3. We made this test in a way where no transactions need to be sent for the fake leader to try to start consensus. Our goal was to demonstrate that nodes only accept INIT messages from the Leader.
4. After about 10 seconds, Server 1 will try to start consensus
4. Go to server 1 (the byzantine one), and check that he failed to start a consensus (it should have printed something like "**BYZANTINE PROCESS COULDN'T START CONSENSUS WHEN NOT LEADER"**)

### 4.2 A byzantine node tries to fake a received transaction in the state message
This test aim to show how the system protects itself from a byzantine node trying to falsify Transactions when responding to the state request.

We have two lines of defense: a correct leader is dropping invalid State messages, and all correct nodes are checking all the COLLECTED entries for validity issues


1. Run the "Byzantine Behaviour 1" run configuration
2. Go to Client 4 and check your own balance with ``7``
3. Verify that server 1 tries to falsify a transaction in the state message 
4. The nodes, as well as the leader, should have not accepted this message when processing the COLLECTED, so look for a print Server 1 that looks like this: "BYZANTINE STATE MSG WAS NOT ACCEPTED BY LEADER"
### 4.3 A byzantine node tries to fake a value when proposing values in the Write Phase

This test aims to show how the system protects itself from a byzantine node trying to insert falsified values during the WRITE phase.

Due to the verifications each node does when processing WRITE messages, these falsified values get dropped and are not repeated in the ACCEPT phase.

1. Run the "Byzantine Behaviour 2" run configuration 
2. Go to client 4 and send a regular transaction to check your balance with ``7``
3. Node 1 will go through the consensus normally, and will eventually try to insert a falsified and never seen before value in its Write message
4. All other nodes will disregard this message, as seen by the presence of the following print message in Node 1: "BYZANTINE WRITE MSG WAS NOT ACCEPTED"

## Byzantine client testing

Most byzantine client behaviour is difficult to test in the conditions of our application, as we only have two clients.

Furthermore, most attacks on smart contracts are well protected by our implementation since it took inspiration to the OpenZeppelin ERC-20 implementation.

However, we can try to simulate an impersonation attack by a client, pretendind to be another EOA, all the while only having access to its own Private Key.

### Steps
1. Run a "Normal Run"
2. Go to Client 5 and Execute a fake transaction, trying to read another user's IST Coin balance by running ``11``.
3. Verify that the EVM recognized that the transaction signature couldn't be validated.


### Troubleshooting Test Issues
- Ensure all dependencies are correctly added and recognized.
- If tests are not running, try rebuilding the project (`Build` > `Rebuild Project`).
- Check the **Run/Debug Configurations** to ensure proper test settings.

---

This guide should help you get the project running smoothly in IntelliJ. If you encounter issues, make sure to check the **Event Log** (`Alt + 0`) for error messages.

