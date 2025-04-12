import os
import re

# File paths
input_file = "./ACTS/actsout.case"
output_dir = "./genCase/acts"
os.makedirs(output_dir, exist_ok=True)

# Conversion logic
def convert(input_date, input_format, output_format):
    if "nan" in [input_date, input_format, output_format]:
        return "invalid"

    if input_date == "12/04/2024":
        if input_format == "default":
            if output_format == "yyyy-MM-dd":
                return "2024-04-12"
            elif output_format == "default":
                return "12/04/2024"
        elif input_format == "auto":
            if output_format == "dd/MM/yyyy":
                return "12/04/2024"
            elif output_format == "yyyy-MM-dd":
                return "2024-04-12"
        return "invalid"

    elif input_date == "2024-04-12":
        if input_format == "yyyy-MM-dd":
            if output_format == "default":
                return "12/04/2024"
        elif input_format == "auto":
            if output_format == "dd/MM/yyyy":
                return "12/04/2024"
            elif output_format == "yyyy-MM-dd":
                return "2024-04-12"
        return "invalid"

    return "invalid"

# Parse ACTS output
with open(input_file, "r") as f:
    content = f.read()

configs = re.split(r'Configuration #\d+:\n', content)[1:]

for idx, config in enumerate(configs, start=1):
    input_date = input_format = output_format = None
    for line in config.strip().splitlines():
        match = re.search(r'\d+\s*=\s*(inputDate|inputFormat|outputFormat)=(.+)', line)
        if match:
            var = match.group(1).strip()
            val = match.group(2).strip()
            if var == 'inputDate':
                input_date = val
            elif var == 'inputFormat':
                input_format = val
            elif var == 'outputFormat':
                output_format = val

    result = convert(input_date, input_format, output_format)

    file_path = os.path.join(output_dir, f"config_{idx}.txt")
    with open(file_path, "w") as f_out:
        f_out.write(f"{input_date}\n{input_format}\n{output_format}\n{result}\n")

print(f"✅ Done. Parsed {len(configs)} configurations and generated test inputs.")
