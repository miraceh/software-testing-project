from z3 import *
from itertools import product
import random
import os

input_formats = ['default', 'auto', 'yyyy-MM-dd', 'nan']
output_formats = ['dd/MM/yyyy', 'yyyy-MM-dd', 'default']

def format_date(d, m, y, style):
    if style == 'slash':
        return "{:02d}/{:02d}/{:04d}".format(d, m, y)
    elif style == 'dash':
        return "{:04d}-{:02d}-{:02d}".format(y, m, d)

# Fixed input dates (3 entries to reduce size)
fixed_dates = [
    (12, 4, 2024, 'slash'),   # 12/04/2024
    (12, 4, 2024, 'dash'),    # 2024-04-12
    (5, 11, 2025, 'slash')    # 05/11/2025
]

date_strings = []

# Z3 check for valid dates
for d, m, y, style in fixed_dates:
    s = Solver()
    day = Int('day')
    month = Int('month')
    year = Int('year')
    s.add(day == d, month == m, year == y)
    s.add(year >= 1990, year <= 2030)
    s.add(month >= 1, month <= 12)
    s.add(day >= 1, day <= 31)
    s.add(Or(
        And(month == 2, day <= 29),
        And(Or(month == 4, month == 6, month == 9, month == 11), day <= 30),
        And(Or(month == 1, month == 3, month == 5, month == 7,
               month == 8, month == 10, month == 12), day <= 31)
    ))

    if s.check() == sat:
        date_strings.append(format_date(d, m, y, style))

# Build all combinations
all_combinations = list(product(date_strings, input_formats, output_formats))

# Shuffle and sample (limit to 30)
random.seed(42)  # Make deterministic
sampled_combinations = random.sample(all_combinations, min(30, len(all_combinations)))

# Write output
output_file = "./zSolver/z3case.txt"
os.makedirs(os.path.dirname(output_file), exist_ok=True)

with open(output_file, "w") as f:
    for date_str, in_fmt, out_fmt in sampled_combinations:
        f.write(f"{date_str},{in_fmt},{out_fmt}\n")

print(f"✅ Generated {len(sampled_combinations)} combinations in {output_file}")
