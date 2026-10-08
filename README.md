# SmartLibrary — Java DSA Hackathon Project

SmartLibrary is a **pure Java console application** for a sample digital library. Run it in Eclipse's Console view. There is no frontend page, Swing window, server, database, Maven dependency, or external library.

## Run in Eclipse

1. Choose **File → Import → General → Existing Projects into Workspace**.
2. Select the repository folder and import **SmartLibrary**.
3. Set the project JRE to **Java 17 or newer** if Eclipse asks.
4. Open `src/com/smartlibrary/Main.java` and choose **Run As → Java Application**.
5. Use the numbered menu in Eclipse's **Console** tab.

The first run creates a local `data/` folder in the project directory with the sample catalogue. Only this top-level runtime folder is ignored by Git; the Java source package `src/com/smartlibrary/data/` is included in the repository.

## What the program does

| Menu feature | Suitable DSA topic |
| --- | --- |
| Search title, author, and topic | KMP (M1) |
| Search a phrase in abstracts | Rabin–Karp (M1) |
| Search several keywords in one pass | Aho–Corasick (M1) |
| Suggest a corrected spelling | Levenshtein distance (M3) |
| Compare abstracts by longest common phrase | Suffix array + Kasai LCP (M2) |
| Identify documents to review from similar pairs | Vertex Cover 2-approximation (M5) |
| Assign research papers to reviewers | Bipartite matching (M4) |
| Rank documents by views | Randomized QuickSort (M6) |
| Sample search queries | Reservoir Sampling (M6) |

The menu also lets you browse, add, edit, open, and delete documents. Documents, view counts, and usage counters are saved as local files. The eight starting titles and all reviewers are fictional sample data.

## Quick demo sequence

1. Choose **2 → 1** and search `machine`; two matching documents appear.
2. Choose **2 → 1** and search `machien`; the program suggests `machine`.
3. Choose **2 → 2** and search `graphs reveal connections`.
4. Choose **2 → 3** and search `climate, networks`.
5. Choose **7** and compare document IDs `2` and `5`.
6. Choose **8** to scan similar pairs, **9** to assign reviewers, and **10** for the usage dashboard.

The similarity check reports phrase overlap to help decide what to inspect. It is not a plagiarism verdict. A pair is flagged when the abstracts share at least 24 characters.

## Source layout

```text
src/com/smartlibrary/
  Main.java               Eclipse entry point
  ConsoleApp.java         Numbered demo menu
  algorithm/              DSA implementations
  data/                   LibraryRepository.java (local file storage)
  model/                  Document and Reviewer
  service/                Search, library, similarity, reviewers, usage
```

The project uses Java 17 language features. To reset the demo, close the program and delete the top-level `data/` folder. Do not delete `src/com/smartlibrary/data/` because it contains a Java source file.
