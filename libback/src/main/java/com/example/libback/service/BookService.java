package com.example.libback.service;

import com.example.libback.model.Accession;
import com.example.libback.model.Item;
import com.example.libback.dto.BookSearchResultDto;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final ItemRepository itemRepository;
    private final AccessionRepository accessionRepository;

    public BookService(ItemRepository itemRepository, AccessionRepository accessionRepository) {
        this.itemRepository = itemRepository;
        this.accessionRepository = accessionRepository;
    }

    // NEW METHOD: Formats and processes search results specifically for public-search layout
    public List<BookSearchResultDto> searchCatalogWithCounts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return accessionRepository.searchCatalog(query.trim());
    }

    // Keep your original entity search for alternative views if needed
    public List<Item> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        return itemRepository.searchByTitle(query);
    }

    @Transactional
    public void saveBook(Item item) {
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be blank.");
        }
        itemRepository.save(item);
    }

    @Transactional
    public void generateBatchAccessions(String isbn, String prefix, int quantity, String shelfLocation) {
        Item item = itemRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not found."));

        long existingCount = accessionRepository.countByItemIsbn(isbn);
        int nextCopyNumber = (int) existingCount + 1;

        for (int i = 0; i < quantity; i++) {
            Accession copy = new Accession();
            String formattedId = prefix + "-" + String.format("%04d", nextCopyNumber);
            
            copy.setAccessionId(formattedId);
            copy.setBarcode("BAR-" + formattedId); 
            copy.setCopyNumber(nextCopyNumber);
            copy.setShelfLocation(shelfLocation);
            copy.setReplacementCost(BigDecimal.valueOf(20.00)); 
            copy.setPurchaseDate(LocalDate.now());

            item.addAccession(copy);
            accessionRepository.save(copy);
            
            nextCopyNumber++;
        }
        itemRepository.save(item);
    }
}