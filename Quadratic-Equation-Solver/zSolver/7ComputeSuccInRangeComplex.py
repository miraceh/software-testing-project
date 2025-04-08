from z3 import *

# Set up the solver for the equation
s = Solver()

a = Real('a')
b = Real('b')
c = Real('c')

BIG = RealVal("9999999")
FOUR = RealVal(4)

# Add some constraints
s.add(a != 0)
s.add(a < BIG)
s.add(b < BIG)
s.add(c < BIG)
s.add(b * b - FOUR * a * c < 0)  # Ensure complex roots (discriminant < 0)

if s.check() == sat:
    m = s.model()

    # Extract the concrete values of a, b, c
    a_val = m[a].as_decimal(20).replace("?", "")
    b_val = m[b].as_decimal(20).replace("?", "")
    c_val = m[c].as_decimal(20).replace("?", "")
    diff_val = m.eval(b * b - FOUR * a * c).as_decimal(20).replace("?", "")

    # Create a new solver to solve for complex roots
    s2 = Solver()

    # Complex solution: x = xr + xi * i
    xr = Real('xr')  # Real part
    xi = Real('xi')  # Imaginary part

    # Construct the complex equation: (xr + xi*i)^2 + (b/a)(xr + xi*i) + c/a = 0
    # That is: a*(x^2) + b*x + c == 0
    # Expand: (xr + i*xi)^2 = (xr^2 - xi^2) + i*2*xr*xi
    # So real part: a*(xr^2 - xi^2) + b*xr + c == 0
    #     imag part: a*(2*xr*xi) + b*xi == 0

    a_real = RealVal(a_val)
    b_real = RealVal(b_val)
    c_real = RealVal(c_val)

    # Real part == 0
    real_part = a_real * (xr * xr - xi * xi) + b_real * xr + c_real
    # Imaginary part == 0
    imag_part = a_real * (2 * xr * xi) + b_real * xi

    s2.add(real_part == 0)
    s2.add(imag_part == 0)

    solutions = []
    if s2.check() == sat:
        m2 = s2.model()
        xr_val = m2[xr].as_decimal(20).replace("?", "")
        xi_val = m2[xi].as_decimal(20).replace("?", "")
        solutions.append(f"{xr_val} + {xi_val}i")
        solutions.append(f"{xr_val} - {xi_val}i")  # Conjugate root

    # Write to file
    with open("./genCase/z3/input7_complex.txt", 'w') as f:
        f.write(f"{a_val}\n{b_val}\n{c_val}\n")
        f.write("x = " + ", ".join(solutions) + "\n")

    print(f"[OK] Wrote input7_complex.txt")
    print(f"a = {a_val}\nb = {b_val}\nc = {c_val}\nb² - 4ac = {diff_val}")
    print(f"x complex solutions: {solutions}")
else:
    print("[FAIL] No solution found")
