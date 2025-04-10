import os
import re
import cmath  # For complex sqrt support

# Input and output paths
input_file = "./ACTS/actsout.case"
output_dir = "./genCase/acts"
os.makedirs(output_dir, exist_ok=True)

with open(input_file, "r") as f:
    content = f.read()

# Split ACTS configurations
configs = re.split(r'Configuration #\d+:\n', content)[1:]

for idx, config in enumerate(configs, start=1):
    a = b = c = None
    for line in config.strip().splitlines():
        match = re.search(r'\b(a|b|c)=(.+)', line)
        if match:
            var = match.group(1).strip()
            val = match.group(2).strip()
            if var == 'a': a = val
            elif var == 'b': b = val
            elif var == 'c': c = val

    # Write the file
    file_path = os.path.join(output_dir, f"config_{idx}.txt")
    with open(file_path, "w") as f_out:
        f_out.write(f"{a}\n{b}\n{c}\n")

        # Only compute roots when a == 1 (regardless of c)
        try:
            if a == "1":
                a_val, b_val, c_val = float(a), float(b), float(c)
                discriminant = cmath.sqrt(b_val ** 2 - 4 * a_val * c_val)
                x1 = (-b_val + discriminant) / (2 * a_val)
                x2 = (-b_val - discriminant) / (2 * a_val)

                def format_root(x):
                    # Special case: x = ±i
                    if abs(x.real) < 1e-8 and abs(x.imag - 1.0) < 1e-8:
                        return "i"
                    elif abs(x.real) < 1e-8 and abs(x.imag + 1.0) < 1e-8:
                        return "-i"
                    elif abs(x.imag) < 1e-8:
                        return f"{x.real}"
                    else:
                        return f"{x.real} + {x.imag}i" if x.imag > 0 else f"{x.real} - {-x.imag}i"

                # Collapse identical roots
                if abs(x1.real - x2.real) < 1e-8 and abs(x1.imag - x2.imag) < 1e-8:
                    result_line = f"x = {format_root(x1)}"
                else:
                    result_line = f"x = {format_root(x1)}, {format_root(x2)}"

                f_out.write(result_line + "\n")
        except:
            pass  # Skip invalid values silently

print(f"✅ Done. Parsed {len(configs)} configurations with simplified root output.")
