# Morula Bank

JavaFX and H2 implementation of the OOAD banking assignment. The project is a separate deliverable copy and does not change the original project in Downloads.

## Requirements

- JDK 17 or newer
- Maven, or the included Maven wrapper
- VS Code with the Extension Pack for Java
- Gluon Scene Builder for editing the FXML screens

## Run

From this directory in the VS Code terminal:

```powershell
./mvnw.cmd javafx:run
```

The H2 file database is created under `data/` on first launch and is seeded with ten sample customers and ten accounts. Demo login: `user1` / `pass1`. Sample accounts include `ACC001` (savings), `ACC002` (investment), and `ACC010` (cheque).

Run the automated test suite with:

```powershell
./mvnw.cmd test
```

## Main classes

- `model/`: `Bank`, `Customer`, `Individual`, `Company`, `Account`, `SavingsAccount`, `InvestmentAccount`, `ChequeAccount`, and the `Interest` interface.
- `controller/`: application-layer validation and coordination for login, registration, accounts, and transactions.
- `dao/`: JDBC persistence, password hashing, customer/account CRUD, transaction history, and monthly interest posting.
- `service/InterestScheduler`: runs the monthly interest job at month end.
- `view/` and `resources/com/bank/view/`: JavaFX boundary controllers and Scene Builder FXML files.

For the class-to-code map, demo walkthrough, and assignment checklist, see the separate `Banking-System-Guide.md` deliverable.
