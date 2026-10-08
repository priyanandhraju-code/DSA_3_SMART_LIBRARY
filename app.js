const books = [
  { title: 'The Shape of Data', author: 'Mira Patel', type: 'Book', topic: 'Data Structures', color: 'green', year: 2023 },
  { title: 'Learning Machines', author: 'Jonas Lee', type: 'Book', topic: 'Machine Learning', color: 'rust', year: 2022 },
  { title: 'A Field Guide to Climate', author: 'Nora Williams', type: 'Book', topic: 'Climate Science', color: 'blue', year: 2021 },
  { title: 'The Quiet Internet', author: 'Sam Rivera', type: 'Book', topic: 'Computer Networks', color: 'plum', year: 2024 },
  { title: 'Small Models, Big Ideas', author: 'A. Chen et al.', type: 'Paper', topic: 'Machine Learning', color: 'gold', year: 2024 },
  { title: 'Patterns in a Warming World', author: 'E. Okafor et al.', type: 'Paper', topic: 'Climate Science', color: 'green', year: 2023 },
  { title: 'Making Search More Helpful', author: 'R. Singh et al.', type: 'Paper', topic: 'Information Retrieval', color: 'ink', year: 2022 },
  { title: 'Networks in Nature', author: 'L. Morgan et al.', type: 'Paper', topic: 'Data Structures', color: 'blue', year: 2021 }
];

const topics = [...new Set(books.map((book) => book.topic.toLowerCase()))];
const form = document.querySelector('#search-form');
const input = document.querySelector('#search-input');
const grid = document.querySelector('#book-grid');
const count = document.querySelector('#search-count');
const suggestion = document.querySelector('#suggestion');
const emptyState = document.querySelector('#empty-state');
let activeFilter = 'All';
let currentQuery = '';
let searches = 0;

// KMP prefix table: longest proper prefix that is also a suffix at each position.
function buildLps(pattern) {
  const lps = new Array(pattern.length).fill(0);
  let length = 0;
  let i = 1;
  while (i < pattern.length) {
    if (pattern[i] === pattern[length]) lps[i++] = ++length;
    else if (length > 0) length = lps[length - 1];
    else lps[i++] = 0;
  }
  return lps;
}

// Knuth-Morris-Pratt substring search, case-insensitive at the call site.
function kmpContains(text, pattern) {
  if (pattern.length === 0) return true;
  const lps = buildLps(pattern);
  let i = 0;
  let j = 0;
  while (i < text.length) {
    if (text[i] === pattern[j]) { i++; j++; }
    if (j === pattern.length) return true;
    if (i < text.length && text[i] !== pattern[j]) {
      if (j > 0) j = lps[j - 1];
      else i++;
    }
  }
  return false;
}

// Levenshtein edit distance using one row of memory.
function editDistance(a, b) {
  let previous = Array.from({ length: b.length + 1 }, (_, i) => i);
  for (let i = 1; i <= a.length; i++) {
    const current = [i];
    for (let j = 1; j <= b.length; j++) {
      const cost = a[i - 1] === b[j - 1] ? 0 : 1;
      current[j] = Math.min(current[j - 1] + 1, previous[j] + 1, previous[j - 1] + cost);
    }
    previous = current;
  }
  return previous[b.length];
}

function findSuggestion(query) {
  if (!query) return '';
  let closest = null;
  let bestDistance = Infinity;
  for (const topic of topics) {
    const distance = editDistance(query, topic);
    if (distance < bestDistance) { closest = topic; bestDistance = distance; }
  }
  return bestDistance <= Math.max(2, Math.floor(query.length * 0.35)) ? closest : '';
}

function render() {
  const query = currentQuery.trim().toLowerCase();
  const results = books.filter((book) => {
    const searchable = `${book.title} ${book.author} ${book.topic} ${book.type}`.toLowerCase();
    return (activeFilter === 'All' || book.type === activeFilter) && (!query || kmpContains(searchable, query));
  });

  grid.innerHTML = results.map((book) => `
    <article class="book-card">
      <div class="cover ${book.color}"><span class="cover-title">${book.title}</span><span class="cover-author">${book.author.toUpperCase()}</span></div>
      <div class="card-info"><span class="type-label">${book.type} · ${book.year}</span><h3>${book.title}</h3><p>by ${book.author}</p><span class="card-topic">${book.topic}</span></div>
    </article>`).join('');

  count.textContent = query ? `${results.length} result${results.length === 1 ? '' : 's'} · ${searches} search${searches === 1 ? '' : 'es'} this session` : `Showing all ${books.length} items · ${searches} search${searches === 1 ? '' : 'es'} this session`;
  emptyState.hidden = results.length > 0;
  suggestion.replaceChildren();
  if (query && results.length === 0) {
    const closeTopic = findSuggestion(query);
    if (closeTopic) {
      suggestion.append('No exact matches. Did you mean ');
      const button = document.createElement('button');
      button.type = 'button';
      button.textContent = closeTopic;
      button.addEventListener('click', () => { input.value = closeTopic; currentQuery = closeTopic; render(); });
      suggestion.append(button, '?');
    }
  }
}

form.addEventListener('submit', (event) => {
  event.preventDefault();
  currentQuery = input.value;
  searches++;
  render();
  document.querySelector('#catalog').scrollIntoView({ behavior: 'smooth', block: 'start' });
});

document.querySelectorAll('.filter').forEach((button) => {
  button.addEventListener('click', () => {
    activeFilter = button.dataset.filter;
    document.querySelectorAll('.filter').forEach((filter) => filter.classList.toggle('active', filter === button));
    render();
  });
});

render();
