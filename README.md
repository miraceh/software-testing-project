# 🛠️ Makefile Targets Overview

This project uses `make` to automate build, testing, coverage, and mutation testing for date conversion logic and Quadratic solver based on **ACTS** and **Z3-generated** case inputs. Below are the key targets:

---

## 📦 Build Targets

| Target       | Description                                    |
|--------------|------------------------------------------------|
| `make build` | Compile all Java source and test files         |
| `make clean` | Clean all generated `.class`, reports, and temp files |

---

## 🔁 Test Case Generation

| Target       | Description                                |
|--------------|--------------------------------------------|
| `make actscase` | Generate ACTS pairwise test cases from `ACTS/dateGen.txt` |
| `make z3case`   | Generate Z3 test cases using Python + Z3, output to `genCase/z3/` |

---

## 🧪 Testing

| Target        | Description                                    |
|---------------|------------------------------------------------|
| `make actstest` | Run JUnit tests using generated ACTS inputs |
| `make z3test` | Run JUnit tests using Z3-generated inputs     |

---

## 🧬 Mutation Testing (via PIT)

| Target            | Description                                        |
|-------------------|----------------------------------------------------|
| `make actsmutation` | Run PIT mutation testing on ACTS-based test suite |
| `make actsmutationBoost` | Mutation testing with `ActsMutationScore` class |
| `make z3mutation` | Run PIT mutation testing on Z3-based test suite   |
| `make z3mutationBoost` | Mutation testing with `z3MutationScore` boost logic |
| `make actsmutation-report` | Open ACTS mutation HTML report in browser |
| `make actsmutation-boost-report` | Open ACTS + Boost mutation report |
| `make z3mutation-report` | Open Z3 mutation HTML report in browser     |
| `make z3mutation-boost-report` | Open Z3 + Boost mutation report     |

---

## 📊 Code Coverage (via JaCoCo)

| Target              | Description                                       |
|---------------------|---------------------------------------------------|
| `make actscoverage` | Generate JaCoCo coverage for ACTS-based inputs    |
| `make actscoverageBoost` | Run coverage with `ActsCoverageScore` boost |
| `make z3coverage`   | Generate JaCoCo coverage for Z3-based inputs      |
| `make z3coverageBoost` | Run coverage with `z3CoverageScore` boost     |
| `make actscoverage-report` | Open ACTS coverage report in browser      |
| `make actscoverage-boost-report` | Open ACTS + Boost coverage report  |
| `make z3coverage-report` | Open Z3 coverage report in browser           |
| `make z3coverage-boost-report` | Open Z3 + Boost coverage report      |

---

### 📂 Generated Outputs

- Test cases: `genCase/acts/`, `genCase/z3/`
- Coverage reports: `jacoco-report-*`
- Mutation reports: `pit-report-*`

---

## 📎 Notes

- **ACTS jar file** (`acts_3.3.jar`) is **not publicly hosted**. You need to contact the original authors or refer to official resources. Try searching **"ACTS NIST combinatorial test generation"** on Google for more information.

- **Makefile must be run inside the appropriate project directory**, such as `Quadratic-Equation-Solver/` or `Convert-dates-between-different-formats/`, depending on your current test target.

- **Report viewing targets** (e.g., `make xxxreport`) use **hardcoded paths** (such as PowerShell on Windows). These may not work on other platforms or customized environments. You may need to modify those lines for your system.
