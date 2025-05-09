package ir.iau.library.service;

import ir.iau.library.dto.BookExcelRowDto;
import ir.iau.library.dto.PersonExcelRowDto;
import ir.iau.library.entity.Book;
import ir.iau.library.entity.Person;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.repository.PersonRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExcelImportService {
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;
    private final Validator validator;

    public ExcelImportService(BookRepository bookRepository,
                              PersonRepository personRepository,
                              ValidatorFactory validatorFactory) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
        this.validator = validatorFactory.getValidator();
    }

    @Transactional
    public Map<Integer, List<String>> importBooks(MultipartFile file) throws Exception {
        Map<Integer, List<String>> errorMap = new HashMap<>();
        try (InputStream in = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                BookExcelRowDto dto = BookExcelRowDto.builder()
                        .title(getCell(row, 0))
                        .author(getCell(row, 1))
                        .translator(getCell(row, 2))
                        .isbn13(getCell(row, 3))
                        .subject(getCell(row, 4))
                        .publicationDate(parseDate(getCell(row, 5)))
                        .build();

                Set<ConstraintViolation<BookExcelRowDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    List<String> messages = new ArrayList<>();
                    violations.forEach(v -> messages.add(v.getPropertyPath() + ": " + v.getMessage()));
                    errorMap.put(rowIndex + 1, messages); // شماره سطر +1 برای هماهنگی با Excel
                    continue;
                }

                // تبدیل DTO به Entity و ذخیره
                Book book = Book.builder()
                        .title(dto.getTitle())
                        .author(dto.getAuthor())
                        .translator(dto.getTranslator())
                        .isbn13(dto.getIsbn13())
                        .subject(dto.getSubject())
                        .publicationDate(dto.getPublicationDate())
                        .active(true)
                        .build();
                bookRepository.save(book);
            }
        }
        return errorMap;
    }

    @Transactional
    public Map<Integer, List<String>> importPersons(MultipartFile file) throws Exception {
        Map<Integer, List<String>> errorMap = new HashMap<>();
        try (InputStream in = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                PersonExcelRowDto dto = PersonExcelRowDto.builder()
                        .firstName(getCell(row, 0))
                        .lastName(getCell(row, 1))
                        .email(getCell(row, 2))
                        .phone(getCell(row, 3))
                        .birthDate(parseDate(getCell(row, 4)))
                        .build();

                Set<ConstraintViolation<PersonExcelRowDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    List<String> messages = new ArrayList<>();
                    violations.forEach(v -> messages.add(v.getPropertyPath() + ": " + v.getMessage()));
                    errorMap.put(rowIndex + 1, messages);
                    continue;
                }

                Person person = Person.builder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .phone(dto.getPhone())
                        .birthDate(dto.getBirthDate())
                        .membershipDate(LocalDate.now())
                        .membershipType("STANDARD")
                        .active(true)
                        .build();
                personRepository.save(person);
            }
        }
        return errorMap;
    }

    // متد کمکی برای خواندن مقدار سلول به رشته
    private String getCell(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private LocalDate parseDate(String text) {
        if (text == null) return null;
        return LocalDate.parse(text);
    }
}
