import csv

result = []
if __name__ == "__main__":
    with open("./movies.csv", "r", encoding="utf-8") as f, open("./movies.txt", "w", encoding="utf-8") as fout:
        reader = csv.reader(f)
        headers = next(reader)
        for row in reader:
            tmp_1 = row[1].replace("\"", "")
            tmp_2 = ""
            if tmp_1.find(", The") != -1:
                tmp_2 = "The " + tmp_1.replace(", The", "")
            else:
                tmp_2 = tmp_1
            tmp = row[0] + "|" + tmp_2 + "\n"
            result.append(tmp)
        fout.writelines(result)