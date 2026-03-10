import sys

def check_braces(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    stack = []
    line_num = 1
    col_num = 1
    
    for i, char in enumerate(content):
        if char == '{':
            stack.append((line_num, col_num))
        elif char == '}':
            if not stack:
                print(f"Extra closing brace at line {line_num}, column {col_num}")
            else:
                stack.pop()
        
        if char == '\n':
            line_num += 1
            col_num = 1
        else:
            col_num += 1
            
    if stack:
        for ln, cn in stack:
            print(f"Unclosed opening brace at line {ln}, column {cn}")
    else:
        print("All braces are balanced.")

if __name__ == "__main__":
    check_braces(sys.argv[1])
