.PHONY: z3 clean build test mutate all

# Run all Python scripts under zSolver/
z3:
	@echo "Running all Z3 input generators in zSolver/..."
	@for f in zSolver/*.py; do \
		echo ">>> Running $$f"; \
		python3 $$f || exit 1; \
	done

# Compile Java source and test files
build:
	@echo "Compiling Java sources and tests..."
	@mkdir -p out
	@javac -cp "lib/*" -d out *.java test/*.java || (echo "❌ Compilation failed." && exit 1)
	@echo "✅ Compilation succeeded."

# Run JUnit tests
test: build
	@echo "Running JUnit tests..."
	@java -jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp out \
		--scan-class-path || (echo "❌ Some tests failed." && exit 1)
	@echo "✅ All tests completed."

# Run PITest mutation testing
mutate: build
	@echo "Running PIT mutation testing..."
	@java -cp "lib/*:out" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest \
		--sourceDirs . || (echo "❌ PITest failed." && exit 1)
	@echo "✅ Mutation testing completed. Report available at pit-report/index.html"

coverage: build
	@echo "▶ Running tests with Jacoco agent (even if some fail)..."
	@java -javaagent:lib/jacocoagent.jar=destfile=jacoco.exec \
		-jar lib/junit-platform-console-standalone-1.10.2.jar \
		-cp out \
		--scan-class-path || echo "⚠️ Some tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report for Quadratic..."
	@java -jar lib/jacococli.jar report jacoco.exec \
		--sourcefiles . \
		--classfiles out/Quadratic.class \
		--html jacoco-report \
		--name "Quadratic Coverage"
	@echo "✅ Report generated at jacoco-report/index.html"

# Open PITest HTML report in Edge from WSL
mutation-report:
	@echo "📂 Opening PIT mutation report in Edge..."
	@if [ -e pit-report/index.html ]; then \
		powershell.exe /c start pit-report/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make mutate' first."; \
	fi

# Open Jacoco HTML coverage report in Edge from WSL
coverage-report:
	@echo "📂 Opening Jacoco coverage report in Edge..."
	@if [ -e jacoco-report/index.html ]; then \
		powershell.exe /c start jacoco-report/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make coverage' first."; \
	fi

# Clean generated input files and class files
clean:
	@echo "Cleaning up class files and generated files in genCase/z3/..."
	@rm -f genCase/z3/*
	@rm -f out/*
	@echo "Done."

# Run everything: generate → compile → test
all: z3 test
