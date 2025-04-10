.PHONY: z3case clean build test mutate all

# Run all Python scripts under zSolver/
z3case:
	@echo "Running all Z3 input generators in zSolver/..."
	@for f in zSolver/*.py; do \
		echo ">>> Running $$f"; \
		python3 $$f || exit 1; \
	done

actscase:
	@echo "▶ Generating ACTS cases with quadratic.txt..."
	@java -jar ACTS/acts_3.3.jar ACTS/quadratic.txt ACTS/actsout.case || (echo "❌ ACTS generation failed." && exit 1)

	@echo "📂 Parsing ACTS output to input files..."
	@python3 ACTS/parserOut.py || (echo "❌ Python parse failed." && exit 1)

	@echo "✅ ACTS test cases generated in genCase/acts/"

# Compile Java source and test files
build:
	@echo "Compiling Java sources and tests..."
	@mkdir -p build
	@javac -cp "lib/*" -d build *.java test/*.java || (echo "❌ Compilation failed." && exit 1)
	@echo "✅ Compilation succeeded."

# Run JUnit tests
z3test: build
	@echo "Running JUnit tests..."
	@java -jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || (echo "❌ Some tests failed." && exit 1)
	@echo "✅ All tests completed."

actstest: build
	@echo "▶ Running JUnit tests with ACTS input files..."
	@java -Dsource=acts \
		-jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || (echo "❌ Some ACTS tests failed." && exit 1)
	@echo "✅ All ACTS-based tests completed."

z3mutate: build
	@echo "Running PIT mutation testing (Z3 input)..."
	@java -cp "lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-z3 \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest \
		--sourceDirs . \
		--jvmArgs="-Dsource=z3" || (echo "❌ PITest (Z3) failed." && exit 1)
	@echo "✅ Mutation testing (Z3) completed. Report at pit-report-z3/index.html"

z3coverage: clean z3case build
	@echo "▶ Running tests with Jacoco agent (Z3 input)..."
	@java -javaagent:lib/jacocoagent.jar=destfile=jacoco-z3.exec \
		-Dsource=z3 \
		-jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || echo "⚠️ Some Z3 tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (Z3)..."
	@java -jar lib/jacococli.jar report jacoco-z3.exec \
		--sourcefiles . \
		--classfiles build/Quadratic.class \
		--html jacoco-report-z3 \
		--name "Quadratic Coverage (Z3)"
	@echo "✅ Report generated at jacoco-report-z3/index.html"

z3mutation-report:
	@echo "📂 Opening PIT mutation report (Z3) in Edge..."
	@if [ -e pit-report-z3/index.html ]; then \
		powershell.exe /c start pit-report-z3/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make z3mutate' first."; \
	fi

z3coverage-report: z3coverage
	@echo "📂 Opening Jacoco coverage report (Z3) in Edge..."
	@if [ -e jacoco-report-z3/index.html ]; then \
		powershell.exe /c start jacoco-report-z3/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make z3coverage' first."; \
	fi

# Run PITest mutation testing for ACTS
actsmutate: build
	@echo "Running PIT mutation testing for ACTS input..."
	@java -cp "lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-acts \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest \
		--sourceDirs . \
		--jvmArgs="-Dsource=acts" || (echo "❌ PITest (ACTS) failed." && exit 1)
	@echo "✅ Mutation testing (ACTS) completed. Report at pit-report-acts/index.html"

# Run Jacoco test coverage for ACTS input
actscoverage: clean actscase build
	@echo "▶ Running tests with Jacoco agent (ACTS input)..."
	@java -javaagent:lib/jacocoagent.jar=destfile=jacoco-acts.exec \
		-Dsource=acts \
		-jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || echo "⚠️ Some ACTS tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (ACTS)..."
	@java -jar lib/jacococli.jar report jacoco-acts.exec \
		--sourcefiles . \
		--classfiles build/Quadratic.class \
		--html jacoco-report-acts \
		--name "Quadratic Coverage (ACTS)"
	@echo "✅ Report generated at jacoco-report-acts/index.html"

# Open PITest HTML report for ACTS input
actsmutation-report:
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


# Clean generated input files and class files
clean:
	@echo "🧹 Cleaning up all generated files and reports..."
	@rm -f genCase/z3/*
	@rm -f genCase/acts/*
	@rm -f build/*
	@rm -f jacoco-*.exec
	@rm -rf jacoco-report-z3 jacoco-report-acts
	@rm -rf pit-report-z3 pit-report-acts
	@echo "✅ Clean complete."

z3: z3case z3test
acts: actscase actstest
all: z3 acts

