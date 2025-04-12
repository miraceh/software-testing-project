import os
import re
from datetime import datetime

# Input file path
input_file = "./zSolver/z3case.txt"
output_dir = "./genCase/z3"
os.makedirs(output_dir, exist_ok=True)

# 日期转换逻辑
def convert(input_date, input_format, output_format):
    if "nan" in [input_date, input_format, output_format]:
        return "invalid"

    try:
        # 解析 inputDate
        if input_format == "default":
            dt = datetime.strptime(input_date, "%d/%m/%Y")
        elif input_format == "yyyy-MM-dd":
            dt = datetime.strptime(input_date, "%Y-%m-%d")
        elif input_format == "auto":
            try:
                dt = datetime.strptime(input_date, "%d/%m/%Y")
            except:
                dt = datetime.strptime(input_date, "%Y-%m-%d")
        else:
            return "invalid"

        # 生成输出
        if output_format == "default":
            return dt.strftime("%d/%m/%Y")
        elif output_format == "yyyy-MM-dd":
            return dt.strftime("%Y-%m-%d")
        elif output_format == "dd/MM/yyyy":
            return dt.strftime("%d/%m/%Y")
        else:
            return "invalid"
    except:
        return "invalid"

# 按行解析 z3case.txt
with open(input_file, "r") as f:
    lines = f.readlines()

for idx, line in enumerate(lines, start=1):
    parts = line.strip().split(",")
    if len(parts) != 3:
        continue

    input_date, input_format, output_format = parts
    result = convert(input_date, input_format, output_format)

    # 写入测试用例文件
    file_path = os.path.join(output_dir, f"z3_{idx:03d}.txt")
    with open(file_path, "w") as f_out:
        f_out.write(f"{input_date}\n{input_format}\n{output_format}\n{result}\n")

print(f"✅ Done. Parsed {len(lines)} lines and generated test inputs in {output_dir}")
