from z3 import *

s = Solver()
a, b, c = Reals('a b c')

BIG = RealVal("9999999")
s.add(a != 0, a < BIG, b < BIG, c < BIG)
s.add(b * b - 4 * a * c < 0)  # Ensure complex roots

if s.check() == sat:
    m = s.model()
    a_val = m[a].as_decimal(20).replace("?", "")
    b_val = m[b].as_decimal(20).replace("?", "")
    c_val = m[c].as_decimal(20).replace("?", "")
    D_val = m.eval(b * b - 4 * a * c).as_decimal(20).replace("?", "")

    a_real = RealVal(a_val)
    b_real = RealVal(b_val)
    c_real = RealVal(c_val)

    s2 = Solver()
    xr, xi = Reals('xr xi')

    real_part = a_real * (xr * xr - xi * xi) + b_real * xr + c_real
    imag_part = a_real * (2 * xr * xi) + b_real * xi

    s2.add(real_part == 0, imag_part == 0)

    solutions = []
    if s2.check() == sat:
        m2 = s2.model()
        xr_val = m2[xr].as_decimal(20).replace("?", "")
        xi_val = m2[xi].as_decimal(20).replace("?", "")

        def format_complex(xr_val, xi_val):
            xr_val = xr_val.lstrip("+")
            xi_val = xi_val.lstrip("+")
            if xi_val.startswith("-"):
                return f"{xr_val} - {xi_val[1:]}i"
            else:
                return f"{xr_val} + {xi_val}i"

        # format original root
        solutions.append(format_complex(xr_val, xi_val))

        # compute conjugate: flip sign manually
        if xi_val.startswith("-"):
            xi_conj = xi_val[1:]  # "-1" → "1"
        else:
            xi_conj = "-" + xi_val  # "1" → "-1"

        solutions.append(format_complex(xr_val, xi_conj))

    with open("./genCase/z3/input7_complex.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")
        if solutions:
            f.write("x = " + ", ".join(solutions) + "\n")

    print(f"[OK] Wrote input7_complex.txt")
    print(f"a = {a_val}\nb = {b_val}\nc = {c_val}\nD = {D_val}")
    print("x complex solutions:", solutions)

else:
    print("[FAIL] No solution found")
