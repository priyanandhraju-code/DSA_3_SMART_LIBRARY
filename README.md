# SmartLibrary — Eclipse Java DSA Hackathon Project

SmartLibrary is a **plain Java Swing desktop application** for a sample digital library. It has no Maven build, server, database, or external runtime libraries. The project is ready to import and run in Eclipse with Java 17 or newer.

## Run in Eclipse

1. Open Eclipse and choose **File → Import → General → Existing Projects into Workspace**.
2. Select the root directory `D:\guru` (or the folder where you cloned this repository).
3. Select **SmartLibrary** and click **Finish**.
4. In Package Explorer, open `src/com/smartlibrary/Main.java`.
5. Right-click `Main.java` → **Run As → Java Application**.

If Eclipse asks for a JRE, select an installed JDK 17 or newer. The app opens in a desktop window with four tabs. Eclipse compiles into `bin/` automatically.

## Features and DSA mapping

| Screen | Feature | Syllabus topic |
| --- | --- | --- |
| Catalogue & Search | Search titles, authors, and topics | KMP (M1) |
| Catalogue & Search | Search abstracts | Rabin–Karp (M1) |
| Catalogue & Search | Search comma-separated keywords together | Aho–Corasick (M1) |
| Catalogue & Search | Suggest a close spelling after no results | Levenshtein distance (M3) |
| Similarity | Find the longest phrase shared by two abstracts | Suffix array + Kasai LCP (M2) |
| Similarity | Choose documents to inspect across similar pairs | Vertex Cover 2-approximation (M5) |
| Reviewer Matching | Assign eligible reviewers to papers | Bipartite matching (M4) |
| Dashboard | Rank documents by view count | Randomized QuickSort (M6) |
| Dashboard | Keep a sample of search queries | Reservoir Sampling (M6) |

Documents, view counts, and search usage are saved as **local files** in `data/`. This is just file storage inside the desktop app; it does not use a backend service. The folder is ignored by Git. For a fresh demo, close the app and delete `data/`.

## Suggested live demo

1. Search `machine` in **Keyword (KMP)** mode. Search `machien` to show the correction suggestion.
2. Search `graphs reveal connections` in **Abstract phrase (Rabin-Karp)** mode.
3. Search `climate, networks` in **Multiple keywords (Aho-Corasick)** mode.
4. Add, edit, and open a document. Opening increases its view count; restarting the app shows that it was saved.
5. Compare *Learning Machines* and *Small Models, Big Ideas* under **Similarity**, then scan similar pairs.
6. Show paper assignments under **Reviewer Matching** and popularity under **Dashboard**.

## Source layout

```text
src/com/smartlibrary/
  Main.java              Run this class in Eclipse
  algorithm/             Nine DSA implementations
  data/                  Local file persistence
  model/                 Document and Reviewer
  service/               Library features
  ui/                    Swing screens and forms
```

The eight starting titles and all reviewer names are fictional sample data. Phrase overlap is a review signal, not a plagiarism decision. The similarity threshold is 24 shared characters so the example pairs are easy to demonstrate.
