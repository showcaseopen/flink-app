import json
import random
import time

def generate_timestamp():
    current_time = int(time.time())
    five_years_in_seconds = 5 * 365 * 24 * 60 * 60
    random_timestamp = random.randint(current_time - five_years_in_seconds, current_time)
    return str(random_timestamp)

def generate_doc_data(i):
    doc_data = {"key" + str(i): "value" + str(i), "key" + str(i+1): "value" + str(i+1)}
    return json.dumps(doc_data)

def generate_entries(num_entries):
    entries = []
    for i in range(1, num_entries+1, 2):
        entry = {"_timestamp": generate_timestamp(), "__doc_data": generate_doc_data(i)}
        entries.append(entry)
    return entries

def save_to_json_file(entries, file_path):
    with open(file_path, 'w') as f:
        json.dump(entries, f)

def main():
    num_entries = 1000000
    entries = generate_entries(num_entries)
    save_to_json_file(entries, "src/main/resources/dataworkspace/input/input.json")

if __name__ == "__main__":
    main()
