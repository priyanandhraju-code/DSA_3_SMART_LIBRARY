# SmartLibrary — Java DSA Hackathon Project

SmartLibrary is a **Java desktop application** for browsing a digital library, comparing research documents, assigning reviewers, and exploring usage. It starts with eight fictional books and papers. You can add, edit, delete, and open documents; changes are saved locally.

## Run

Install JDK 17 or newer and Maven. From the project folder:

```powershell
mvn package
java -jar target/smartlibrary-1.0.0.jar
```

For development, `mvn compile exec:java` also launches the app. Run `mvn test` for the algorithm and application-flow checks.

The first run creates a `data/` folder with the sample catalogue and usage information. This folder is ignored by Git so each user's demo data stays local. To reset the sample data, close the app and delete the `data/` folder.

## Features and DSA mapping

| Module | Feature shown in the app | Suitable syllabus topic |
| --- | --- | --- |
| Catalogue & Search | Search title, author, and topic | KMP (M1) |
| Catalogue & Search | Search inside abstracts | Rabin–Karp (M1) |
| Catalogue & Search | Search several comma-separated keywords at once | Aho–Corasick (M1) |
| Catalogue & Search | Suggest a close word after an unsuccessful search | Levenshtein distance (M3) |
| Similarity | Find the longest phrase shared by two abstracts | Suffix array + Kasai LCP (M2) |
| Similarity | Flag documents covering all similar-document pairs | Vertex Cover 2-approximation (M5) |
| Reviewer Matching | Assign eligible reviewers to research papers, one paper per reviewer | Bipartite matching (M4) |
| Dashboard | Rank documents by views | Randomized QuickSort (M6) |
| Dashboard | Keep a small representative sample of search queries | Reservoir Sampling (M6) |

The similarity screen checks **phrase overlap**, which is useful for identifying documents to inspect. It does not decide plagiarism. The Vertex Cover feature selects documents for human review when a pair shares at least 24 characters. The threshold is a demo setting in `SimilarityService.java`.

## Suggested hackathon walkthrough

1. Open **Catalogue & Search**. Search `machine` with KMP. Then search `machien` to show the spelling suggestion.
2. Select **Abstract phrase (Rabin-Karp)** and search `graphs reveal connections`.
3. Select **Multiple keywords (Aho-Corasick)** and search `climate, networks`.
4. Add a document, open it to increase its view count, then edit it. Restart the app to show that changes persist.
5. In **Similarity**, compare *Learning Machines* and *Small Models, Big Ideas*. Run **Scan similar pairs** to show the document-review set.
6. In **Reviewer Matching**, show automatic paper assignments based on expertise.
7. In **Dashboard**, show the most-viewed ranking and sampled search queries.

## Project layout

```text
src/main/java/com/smartlibrary/
  Main.java                     application entry point
  algorithm/                    nine DSA implementations
  data/LibraryRepository.java   local file persistence
  model/                        Document and Reviewer
  service/                      catalogue, search, similarity, reviews, usage
  ui/                           Swing screens and document form
pom.xml                         Maven build
```

The app uses only Java's standard library at runtime. Maven compiles the source and launches the Swing interface. Search counts, sample queries, documents, and view counts are saved on the local computer. The titles and authors are fictional demonstration data.

## Complexity at a glance

| Algorithm | Typical time |
| --- | --- |
| KMP | `O(n + m)` per text |
| Rabin–Karp | Average `O(n + m)`; worst `O(nm)` with repeated hash matches |
| Aho–Corasick | `O(text length + total keyword length + matches)` |
| Levenshtein | `O(nm)` time and `O(m)` space per word comparison |
| Suffix array + Kasai LCP | `O(n log² n)` construction with prefix doubling and sorting; `O(n)` LCP |
| Bipartite matching | `O(VE)` with augmenting paths |
| Vertex Cover 2-approximation | `O(E)` after similarity edges are built |
| Randomized QuickSort | Expected `O(n log n)`; worst `O(n²)` |
| Reservoir Sampling | `O(1)` expected work and `O(k)` memory per new query |

Here `n` and `m` usually refer to text/query lengths, `V` and `E` to graph vertices/edges, and `k` to the sample size. The collection is intentionally small so every result can be explained live.
