.PHONY: z3case clean build test all

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
	@javac -cp "../lib/*" -d build *.java test/*.java || (echo "❌ Compilation failed." && exit 1)
	@echo "✅ Compilation succeeded."

# Run JUnit tests
z3test: z3case build
	@echo "Running JUnit tests..."
	@java -jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || (echo "❌ Some tests failed." && exit 1)
	@echo "✅ All tests completed."

actstest: actscase build
	@echo "▶ Running JUnit tests with ACTS input files..."
	@java -Dsource=acts \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || (echo "❌ Some ACTS tests failed." && exit 1)
	@echo "✅ All ACTS-based tests completed."

z3mutation: build
	@echo "Running PIT mutation testing (Z3 input)..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-z3 \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest \
		--sourceDirs . \
		--jvmArgs="-Dsource=z3" || (echo "❌ PITest (Z3) failed." && exit 1)
	@echo "✅ Mutation testing (Z3) completed. Report at pit-report-z3/index.html"

build/mutationScore.class: zSolver/mutationScore.java
	@echo "📦 Compiling mutationScore.java..."
	@javac -cp "../lib/*:build" -d build zSolver/mutationScore.java || (echo "❌ mutationScore compilation failed." && exit 1)

z3mutationBoost: build build/mutationScore.class
	@echo "Running PIT mutation testing for mutationScore.java..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-z3-boost \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest,mutationScore \
		--sourceDirs . \
		--jvmArgs="-Dsource=z3" || (echo "❌ PITest (Boost) failed." && exit 1)
	@echo "✅ Mutation testing (mutationScore) completed. Report at pit-report-z3-boost/index.html"

z3coverage: z3case build
	@echo "▶ Running tests with Jacoco agent (Z3 input)..."
	@java -javaagent:../lib/jacocoagent.jar=destfile=jacoco-z3.exec \
		-Dsource=z3 \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--scan-class-path || echo "⚠️ Some Z3 tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (Z3)..."
	@java -jar ../lib/jacococli.jar report jacoco-z3.exec \
		--sourcefiles . \
		--classfiles build/Quadratic.class \
		--html jacoco-report-z3 \
		--name "Quadratic Coverage (Z3)"
	@echo "✅ Report generated at jacoco-report-z3/index.html"

build/coverageScore.class: zSolver/coverageScore.java
	@echo "📦 Compiling coverageScore.java..."
	@javac -cp "../lib/*:build" -d build zSolver/coverageScore.java || (echo "❌ coverageScore compilation failed." && exit 1)

z3coverageBoost: z3case build build/coverageScore.class
	@echo "▶ Running tests with Jacoco agent (Z3 + Boost)..."
	@java -javaagent:../lib/jacocoagent.jar=destfile=jacoco-z3-boost.exec \
		-Dsource=z3 \
		-jar ../lib/junit-platform-console-standalone-1.10.2.jar \
		-cp build \
		--select-class=QuadraticParamTest --select-class=coverageScore || echo "⚠️ Some Z3 tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (Z3 + Boost)..."
	@java -jar ../lib/jacococli.jar report jacoco-z3-boost.exec \
		--sourcefiles . \
		--classfiles build/Quadratic.class \
		--html jacoco-report-z3-boost \
		--name "Quadratic Coverage (Z3 + Boost)"
	@echo "✅ Report generated at jacoco-report-z3-boost/index.html"

z3mutation-report: z3mutation
	@echo "📂 Opening PIT mutation report (Z3) in Edge..."
	@if [ -e pit-report-z3/index.html ]; then \
		powershell.exe /c start pit-report-z3/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make z3mutation' first."; \
	fi

z3mutation-boost-report: z3mutationBoost
	@echo "📂 Opening PIT mutation report (Z3) in Edge..."
	@if [ -e pit-report-z3-boost/index.html ]; then \
		powershell.exe /c start pit-report-z3-boost/index.html; \
	else \
		echo "❌ PIT report not found. Please run 'make z3mutation' first."; \
	fi

z3coverage-report: z3coverage
	@echo "📂 Opening Jacoco coverage report (Z3) in Edge..."
	@if [ -e jacoco-report-z3/index.html ]; then \
		powershell.exe /c start jacoco-report-z3/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make z3coverage' first."; \
	fi

z3coverage-boost-report: z3coverageBoost
	@echo "📂 Opening Jacoco coverage report (Z3) in Edge..."
	@if [ -e jacoco-report-z3-boost/index.html ]; then \
		powershell.exe /c start jacoco-report-z3-boost/index.html; \
	else \
		echo "❌ Jacoco report not found. Please run 'make z3coverageBoost' first."; \
	fi

# Run PITest mutation testing for ACTS
actsmutate: build
	@echo "Running PIT mutation testing for ACTS input..."
	@java -cp "../lib/*:build" org.pitest.mutationtest.commandline.MutationCoverageReport \
		--reportDir pit-report-acts \
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest \
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
		--targetClasses Quadratic \
		--targetTests QuadraticParamTest,ActsMutationScore \
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
		--classfiles build/Quadratic.class \
		--html jacoco-report-acts \
		--name "Quadratic Coverage (ACTS)"
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
		--select-class=QuadraticParamTest \
		--select-class=ActsCoverageScore || echo "⚠️ Some ACTS tests failed, but coverage will still be reported."

	@echo "📊 Generating Jacoco HTML report (ACTS + Boost)..."
	@java -jar ../lib/jacococli.jar report jacoco-acts-boost.exec \
		--sourcefiles . \
		--classfiles build/Quadratic.class \
		--html jacoco-report-acts-boost \
		--name "Quadratic Coverage (ACTS + Boost)"
	@echo "✅ Report generated at jacoco-report-acts-boost/index.html"


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


# Clean generated input files and class files
clean:
	@echo "🧹 Cleaning up all generated files and reports..."
	@rm -f ACTS/actsout.case
	@rm -f genCase/z3/*
	@rm -f genCase/acts/*
	@rm -f build/*
	@rm -f jacoco-*.exec
	@rm -rf jacoco-report-z3 jacoco-report-z3-boost jacoco-report-acts jacoco-report-acts-boost
	@rm -rf pit-report-z3 pit-report-z3-boost pit-report-acts pit-report-acts-boost
	@echo "✅ Clean complete."

z3: z3case z3test
acts: actscase actstest
all: z3 acts

