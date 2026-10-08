package com.smartlibrary;

import com.smartlibrary.data.LibraryRepository;
import com.smartlibrary.service.LibraryService;
import com.smartlibrary.service.ReviewerService;
import com.smartlibrary.service.SearchService;
import com.smartlibrary.service.SimilarityService;
import com.smartlibrary.service.UsageService;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class LibraryFlowTest {
    @TempDir Path temp;

    @Test void catalogueSearchPersistenceAndMatchingWorkTogether() {
        LibraryRepository repository = new LibraryRepository(temp);
        LibraryService library = new LibraryService(repository);
        UsageService usage = new UsageService(repository);
        SearchService search = new SearchService(library, usage);

        assertEquals(8, library.all().size());
        assertEquals(2, search.search("machine", SearchService.Mode.KEYWORD).documents().size());
        assertEquals("machine", search.search("machien", SearchService.Mode.KEYWORD).suggestion());
        assertEquals(2, search.search("graphs reveal connections", SearchService.Mode.ABSTRACT).documents().size());
        assertTrue(search.search("climate, networks", SearchService.Mode.MULTI_KEYWORD).documents().size() >= 2);
        assertEquals(4, new ReviewerService().assign(library.all()).size());
        assertTrue(new SimilarityService().compare(library.find(2), library.find(5)).length() >= 24);

        library.add("Paper", "New Study", "Student", 2026, "Data Structures", "A new paper abstract.");
        int newId = library.all().stream().mapToInt(d -> d.id()).max().orElseThrow();
        library.open(newId);
        assertEquals(1, new LibraryService(repository).find(newId).views());
        assertEquals(4, new UsageService(repository).searches());
    }
}
