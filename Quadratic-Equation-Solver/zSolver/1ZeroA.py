from z3 import *
import random

# Solve a == 0 using Z3
s = Solver()
a = Real('a')
s.add(a == 0)

if s.check() == sat:
    m = s.model()
    a_val = m[a].as_decimal(10).replace("?", "")

    # Randomly generate b and c as floating-point numbers
    b_val = random.uniform(-1e10, 1e10)
    c_val = random.uniform(-1e10, 1e10)

    # Write values to file
    with open("./genCase/z3/input1_zeroA.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")

    print(f"[OK] Wrote input_abc_case_1.txt: a={a_val}, b={b_val}, c={c_val}")
else:
    print("[FAIL] No solution found for a")
