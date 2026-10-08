# SmartLibrary — Java DSA Student Project

SmartLibrary is a small **Java console application** for a sample digital library. It includes eight fictional books and research papers. It needs no database, web server, or external libraries.

## Requirements and run commands

Use JDK 17 or newer. In PowerShell opened at the project folder:

```powershell
javac -d out (Get-ChildItem src/smartlibrary/*.java).FullName
java -cp out smartlibrary.SmartLibrary
```

## What you can demonstrate

1. **List documents** — see all eight sample items.
2. **Search title, author, or topic** — KMP finds a keyword or phrase. Try `machine`.
3. **Search abstracts** — Rabin–Karp finds a phrase using a rolling hash. Try `graphs`.
4. **Open a document** — enter its ID to read the abstract and increase its view count.
5. **Rank by views** — Randomized QuickSort orders documents by popularity.
6. **Show usage statistics** — see searches made in this run and total sample views.

If a one-word search finds nothing, Levenshtein distance suggests a close word from the catalogue. Try `machien`.

## Algorithms used

| Topic from syllabus | Where used | Simple explanation | Time complexity |
| --- | --- | --- | --- |
| KMP (M1) | Title, author, and topic search | A prefix table avoids restarting the keyword search after a mismatch. | O(n + m) per document |
| Rabin–Karp (M1) | Abstract search | A rolling hash checks each text window. Matching hashes are verified character by character. | Average O(n + m) per document; worst O(nm) |
| Levenshtein distance (M3) | Typo suggestion | Dynamic programming counts insertions, deletions, and replacements needed to change one word into another. | O(nm) time, O(m) space per comparison |
| Randomized QuickSort (M6) | Most viewed ranking | A random pivot partitions documents by their view counts. | Expected O(n log n), worst O(n²) |

Here, `n` and `m` are the lengths of the searched text and query, except for QuickSort where `n` is the number of documents.

## Project structure

- `src/smartlibrary/Document.java` — library item data.
- `src/smartlibrary/Algorithms.java` — the four DSA algorithms.
- `src/smartlibrary/SmartLibrary.java` — sample data and console menu.

The catalogue and starting view counts are fictional. Searches and views exist only in memory and reset when the program restarts. This keeps the project small enough to explain in a class demo.
