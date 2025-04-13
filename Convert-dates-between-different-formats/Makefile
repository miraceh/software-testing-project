.PHONY: actscase clean build test all

JUNIT_JAR=../lib/junit-platform-console-standalone-1.10.2.jar
BUILD_DIR=build
CLASSPATH=$(JUNIT_JAR):$(BUILD_DIR)

SOURCES=DateHelper.java \
        test/TestCaseData.java \
        test/DataHelperTest.java

# Generate ACTS cases
actscase:
	@echo "▶ Generating ACTS cases with dateGen.txt..."
	@java -jar ACTS/acts_3.3.jar ACTS/dateGen.txt ACTS/actsout.case || (echo "❌ ACTS generation failed." && exit 1)

	@echo "📂 Parsing ACTS output to input files..."
	@python3 ACTS/parserOut.py || (echo "❌ Python parse failed." && exit 1)

	@echo "✅ ACTS test cases generated in genCase/acts/"

# Compile Java source and test files
build:
	@echo "Compiling Java sources and tests..."
	@mkdir -p $(BUILD_DIR)
	@javac -cp "../lib/*" -d $(BUILD_DIR) *.java test/*.java || (echo "❌ Compilation failed." && exit 1)
	@echo "✅ Compilation succeeded."

# Run JUnit tests
actstest: actscase build
	@echo "▶ Running JUnit tests with ACTS input files..."
	@java -Dsource=acts \
		-jar $(JUNIT_JAR) \
		-cp $(BUILD_DIR) \
		--scan-class-path || (echo "❌ Some ACTS tests failed." && exit 1)
	@echo "✅ All ACTS-based tests completed."

# Mutation Testing (ACTS)
actsmutate: build
	@echo "Running PIT mutation testing for ACTS input..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-acts \
		--targetClasses DateHelper \
		--targetTests DataHelperTest \
		--sourceDirs . \
		--jvmArgs="-Dsource=acts" || (echo "❌ PITest (ACTS) failed." && exit 1)
	@echo "✅ Mutation testing (ACTS) completed. Report at pit-report-acts/index.html"

build/ActsMutationScore.class: ACTS/ActsMutationScore.java
	@echo "📦 Compiling ACTS mutationScore.java..."
	@javac -cp "../lib/*:build" -d build ACTS/ActsMutationScore.java || (echo "❌ ACTS mutationScore compilation failed." && exit 1)

actsmutationBoost: build build/ActsMutationScore.class
	@echo "Running PIT mutation testing for ACTS + Boost..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-acts-boost \
		--targetClasses DateHelper \
		--targetTests DataHelperTest,ActsMutationScore \
		--sourceDirs . \
		--jvmArgs="-Dsource=acts" || (echo "❌ PITest (ACTS + Boost) failed." && exit 1)
	@echo "✅ Mutation testing (ACTS + Boost) completed. Report at pit-report-acts-boost/index.html"

# Run Jacoco test coverage for ACTS input
actscoverage: clean actscase build
	@echo "▶ Running tests with Jacoco agent (ACTS input)..."
	@java -javaagent:../lib/jacocoagent.jar=destfile=jacoco-acts.exec \
		-Dsource=acts \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || echo "⚠️ Some ACTS tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (ACTS)..."
	@java -jar ../lib/jacococli.jar report jacoco-acts.exec \
		--sourcefiles . \
		--classfiles build/DateHelper.class \
		--html jacoco-report-acts \
		--name "DateHelper Coverage (ACTS)"
	@echo "✅ Report generated at jacoco-report-acts/index.html"

build/ActsCoverageScore.class: ACTS/ActsCoverageScore.java
	@echo "📦 Compiling ACTS coverageScore.java..."
	@javac -cp "../lib/*:build" -d build ACTS/ActsCoverageScore.java || (echo "❌ ACTS coverageScore compilation failed." && exit 1)

actscoverageBoost: actscase build build/ActsCoverageScore.class
	@echo "▶ Running tests with Jacoco agent (ACTS + Boost)..."
	@java -javaagent:../lib/jacocoagent.jar=destfile=jacoco-acts-boost.exec \
		-Dsource=acts \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		--class-path build \
		--select-class=DataHelperTest \
		--select-class=ActsCoverageScore || echo "⚠️ Some ACTS tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (ACTS + Boost)..."
	@java -jar ../lib/jacococli.jar report jacoco-acts-boost.exec \
		--sourcefiles . \
		--classfiles build/DateHelper.class \
		--html jacoco-report-acts-boost \
		--name "DateConvert Coverage (ACTS + Boost)"
	@echo "✅ Report generated at jacoco-report-acts-boost/index.html"

# Open PITest HTML report for ACTS input
actsmutation-report: actsmutate
	@echo "📂 Opening PIT mutation report (ACTS) in Edge..."
	@if [ -e pit-report-acts/index.html ]; then \
		powershell.exe /c start pit-report-acts/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make actsmutate' first."; \
	fi

# Open Jacoco HTML report for ACTS input
actscoverage-report: actscoverage
	@echo "📂 Opening Jacoco coverage report (ACTS) in Edge..."
	@if [ -e jacoco-report-acts/index.html ]; then \
		powershell.exe /c start jacoco-report-acts/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make actscoverage' first."; \
	fi

actscoverage-boost-report: actscoverageBoost
	@echo "📂 Opening Jacoco coverage report (ACTS + Boost) in Edge..."
	@if [ -e jacoco-report-acts-boost/index.html ]; then \
		powershell.exe /c start jacoco-report-acts-boost/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make actscoverageBoost' first."; \
	fi

actsmutation-boost-report: actsmutationBoost
	@echo "📂 Opening PIT mutation report (ACTS + Boost) in Edge..."
	@if [ -e pit-report-acts-boost/index.html ]; then \
		powershell.exe /c start pit-report-acts-boost/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make actsmutationBoost' first."; \
	fi

# Clean all build and output files
clean:
	@echo "🧹 Cleaning up all generated files and reports..."
	@rm -f ACTS/actsout.case
	@rm -f genCase/acts/*
	@rm -f genCase/z3/*
	@rm -f $(BUILD_DIR)/*
	@rm -f jacoco-*.exec
	@rm -rf jacoco-report-z3 jacoco-report-z3-boost jacoco-report-acts jacoco-report-acts-boost
	@rm -rf pit-report-z3 pit-report-z3-boost pit-report-acts pit-report-acts-boost
	@echo "✅ Clean complete."

# Shortcut targets
acts: actscase actstest
all: acts

z3case:
	@echo "▶ Generating Z3 cases with gencase.py..."
	@python3 zSolver/gencase.py || (echo "❌ gencase.py failed." && exit 1)

	@echo "📂 Parsing Z3 output to input files with parserOut.py..."
	@python3 zSolver/parserOut.py || (echo "❌ parserOut.py failed." && exit 1)

	@echo "✅ Z3 test cases generated in genCase/z3/"

# Run JUnit tests for Z3 input
z3test: z3case build
	@echo "Running JUnit tests (Z3 input)..."
	@java -Dsource=z3 -jar $(JUNIT_JAR) \
		-cp $(BUILD_DIR) \
		--scan-class-path || (echo "❌ Some Z3 tests failed." && exit 1)
	@echo "✅ All Z3 tests completed."

# Mutation Testing (Z3)
z3mutation: build
	@echo "Running PIT mutation testing (Z3 input)..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-z3 \
		--targetClasses DateHelper \
		--targetTests DataHelperTest \
		--sourceDirs . \
		--jvmArgs="-Dsource=z3" || (echo "❌ PITest (Z3) failed." && exit 1)
	@echo "✅ Mutation testing (Z3) completed. Report at pit-report-z3/index.html"

build/mutationScore.class: zSolver/mutationScore.java
	@echo "📦 Compiling mutationScore.java..."
	@echo "[TODO] Compile mutationScore.java"

z3mutationBoost: build build/mutationScore.class
	@echo "Running PIT mutation testing for mutationScore.java..."
	@echo "[TODO] Add Boost PIT command for Z3"

# Jacoco Coverage (Z3)
z3coverage: clean z3case build
	@echo "▶ Running tests with Jacoco agent (Z3 input)..."
	@java -javaagent:../lib/jacocoagent.jar=destfile=jacoco-z3.exec \
		-Dsource=z3 \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || echo "⚠️ Some Z3 tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (Z3)..."
	@java -jar ../lib/jacococli.jar report jacoco-z3.exec \
		--sourcefiles . \
		--classfiles build/DateHelper.class \
		--html jacoco-report-z3 \
		--name "DateHelper Coverage (Z3)"
	@echo "✅ Report generated at jacoco-report-z3/index.html"

build/coverageScore.class: zSolver/coverageScore.java
	@echo "📦 Compiling coverageScore.java..."
	@echo "[TODO] Compile coverageScore.java"

z3coverageBoost: z3case build build/coverageScore.class
	@echo "▶ Running tests with Jacoco agent (Z3 + Boost)..."
	@echo "[TODO] Add Jacoco Boost test execution and report generation for Z3"

# Report Opening Shortcuts (Z3)
z3mutation-report: z3mutation
	@echo "📂 Opening PIT mutation report (Z3) in Edge..."
	@if [ -e pit-report-z3/index.html ]; then \
		powershell.exe /c start pit-report-z3/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make z3mutation' first."; \
	fi

z3mutation-boost-report: z3mutationBoost
	@echo "📂 Opening PIT mutation report (Z3 + Boost) in Edge..."
	@echo "[TODO] Add script to open mutation report (Boost)"

z3coverage-report: z3coverage
	@echo "📂 Opening Jacoco coverage report (Z3) in Edge..."
	@if [ -e jacoco-report-z3/index.html ]; then \
		powershell.exe /c start jacoco-report-z3/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make z3coverage' first."; \
	fi

z3coverage-boost-report: z3coverageBoost
	@echo "📂 Opening Jacoco coverage report (Z3 + Boost) in Edge..."
	@echo "[TODO] Add script to open coverage report (Boost)"

# Shortcut
z3: z3case z3test
