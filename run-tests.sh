#!/bin/bash
# run-tests.sh - Run tests with formatted table output

echo ""
echo "╔════════════════════════════════════════════════════════════════════════════╗"
echo "║                         🧪  WORKSHOP TEST SUITE                            ║"
echo "╚════════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "Running tests..."
echo ""

# Run tests and capture output
TMPFILE=$(mktemp)
./mvnw test 2>&1 | tee "$TMPFILE"
EXIT_CODE=${PIPESTATUS[0]}

echo ""
echo "┌──────────────────────────────────────────────────┬───────┬────────┬────────┬─────────┐"
echo "│ Status │ Test Class                              │ Tests │ Passed │ Failed │ Skipped │"
echo "├──────────────────────────────────────────────────┼───────┼────────┼────────┼─────────┤"

# Parse results from temp file
grep -E "Tests run:.*in " "$TMPFILE" | while IFS= read -r line; do
    # Extract class name
    class=$(echo "$line" | grep -oE "in [a-zA-Z0-9._()]*$" | sed 's/in //')
    
    # Extract metrics
    tests=$(echo "$line" | grep -oE "Tests run: [0-9]+" | grep -oE "[0-9]+")
    failures=$(echo "$line" | grep -oE "Failures: [0-9]+" | grep -oE "[0-9]+")
    errors=$(echo "$line" | grep -oE "Errors: [0-9]+" | grep -oE "[0-9]+")
    skipped=$(echo "$line" | grep -oE "Skipped: [0-9]+" | grep -oE "[0-9]+")
    
    # Skip if no tests
    if [ -z "$tests" ] || [ "$tests" = "0" ]; then
        continue
    fi
    
    # Calculate passed
    total_failed=$((failures + errors))
    passed=$((tests - total_failed - skipped))
    
    # Shorten class name
    short_class=$(echo "$class" | sed 's/com.bootstrap.workshop.//')
    
    # Status text (no emoji for alignment)
    if [ "$total_failed" -eq 0 ]; then
        status="PASS"
    else
        status="FAIL"
    fi
    
    # Print row with proper alignment
    printf "│   %-4s │ %-39s │ %5d │ %6d │ %6d │ %7d │\n" "$status" "$short_class" "$tests" "$passed" "$total_failed" "$skipped"
done

echo "└──────────────────────────────────────────────────┴───────┴────────┴────────┴─────────┘"

# Calculate totals from file
TOTAL_TESTS=$(grep -E "Tests run:.*in " "$TMPFILE" | grep -oE "Tests run: [0-9]+" | grep -oE "[0-9]+" | awk '{sum+=$1} END {print sum}')
TOTAL_FAILED=$(grep -E "Tests run:.*in " "$TMPFILE" | grep -oE "Failures: [0-9]+" | grep -oE "[0-9]+" | awk '{sum+=$1} END {print sum}')
TOTAL_ERRORS=$(grep -E "Tests run:.*in " "$TMPFILE" | grep -oE "Errors: [0-9]+" | grep -oE "[0-9]+" | awk '{sum+=$1} END {print sum}')
TOTAL_SKIPPED=$(grep -E "Tests run:.*in " "$TMPFILE" | grep -oE "Skipped: [0-9]+" | grep -oE "[0-9]+" | awk '{sum+=$1} END {print sum}')
TOTAL_PASSED=$((TOTAL_TESTS - TOTAL_FAILED - TOTAL_ERRORS - TOTAL_SKIPPED))

echo ""
echo "┌────────────────────────────────────────────────────────────────────────────────┐"
echo "│                                  SUMMARY                                       │"
echo "├────────────────────────────────────────────────────────────────────────────────┤"
printf "│    Total: %3d       Passed: %3d       Failed: %3d       Skipped: %3d           │\n" "$TOTAL_TESTS" "$TOTAL_PASSED" "$((TOTAL_FAILED + TOTAL_ERRORS))" "$TOTAL_SKIPPED"
echo "└────────────────────────────────────────────────────────────────────────────────┘"
echo ""

# Cleanup
rm -f "$TMPFILE"

if [ $EXIT_CODE -eq 0 ]; then
    echo "================================================================================"
    echo "                            ALL TESTS PASSED!                                   "
    echo "================================================================================"
else
    echo "================================================================================"
    echo "                            SOME TESTS FAILED                                   "
    echo "================================================================================"
fi
echo ""
