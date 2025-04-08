from z3 import *

s = Solver()

a = Real('a')
b = Real('b')
c = Real('c')

BIG = RealVal("99999999999999")
FOUR = RealVal(4)

# Add constraints
s.add(a != 0)
s.add(a < BIG)
s.add(b < BIG)
s.add(c < BIG)

# Discriminant == b² ⇨ b² - 4ac == b² ⇨ 4ac == 0
s.add(b * b - FOUR * a * c == b * b)

if s.check() == sat:
    m = s.model()

    # Extract values of a, b, c
    a_val = m[a].as_decimal(20).replace("?", "")
    b_val = m[b].as_decimal(20).replace("?", "")
    c_val = m[c].as_decimal(20).replace("?", "")
    disc_val = m.eval(b * b - FOUR * a * c).as_decimal(20).replace("?", "")

    # Solve ax^2 + bx + c = 0 using the concrete values of a, b, c
    s2 = Solver()
    x = Real('x')

    a_real = RealVal(a_val)
    b_real = RealVal(b_val)
    c_real = RealVal(c_val)

    s2.add(a_real * x * x + b_real * x + c_real == 0)

    x_solutions = []
    if s2.check() == sat:
        m2 = s2.model()
        x1_expr = m2[x]
        x1_val = x1_expr.as_decimal(20).replace("?", "")
        x_solutions.append(x1_val)
        # Try to find the second root using expression directly
        s2.add(x != x1_expr)
        if s2.check() == sat:
            m3 = s2.model()
            x2_val = m3[x].as_decimal(20).replace("?", "")
            if x2_val != x1_val:
                x_solutions.append(x2_val)

    # Write results to file
    with open("./genCase/z3/input4_ComputeFail.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")
        f.write("x = " + ", ".join(x_solutions) + "\n")

    print(f"[OK] Wrote input4.txt")
    print(f"a = {a_val}\nb = {b_val}\nc = {c_val}\ndiscriminant = {disc_val}")
    print(f"x solutions: {x_solutions}")
else:
    print("[FAIL] No solution found")
