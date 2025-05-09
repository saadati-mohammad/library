package ir.iau.library.controller;

import ir.iau.library.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ExcelImportController {

    private final ExcelImportService importService;

    @PostMapping("/books")
    public ResponseEntity<?> uploadBooks(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("فرمت فایل باید .xlsx باشد");
            }
            Map<Integer, List<String>> errors = importService.importBooks(file);
            if (errors.isEmpty()) {
                return ResponseEntity.ok("تمام کتاب‌ها با موفقیت بارگذاری شدند");
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(errors);
            }
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("خطا در پردازش فایل: " + ex.getMessage());
        }
    }

    @PostMapping("/persons")
    public ResponseEntity<?> uploadPersons(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("فرمت فایل باید .xlsx باشد");
            }
            Map<Integer, List<String>> errors = importService.importPersons(file);
            if (errors.isEmpty()) {
                return ResponseEntity.ok("تمام افراد با موفقیت بارگذاری شدند");
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(errors);
            }
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("خطا در پردازش فایل: " + ex.getMessage());
        }
    }
}