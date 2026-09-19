# STEP-C1 Dynamic Activities

This is a separate, class-style Java project based on the supplied Activities 1-8.

## Structure
Each activity follows:
`activityX/src/com/gdb/domain`
`activityX/src/com/gdb/exceptions` (where needed)
`activityX/src/com/gdb/tests`

## Dynamic tests
The test programs are menu-driven using `Scanner`. They do not rely on the fixed sample account numbers/scenarios from the expected output. You choose the operation and enter values at runtime.

Examples:
- Activity 1/2: create, deposit, withdraw, edit/display.
- Activity 3/4: enhanced account operations, PIN, close/reopen.
- Activity 5/6: exception-based operations with try/catch.
- Activity 8: create Savings/Current accounts, select accounts by number, interest, overdraft, polymorphism, operations.

## Compile/run
From an activity directory:
`javac -d out $(find src -name "*.java")`
On Windows PowerShell, use:
`Get-ChildItem -Recurse src\*.java | ForEach-Object { $_.FullName } | javac -d out ...`
or compile from the `src` tree using your IDE.

Important: Activity 7 is the implementation step; Activity 8 is the dynamic test for subclasses.
