# SmartLibrary — DSA Student Demo

A small, browser-only digital library demo. It uses a sample collection of books and research papers to show a few DSA ideas in a practical, explainable way.

## Run it

Open `index.html` in a modern browser. There is no installation, backend, or database required.

## Features

- Search item titles, authors, topics, and types with **KMP (Knuth–Morris–Pratt)**.
- Filter the collection to show books or research papers.
- When a search has no results, use **Levenshtein edit distance** to suggest a nearby topic spelling.
- See the number of results and searches made in the current browser session.

## Algorithms in simple terms

### KMP search — M1 / CO1

KMP searches for a keyword in the text attached to each library item. It builds a small prefix table for the keyword. When a mismatch happens, that table tells the search where it can safely continue, so it does not start over from the beginning. For text length `n` and keyword length `m`, the search takes `O(n + m)` time.

### Levenshtein distance — M3 / CO3

This measures how many single-character insertions, deletions, or replacements turn one string into another. The demo checks a query against the collection's topics and offers the closest topic when it is near enough. Comparing strings of lengths `n` and `m` takes `O(n × m)` time.

### Search usage count — M6 / CO6 (basic data collection)

The page keeps simple counters in memory for this session: how many searches were submitted and how many items matched the latest query. Refreshing the page resets them. This is intentionally a small illustration, not persistent analytics.

## Sample collection

The titles, authors, years, and topics are fictional sample data for demonstration. This project does not connect to a real library catalogue.
