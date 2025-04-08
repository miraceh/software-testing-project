from z3 import *

s = Solver()

a = String('a')
b = String('b')
c = String('c')

digits = [StringVal(str(d)) for d in range(10)]  # "0" to "9"
digit_set = Or(*[a == d for d in digits])
not_digit_a = Not(Or(*[a == d for d in digits]))
not_digit_b = Not(Or(*[b == d for d in digits]))
not_digit_c = Not(Or(*[c == d for d in digits]))

# At least one of a, b, or c is not a digit
s.add(Or(not_digit_a, not_digit_b, not_digit_c))
s.add(Length(a) >= 1)
s.add(Length(b) >= 1)
s.add(Length(c) >= 1)

if s.check() == sat:
    m = s.model()

    a_val = m[a].as_string()
    b_val = m[b].as_string()
    c_val = m[c].as_string()

    with open("./genCase/z3/input3_NonNum.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")

    print(f"[OK] Wrote input_abc_case_3.txt: a={a_val}, b={b_val}, c={c_val}")
else:
    print("[FAIL] No solution found")
