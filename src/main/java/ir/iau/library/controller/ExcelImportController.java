package ir.iau.library.controller;

import ir.iau.library.dto.BookFilterDto;
import ir.iau.library.dto.PersonFilterDto;
import ir.iau.library.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/excel-import")
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    @Autowired
    public ExcelImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping("/books")
    public ResponseEntity<String> importBooks(@RequestPart("file") MultipartFile file) {
        try {
            List<BookFilterDto> books = excelImportService.importBooksFromExcel(file);
            return ResponseEntity.ok("Books successfully imported. Total records: " + books.size());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error importing books: " + e.getMessage());
        }
    }

    @PostMapping("/persons")
    public ResponseEntity<String> importPersons(@RequestPart("file") MultipartFile file) {
        try {
            List<PersonFilterDto> persons = excelImportService.importPersonsFromExcel(file);
            return ResponseEntity.ok("Persons successfully imported. Total records: " + persons.size());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error importing persons: " + e.getMessage());
        }
    }
}