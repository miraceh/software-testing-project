from z3 import *

s = Solver()

a = Real('a')
b = Real('b')
c = Real('c')

# Constraint 1: a ≠ 0
s.add(a != 0)

# Constraint 2: At least one of a, b, or c must be greater than 9999999999999998
BIG = 9999999999999998
s.add(Or(a > BIG, b > BIG, c > BIG))

if s.check() == sat:
    m = s.model()

    a_val = m[a].as_decimal(20).replace("?", "")
    b_val = m[b].as_decimal(20).replace("?", "")
    c_val = m[c].as_decimal(20).replace("?", "")

    # Write values to file
    with open("./genCase/z3/input2_OutofRange.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")

    print(f"[OK] Wrote input_abc_case_2.txt: a={a_val}, b={b_val}, c={c_val}")
else:
    print("[FAIL] No solution found")
